import { Component, OnInit, inject } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './core/auth/auth.service';
import type { UserMeResponse } from './core/models/auth.model';

@Component({
  selector: 'app-root',
  imports: [AsyncPipe, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  auth = inject(AuthService);
  router = inject(Router);

  currentUser$ = this.auth.currentUser;

  ngOnInit(): void {
    if (this.auth.token) {
      this.auth.me().subscribe();
    }
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/']);
  }

  isStaff(user: UserMeResponse | null): boolean {
    return user?.roles?.includes('STAFF') ?? false;
  }

  isAdopter(user: UserMeResponse | null): boolean {
    return user?.roles?.includes('ADOPTER') ?? false;
  }
}
