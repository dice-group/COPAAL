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
  console.log(resourceUris)
    const sparqlEndpoint = 'https://dbpedia.org/sparql';
    return this.http.get<any[]>(sparqlEndpoint, {
      params: new HttpParams().set('query', this.buildSparqlQuery(resourceUris)),
//        ...httpOptions
    }).pipe(
      map(data =>{ this.extractResults(data)
      })
    );
  }

  private extractResults(data: any): any[] {
  return data
//    if (data.results && data.results.bindings && Array.isArray(data.results.bindings)) {
//         console.log('Valid JSON structure found.', data);
//         return data.results.bindings;
//       } else {
//         console.error(' in the response:', data);
//       }
  }

  buildSparqlQuery(resourceUris: string[]): string {
    if (resourceUris.length === 0) {
      return '';
    }
    if (resourceUris.length === 1) {
      return `
        SELECT ?f ?l
        WHERE {
          <${resourceUris[0].trim()}> <http://dbpedia.org/ontology/thumbnail> ?f .
          <${resourceUris[0].trim()}> <http://www.w3.org/2000/01/rdf-schema#label> ?l .
          FILTER(LANG(?l)="en")
        }
        LIMIT 1
      `;
    } else {
      return `
        SELECT ?f ?l
        WHERE {
          {
            <${resourceUris[0].trim()}> <http://dbpedia.org/ontology/thumbnail> ?f .
            <${resourceUris[0].trim()}> <http://www.w3.org/2000/01/rdf-schema#label> ?l .
            FILTER(LANG(?l)="en")
          }
          UNION
          {
            <${resourceUris[1].trim()}> <http://dbpedia.org/ontology/thumbnail> ?f .
            <${resourceUris[1].trim()}> <http://www.w3.org/2000/01/rdf-schema#label> ?l .
            FILTER(LANG(?l)="en")
          }
        }
        LIMIT 1
      `;
    }
  }

}
