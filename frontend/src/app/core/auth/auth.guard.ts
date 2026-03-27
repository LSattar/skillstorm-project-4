import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map, catchError, of } from 'rxjs';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.token) {
    return router.createUrlTree(['/login']);
  }

  if (auth.currentUserValue) {
    return true;
  }

  return auth.me().pipe(
    map((user) => {
      if (!user) {
        return router.createUrlTree(['/login']);
      }
      return true;
    }),
    catchError(() => of(router.createUrlTree(['/login'])))
  );
};
