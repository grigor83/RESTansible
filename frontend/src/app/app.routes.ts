import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { PlayComponent } from './play/play.component';
import { PlaybooksComponent } from './playbooks/playbooks.component';
import { authGuard } from './auth.guard';
import { InventoriesComponent } from './inventories/inventories.component';

export const routes: Routes = [
    { path: 'login', component: LoginComponent},
    { path: 'register', component: RegisterComponent},
    { path: 'devices', component: PlayComponent, canActivate : [authGuard]},
    { path: 'playbooks', component: PlaybooksComponent, canActivate : [authGuard]},
    { path: 'hosts', component: InventoriesComponent , canActivate : [authGuard]},
    { path: '', redirectTo: '/login', pathMatch: 'full' }
];
