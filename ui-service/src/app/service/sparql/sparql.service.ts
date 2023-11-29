import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SparqlService {

  constructor(private http: HttpClient) { }

    executeThumbnailQueries(paths: string[]): Observable<any>[] {
    console.log(paths,'pathsssss')
      return paths.map(path => {
        const thumbnailQuery = `
          SELECT ?f ?l
          WHERE {
            <${path}>
            <http://dbpedia.org/ontology/thumbnail> ?f;
            <http://www.w3.org/2000/01/rdf-schema#label> ?l .
            FILTER(LANG(?l)="en")
          }
          LIMIT 1
        `;

        const thumbnailParams = new HttpParams().set('query', thumbnailQuery);

        const thumbnailOptions = {
          headers: new HttpHeaders({
            'Content-Type': 'application/x-www-form-urlencoded',
            Accept: 'application/json',
          }),
          params: thumbnailParams,
        };

//         this.http.get('http://dbpedia.org/sparql', options) // 1
//               .map(response => response.json())
//               .subscribe(data => {
//                   console.log(data);
//                   const sparqlData = data; // 3
//                });

        return this.http.get('http://dbpedia.org/sparql', thumbnailOptions);

      });
    }
}
