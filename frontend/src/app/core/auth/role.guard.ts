import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export function roleGuard(allowedRoles: string[]): CanActivateFn {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);
    const user = auth.currentUserValue;

    if (!user) {
      router.navigate(['/login']);
      return false;
    }

    const hasRole = allowedRoles.some((role) => auth.hasRole(role));
    if (!hasRole) {
      if (auth.isStaff()) {
        router.navigate(['/staff']);
      } else if (auth.isAdopter()) {
        router.navigate(['/adopter/applications']);
      } else {
        router.navigate(['/']);
      }
      return false;
    }
    return true;
  };
}
