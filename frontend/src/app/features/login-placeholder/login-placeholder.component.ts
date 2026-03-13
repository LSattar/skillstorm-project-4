import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-login-placeholder',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="placeholder">
      <h1>Login</h1>
      <p>Login coming soon.</p>
      <a routerLink="/">Back to home</a>
    </div>
  `,
  styles: `
    .placeholder {
      text-align: center;
      padding: 3rem;
    }
    .placeholder a {
      color: #0d6efd;
      text-decoration: none;
    }
    .placeholder a:hover {
      text-decoration: underline;
    }
  `
})
export class LoginPlaceholderComponent {}
