import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {AppUser} from "../model/appUser.model";

@Injectable({
  providedIn: 'root'
})

export class AppUserService {
  apiHost: string = 'http://localhost:8082/';
  headers: HttpHeaders = new HttpHeaders({ 'Content-Type': 'application/json' });

  constructor(private http: HttpClient) { }

  getAppUsers(): Observable<AppUser[]>{
    return this.http.get<AppUser[]>(this.apiHost + 'api/users/all', {headers: this.headers});
  }
}