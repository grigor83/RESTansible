import { Component } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { UserService } from '../services/user.service';
import { filter } from 'rxjs/operators';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-sidenav',
  standalone: true,
  imports: [RouterOutlet, NgIf, RouterLink, RouterLinkActive],
  templateUrl: './sidenav.component.html',
  styleUrl: './sidenav.component.css'
})
export class SidenavComponent {

  currentRoute!: string;
  showSidenav: boolean = false;

  constructor(private router: Router, private userService : UserService) {}

  ngOnInit(): void {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.currentRoute = this.router.url;
      this.updateLinkVisibility();
    });
  }

  updateLinkVisibility() {
    if (this.currentRoute === '/login' || this.currentRoute === '/register') {
      this.showSidenav = false;
    }
    else 
      this.showSidenav = true;
  }

  logout() {
    this.userService.logout();
  }
}
