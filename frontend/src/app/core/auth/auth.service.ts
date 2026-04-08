import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap, catchError, map, of } from 'rxjs';
import { apiBaseUrl } from '../api-config';
import type { LoginRequest, LoginResponse, UserMeResponse } from '../models/auth.model';

const TOKEN_KEY = 'access_token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private readonly currentUser$ = new BehaviorSubject<UserMeResponse | null>(null);

  get currentUser(): Observable<UserMeResponse | null> {
    return this.currentUser$.asObservable();
  }

  get currentUserValue(): UserMeResponse | null {
    return this.currentUser$.getValue();
  }

  get token(): string | null {
    if (typeof localStorage === 'undefined') return null;
    return localStorage.getItem(TOKEN_KEY);
  }

  get isLoggedIn(): boolean {
    return !!this.token;
  }

  hasRole(role: string): boolean {
    const user = this.currentUser$.getValue();
    if (!user?.roles?.length) return false;
    const normalized = role.startsWith('ROLE_') ? role : `ROLE_${role}`;
    return user.roles.includes(role) || user.roles.includes(normalized);
  }

  isStaff(): boolean {
    return this.hasRole('STAFF');
  }

  isAdopter(): boolean {
    return this.hasRole('ADOPTER');
  }

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${apiBaseUrl}/auth/login`, credentials).pipe(
      tap((res) => {
        if (res.accessToken && typeof localStorage !== 'undefined') {
          localStorage.setItem(TOKEN_KEY, res.accessToken);
        }
        if (res.user) {
          this.currentUser$.next(res.user);
        }
      })
    );
  }

  register(credentials: { username: string; password: string; email?: string; displayName?: string }): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${apiBaseUrl}/auth/register`, credentials).pipe(
      tap((res) => {
        if (res.accessToken && typeof localStorage !== 'undefined') {
          localStorage.setItem(TOKEN_KEY, res.accessToken);
        }
        if (res.user) {
          this.currentUser$.next(res.user);
        }
      })
    );
  }

  logout(): void {
    if (typeof localStorage !== 'undefined') localStorage.removeItem(TOKEN_KEY);
    this.currentUser$.next(null);
  }

  me(): Observable<UserMeResponse | null> {
    if (!this.token) {
      this.currentUser$.next(null);
      return of(null);
    }
    return this.http.get<UserMeResponse>(`${apiBaseUrl}/auth/me`).pipe(
      tap((user) => this.currentUser$.next(user)),
      catchError(() => {
        this.logout();
        return of(null);
      })
    );
  }

  loadCurrentUser(): Observable<UserMeResponse | null> {
    return this.me();
  }

  getGoogleLoginRedirectUrl(): Observable<string> {
    return this.http
      .get<{ redirectUrl: string }>(`${apiBaseUrl}/auth/oauth2/google-url`)
      .pipe(map((res) => res.redirectUrl), catchError(() => of('')));
  }

  getGoogleLinkRedirectUrl(): Observable<string> {
    return this.http
      .get<{ redirectUrl: string }>(`${apiBaseUrl}/auth/link-google`)
      .pipe(map((res) => res.redirectUrl), catchError(() => of('')));
  }

  completeOAuthLogin(token: string): Observable<UserMeResponse | null> {
    if (typeof localStorage !== 'undefined') {
      localStorage.setItem(TOKEN_KEY, token);
    }
    return this.me();
  }
}
