import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { DevicesComponent } from './devices/devices.component';
import { PlaybooksComponent } from './playbooks/playbooks.component';
import { HostsComponent } from './hosts/hosts.component';
import { authGuard } from './auth.guard';

export const routes: Routes = [
    { path: 'login', component: LoginComponent},
    { path: 'register', component: RegisterComponent},
    { path: 'devices', component: DevicesComponent, canActivate : [authGuard]},
    { path: 'playbooks', component: PlaybooksComponent, canActivate : [authGuard]},
    { path: 'hosts', component: HostsComponent, canActivate : [authGuard]},
    { path: '', redirectTo: '/login', pathMatch: 'full' }
];
