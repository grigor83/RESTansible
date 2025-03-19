import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { User } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private url = 'http://localhost:8080/users';
  public activeUser: User | null = null;

  constructor(private http: HttpClient) { }

  register(user: User) {
    return this.http.post<any>(this.url + '/register', user);
  }

  login(username: string, password: string){
    return this.http.post<User>(this.url + '/login', {
      username : username,
      password : password,
    });
  }

  logout(){
    this.activeUser = null;
  }
}