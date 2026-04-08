import { Component, OnInit, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-auth-callback',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './auth-callback.component.html',
  styleUrl: './auth-callback.component.css'
})
export class AuthCallbackComponent implements OnInit {
  private router = inject(Router);
  private auth = inject(AuthService);

  loading = true;
  message = 'Finishing sign-in...';
  isError = false;

  ngOnInit(): void {
    if (typeof window === 'undefined') {
      this.loading = false;
      this.isError = true;
      this.message = 'OAuth callback is only available in the browser.';
      return;
    }

    const hashParams = new URLSearchParams(window.location.hash.replace('#', ''));
    const queryParams = new URLSearchParams(window.location.search);
    const token = hashParams.get('token');
    const error = queryParams.get('error');
    const linked = queryParams.get('linked');

    if (token) {
      this.auth.completeOAuthLogin(token).pipe(
        finalize(() => {
          this.loading = false;
        })
      ).subscribe({
        next: (user) => {
          if (user?.roles?.includes('STAFF')) {
            this.router.navigate(['/staff']);
            return;
          }
          if (user?.roles?.includes('ADOPTER')) {
            this.router.navigate(['/adopter/applications']);
            return;
          }
          this.router.navigate(['/']);
        },
        error: () => {
          this.isError = true;
          this.message = 'Could not complete Google sign-in.';
        }
      });
      return;
    }

    this.loading = false;
    if (linked === 'true') {
      this.isError = false;
      this.message = 'Google account linked successfully.';
      return;
    }
    if (error === 'use_password') {
      this.isError = true;
      this.message = 'Use username/password first, then link your Google account.';
      return;
    }
    this.isError = true;
    this.message = 'Google sign-in was cancelled or failed.';
  }
}
