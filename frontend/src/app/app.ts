import { Component, inject } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from './core/auth/auth.service';
import type { UserMeResponse } from './core/models/auth.model';
import { FooterComponent } from './shared/footer/footer.component';

@Component({
  selector: 'app-root',
  imports: [AsyncPipe, RouterLink, RouterLinkActive, RouterOutlet, FooterComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  auth = inject(AuthService);
  router = inject(Router);

  currentUser$ = this.auth.currentUser;
  linkingOAuth = false;
  oauthLinkError: string | null = null;

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/']);
  }

  isStaff(user: UserMeResponse | null): boolean {
    if (!user?.roles?.length) return false;
    return user.roles.includes('STAFF') || user.roles.includes('ROLE_STAFF');
  }

  isAdopter(user: UserMeResponse | null): boolean {
    if (!user?.roles?.length) return false;
    return user.roles.includes('ADOPTER') || user.roles.includes('ROLE_ADOPTER');
  }

  canLinkGoogle(user: UserMeResponse | null): boolean {
    if (!user?.roles?.length) return false;
    return user.roles.includes('STAFF') || user.roles.includes('ROLE_STAFF')
      || user.roles.includes('FOSTER') || user.roles.includes('ROLE_FOSTER');
  }

  onLinkGoogleAccount(): void {
    this.oauthLinkError = null;
    this.linkingOAuth = true;
    this.auth.getGoogleLinkRedirectUrl().pipe(
      finalize(() => {
        this.linkingOAuth = false;
      })
    ).subscribe({
      next: (redirectUrl) => {
        if (!redirectUrl || typeof window === 'undefined') {
          this.oauthLinkError = 'Could not start Google account linking.';
          return;
        }
        window.location.href = redirectUrl;
      },
      error: () => {
        this.oauthLinkError = 'Could not start Google account linking.';
      }
    });
  }
}
