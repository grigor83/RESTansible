import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../services/user.service';
import { FormsModule, NgForm } from '@angular/forms';
import { User } from '../models/user';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  name!: string;
  lastname!: string;
  username!: string;
  password!: string;
  phoneNumber!: string;
  email!: string;

  constructor(private router: Router, private userService: UserService) {}

  register(registerForm: NgForm) {
    const user = new User(this.name, this.lastname, this.username, this.password, this.phoneNumber, this.email);

    this.userService.register(user).subscribe({
      next: response => {
            alert("Uspješno ste podnijeli zahtjev za registraciju!");
            registerForm.reset();
      },
      error: error => {
        alert("Uneseno korisničko ime je već zauzeto!");
      }
    });
  }


  login(){
    this.router.navigate(['/login']); 
  }

}
