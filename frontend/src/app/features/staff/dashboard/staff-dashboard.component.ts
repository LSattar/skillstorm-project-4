import { ChangeDetectorRef, Component, NgZone, OnInit, inject } from '@angular/core';
import { PLATFORM_ID } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { DatePipe, KeyValuePipe, isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { catchError, forkJoin, of, timeout } from 'rxjs';
import { StaffAnimalsService } from '../../../core/services/staff-animals.service';
import { StaffApplicationsService } from '../../../core/services/staff-applications.service';
import type { AnimalResponse } from '../../../core/models/staff.model';
import type { AdoptionApplicationResponse } from '../../../core/models/adopter.model';

@Component({
  selector: 'app-staff-dashboard',
  standalone: true,
  imports: [DatePipe, KeyValuePipe],
  templateUrl: './staff-dashboard.component.html',
  styleUrl: './staff-dashboard.component.css'
})
export class StaffDashboardComponent implements OnInit {
  private cdr = inject(ChangeDetectorRef);
  private router = inject(Router);
  private platformId = inject(PLATFORM_ID);
  private ngZone = inject(NgZone);
  private animalsService = inject(StaffAnimalsService);
  private applicationsService = inject(StaffApplicationsService);

  statsByStatus: Record<string, number> = {};
  recentApplications: AdoptionApplicationResponse[] = [];
  loading = true;
  error: string | null = null;

  ngOnInit(): void {
    if (!isPlatformBrowser(this.platformId)) {
      this.loading = false;
      return;
    }
    forkJoin({
      animals: this.animalsService.list().pipe(
        timeout(10000),
        catchError(() => {
          return of([] as AnimalResponse[]);
        })
      ),
      applications: this.applicationsService.list().pipe(
        timeout(10000),
        catchError(() => {
          return of([] as AdoptionApplicationResponse[]);
        })
      )
    }).subscribe({
      next: ({ animals, applications }) => {
        this.ngZone.run(() => {
          this.buildStats(animals);
          this.recentApplications = applications
            .sort((a, b) => (b.createdAt ?? '').localeCompare(a.createdAt ?? ''))
            .slice(0, 10);
          this.cdr.detectChanges();
        });
      },
      error: (err: HttpErrorResponse) => {
        this.ngZone.run(() => {
          this.error = this.apiErrorMessage(err, 'Could not load dashboard.');
          this.cdr.detectChanges();
        });
      },
      complete: () => {
        this.ngZone.run(() => {
          this.loading = false;
          this.cdr.detectChanges();
        });
      }
    });
  }

  private buildStats(animals: AnimalResponse[]): void {
    const counts: Record<string, number> = {};
    for (const a of animals) {
      const s = a.status ?? 'UNKNOWN';
      counts[s] = (counts[s] ?? 0) + 1;
    }
    this.statsByStatus = counts;
  }

  statusLabel(status: string): string {
    return status?.replace(/_/g, ' ') ?? status;
  }

  openApplication(applicationId: string): void {
    this.router.navigate(['/staff/applications', applicationId]);
  }

  openApplicationsList(): void {
    this.router.navigate(['/staff/applications']);
  }

  get hasStats(): boolean {
    return Object.keys(this.statsByStatus).length > 0;
  }

  private apiErrorMessage(err: HttpErrorResponse, fallback: string): string {
    if (err.status === 401) return 'Your session has expired. Please log in again.';
    if (err.status === 403) return 'You do not have permission to access the staff dashboard.';
    return fallback;
  }
}
