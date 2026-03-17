import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  { path: '', renderMode: RenderMode.Prerender },
  { path: 'login', renderMode: RenderMode.Prerender },
  { path: 'animals/:id', renderMode: RenderMode.Server },
  { path: 'adopter/applications/:id', renderMode: RenderMode.Server },
  { path: 'staff/animals/:id/edit', renderMode: RenderMode.Server },
  { path: 'staff/animals/:id/move', renderMode: RenderMode.Server },
  { path: 'staff/applications/:id', renderMode: RenderMode.Server },
  { path: 'staff/employees/:id', renderMode: RenderMode.Server },
  { path: '**', renderMode: RenderMode.Server }
];
