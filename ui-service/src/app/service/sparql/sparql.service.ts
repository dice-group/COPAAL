import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class SparqlService {

  constructor(private http: HttpClient) { }

  executeThumbnailQueries(resourceUris: string[]): Observable<any[]> {
    const sparqlEndpoint = 'https://dbpedia.org/sparql';
//     const httpOptions = {
//       headers: new HttpHeaders({
//         'Accept': 'application/json',
//         'Content-Type': 'application/x-www-form-urlencoded'
//       }),
//       responseType: 'text'
//     };

    return this.http.get<any[]>(sparqlEndpoint, {
      params: new HttpParams().set('query', this.buildSparqlQuery(resourceUris)),
//        ...httpOptions
    }).pipe(
      map(data =>{
      try{
            console.log(data);
            }
            catch(e){
              console.log(e,'error');
            }
            return data;
      })
    );
  }

  private extractResults(data: any): any[] {
   if (data.results && data.results.bindings && Array.isArray(data.results.bindings)) {
        console.log('Valid JSON structure found.', data);
        return data.results.bindings;
      } else {
        console.error(' in the response:', data);
      }
  }

  buildSparqlQuery(resourceUris: string[]): string {
    const trimmedUri = resourceUris[0].trim();
    const formattedUri = `<${trimmedUri}>`;

    return `
      SELECT ?f ?l
      WHERE {
        ${formattedUri} <http://dbpedia.org/ontology/thumbnail> ?f .
        ${formattedUri} <http://www.w3.org/2000/01/rdf-schema#label> ?l .
        FILTER(LANG(?l)="en")
      }
      LIMIT 1
    `;
  }
}
