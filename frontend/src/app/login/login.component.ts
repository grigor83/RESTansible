import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../services/user.service';
import { FormsModule, NgModel } from '@angular/forms';
import { User } from '../models/user';

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

  constructor(private router : Router, private userService: UserService){}

  login() {    
    this.userService.login(this.username, this.password).subscribe({
      next: response => {
        this.userService.activeUser = response;
        this.router.navigate(['/devices']); 
      },
      error: error => {
        alert("Uneseni kredencijali nisu validni!");
      }
    });
     
  }

  register(){
    this.router.navigate(['/register']); 
  }

}
