import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class SparqlService {

  constructor(private http: HttpClient) { }

  executeThumbnailQueries(resourceUris: string[]): Observable<string[]> {
    console.log(resourceUris);
    const sparqlEndpoint = 'https://dbpedia.org/sparql';
    return this.http.get<any[]>(sparqlEndpoint, {
      params: new HttpParams().set('query', this.buildSparqlQuery(resourceUris)),
    }).pipe(
      map(data => this.extractResults(data))
    );
  }

  private extractResults(data: any): any[] {
    return data;
  }

  private buildSparqlQuery(resourceUris: string[]): string {
    if (resourceUris.length === 0) {
      return '';
    }

    const queries = resourceUris.map(uri => {
      const trimmedUri = uri.trim();
      // Check if the URI is valid or if it is a literal with datatype
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

    const sparqlQuery = `
      SELECT ?f ?l
      WHERE {
        ${queries.join(' UNION ')}
      }
      LIMIT 1
    `;
    return sparqlQuery;
  }

  private isValidUri(str: string): boolean {
    try {
      new URL(str);
      return true;
    } catch (error) {
      return false;
    }
  }

  private isLiteralWithDatatype(str: string): boolean {
    return str.includes('^^');
  }
}
