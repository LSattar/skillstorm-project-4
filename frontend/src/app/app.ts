import { Component, inject } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
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
}
