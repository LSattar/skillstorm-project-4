import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { catchError, map, of } from 'rxjs';
import { AuthService } from './auth.service';

export function roleGuard(allowedRoles: string[]): CanActivateFn {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);
    const roleRedirect = () => {
      if (auth.isStaff()) return router.createUrlTree(['/staff']);
      if (auth.isAdopter()) return router.createUrlTree(['/adopter/applications']);
      return router.createUrlTree(['/']);
    };

    const canActivateForRoles = () => {
      const hasRole = allowedRoles.some((role) => auth.hasRole(role));
      if (!hasRole) {
        return roleRedirect();
      }
      return true;
    };

    if (auth.currentUserValue) {
      return canActivateForRoles();
    }

    if (!auth.token) {
      return router.createUrlTree(['/login']);
    }

    return auth.me().pipe(
      map((user) => {
        if (!user) {
          return router.createUrlTree(['/login']);
        }
        return canActivateForRoles();
      }),
      catchError(() => of(router.createUrlTree(['/login'])))
    );
  };
}
