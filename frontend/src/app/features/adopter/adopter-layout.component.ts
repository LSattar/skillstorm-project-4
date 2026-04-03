import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-adopter-layout',
  standalone: true,
  imports: [RouterLink, RouterOutlet],
  template: `
    <nav class="adopter-nav">
      <a routerLink="/adopter/profile">Profile</a>
      <a routerLink="/adopter/recommendations">Recommendations</a>
      <a routerLink="/adopter/applications/new">New Application</a>
      <a routerLink="/adopter/applications">My Applications</a>
    </nav>
    <router-outlet />
  `,
  styles: `
    .adopter-nav {
      display: flex;
      gap: 1rem;
      margin-bottom: 1rem;
    }
    .adopter-nav a {
      color: var(--color-primary);
      text-decoration: none;
    }
    .adopter-nav a:hover { text-decoration: underline; }
  `
})
export class AdopterLayoutComponent {}
