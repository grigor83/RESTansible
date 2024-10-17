import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../services/user.service';
import { FormsModule, NgModel } from '@angular/forms';
import { User } from '../models/user';
import { EncryptionService } from '../services/encryption.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  username!: string;
  password!: string;

  constructor(private router : Router, private userService: UserService, private encryptionService: EncryptionService){}

  login() {
    this.userService.login(this.username, this.password).subscribe({
      next: response => {
        this.userService.activeUser = response;
        if (localStorage !== undefined){
          localStorage.removeItem('activeUser');
          this.userService.activeUser.expiry = new Date().getTime() + 1000*60*20; // 30 min
          const encryptedData = this.encryptionService.encryptData(this.userService.activeUser);
          localStorage.setItem('activeUser', encryptedData);
        }

        this.router.navigate(['/devices']);
      },
      error: error => {
        alert("User credentials are invalid!");
      }
    });
    
  }

  register(){
    this.router.navigate(['/register']); 
  }

}
