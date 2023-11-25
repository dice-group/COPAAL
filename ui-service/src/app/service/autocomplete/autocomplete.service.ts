import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from "../../../environments/environment";
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AutocompleteService {
  // Base URL for the autocomplete service
  private apiUrl = environment.dbpediaUrlBaseAC;
  constructor(private http: HttpClient) {}

  // Function to search for autocomplete options based on the input and query
  search(input, query: string): Observable<string[]> {
    return this.http.get(this.apiUrl, {
      params: {
        MaxHits: '20',
        QueryString: query
      },
      headers: {
        Accept: 'application/xml' // XML response format
      },
      responseType: 'text' // the response is treated as text, not JSON
    }).pipe(
      map(xmlData => this.parseXmlToJson(xmlData, input))
    );
  }

  // Function to parse XML data to JSON based on the input type (subject or object)
  private parseXmlToJson(xmlData: string, input): string[] {
    let result: string[] = [];
    const parser = new DOMParser();
    const xmlDoc = parser.parseFromString(xmlData, 'text/xml');

    if (input === 'subject') {
      const labelNodes = xmlDoc.querySelectorAll('Result > Label');
      labelNodes.forEach(node => {
        result.push(node.textContent.trim());
      });
    }
    else if (input === 'object'){
      const categoryNodes = xmlDoc.querySelectorAll('Result > Categories > Category > URI');
      categoryNodes.forEach(node => {
        const categoryText = node.textContent.trim();
        const categoryValue = categoryText.replace(environment.dbpediaUrlBaseObj, '');
        result.push(categoryValue);
      });
    }

    return result;
  }
}
