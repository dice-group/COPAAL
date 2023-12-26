import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class SparqlService {

  constructor(private http: HttpClient) { }

  /**
   * Executes SPARQL queries to get the data for the URIs.
   *
   * @param {string[]} resourceUris - an array of resource URIs for which thumbnail information is requested.
   * @returns {Observable<string[]>} an Observable emitting an array of thumbnail information for each resource URI.
   */
  executeThumbnailQueries(resourceUris: string[]): Observable<string[]> {
    // SPARQL endpoint for querying
    const sparqlEndpoint = 'https://dbpedia.org/sparql';
    return this.http.get<any[]>(sparqlEndpoint, {
      // Make an HTTP GET request to the SPARQL endpoint with the query generated
      params: new HttpParams().set('query', this.buildSparqlQuery(resourceUris)),
    }).pipe(
      map(data => this.extractResults(data))
    );
  }

  /**
   * Extracts and returns results from the SPARQL query response.
   *
   * @param {any} data - the raw data received from the SPARQL query.
   * @returns {any[]} an array of results extracted from the SPARQL query response.
   * this function will be useful if we need to change the format of the data.
   */
  private extractResults(data: any): any[] {
    return data;
  }

  /**
   * Builds a SPARQL query based on the URIs.
   *
   * @param {string[]} resourceUris - an array of URIs.
   * @returns {string} the generated SPARQL query string.
   */
  private buildSparqlQuery(resourceUris: string[]): string {
    if (resourceUris.length === 0) {
      return '';
    }
    // generate SPARQL queries for each resource URI
    const queries = resourceUris.map(uri => {
      const trimmedUri = uri.trim();
      // check if the URI is valid or if it is a literal
      if (this.isValidUri(trimmedUri) || this.isLiteralWithDatatype(trimmedUri)) {
        return `
          {
            <${trimmedUri}> <http://dbpedia.org/ontology/thumbnail> ?f .
            <${trimmedUri}> <http://www.w3.org/2000/01/rdf-schema#label> ?l .
            FILTER(LANG(?l)="en")
          }`;
      } else {
        console.error(`Invalid URI: ${trimmedUri}`);
        // Use a placeholder or default value for invalid URIs
        return `
          {
            BIND("${trimmedUri}" as ?f)
            BIND("${trimmedUri}"@en as ?l)
          }`;
      }
    });
    // Combine individual queries into a single SPARQL query
    const sparqlQuery = `
      SELECT ?f ?l
      WHERE {
        ${queries.join(' UNION ')}
      }
      LIMIT 1
    `;
    return sparqlQuery;
  }

  /**
   * checks if a string is a valid URI.
   *
   * @param {string} str - the string to be checked.
   * @returns {boolean} True if the string is a valid URI, false otherwise.
   */
  private isValidUri(str: string): boolean {
    try {
      new URL(str);
      return true;
    } catch (error) {
      return false;
    }
  }

  /**
   * checks if a string represents a literal with datatype.
   *
   * @param {string} str - the string to be checked.
   * @returns {boolean} True if the string represents a literal, false otherwise.
   */
  private isLiteralWithDatatype(str: string): boolean {
    return str.includes('^^');
  }
}
