import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-staff-layout',
  standalone: true,
  imports: [RouterLink, RouterOutlet],
  template: `
    <nav class="staff-nav">
      <a routerLink="/staff">Dashboard</a>
      <a routerLink="/staff/animals">Animals</a>
      <a routerLink="/staff/applications">Applications</a>
      <a routerLink="/staff/employees">Employees</a>
    </nav>
    <router-outlet />
  `,
  styles: `
    .staff-nav {
      display: flex;
      gap: 1rem;
      margin-bottom: 1rem;
    }
    .staff-nav a {
      color: var(--color-primary);
      text-decoration: none;
    }
    .staff-nav a:hover { text-decoration: underline; }
  `
})
export class StaffLayoutComponent {}
