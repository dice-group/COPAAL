import {EventEmitter, Injectable} from '@angular/core';
import {HttpClient, HttpEvent, HttpHandler, HttpInterceptor, HttpParams, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../../environments/environment';
@Injectable({
  providedIn: 'root'
})
export class RestService implements HttpInterceptor {
  private hosturl = environment.apiBase;
  public requestEvnt: EventEmitter<boolean> = new EventEmitter();

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    // Clone the request and replace the original headers with
    // cloned headers, updated with the authorization.
    const authReq = req.clone({
      withCredentials: true
      /*headers: req.headers.set('Content-Type', 'application/json')*/
    });
    // send cloned request with header to the next handler.
    const reqObs: Observable<HttpEvent<any>> = next.handle(authReq);
    return reqObs;
  }

  constructor(private http: HttpClient) {
  }

  getFullUrl(path: string) {
    return this.hosturl + path;
  }

  public getNPRequest(path: string): Observable<any> {
    return this.http.get(this.getFullUrl(path));
  }

  public getRequest(path: string, prmobj: object): Observable<any> {
    let params = new HttpParams();
    for (const x in prmobj) {
      if (prmobj[x] != null) {
        params = params.set(x, prmobj[x]);
      }
    }
    this.requestEvnt.emit(true);
    const reqObs = this.http.get(this.getFullUrl(path), {params: params});
    return reqObs;
  }
}
