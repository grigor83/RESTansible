import { HttpClient } from '@angular/common/http';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { User } from '../models/user';
import { EncryptionService } from './encryption.service';
import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private url = 'http://localhost:8080/users';
  public activeUser: User | null = null;

  constructor(private http: HttpClient, private router: Router, @Inject(PLATFORM_ID) private platformId: Object, private encryption: EncryptionService) {
    if (isPlatformBrowser(this.platformId) && localStorage !== undefined){
      const storedUser = localStorage.getItem('activeUser');
      if (storedUser){
        this.activeUser = this.encryption.decryptData(storedUser);
        if (this.activeUser && new Date().getTime() > this.activeUser?.expiry){
          this.logout();
        }
      }
    }
  }

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
    if (isPlatformBrowser(this.platformId) && localStorage !== undefined)
      localStorage.removeItem('activeUser');
    
    this.router.navigate(['/login']); 
  }

  checkExpiry(): boolean {
    if (this.activeUser && new Date().getTime() > this.activeUser?.expiry)
      return false;
    else
      return true;
  }
}