import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home.component';
import { AnimalDetailComponent } from './features/animal-detail/animal-detail.component';
import { LoginPlaceholderComponent } from './features/login-placeholder/login-placeholder.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'animals/:id', component: AnimalDetailComponent },
  { path: 'login', component: LoginPlaceholderComponent }
];
