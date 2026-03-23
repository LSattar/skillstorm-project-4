import { Component, OnInit, inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { DatePipe, KeyValuePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { StaffAnimalsService } from '../../../core/services/staff-animals.service';
import { StaffApplicationsService } from '../../../core/services/staff-applications.service';
import type { AnimalResponse } from '../../../core/models/staff.model';
import type { AdoptionApplicationResponse } from '../../../core/models/adopter.model';

@Component({
  selector: 'app-staff-dashboard',
  standalone: true,
  imports: [DatePipe, KeyValuePipe, RouterLink],
  templateUrl: './staff-dashboard.component.html',
  styleUrl: './staff-dashboard.component.css'
})
export class StaffDashboardComponent implements OnInit {
  private animalsService = inject(StaffAnimalsService);
  private applicationsService = inject(StaffApplicationsService);

  statsByStatus: Record<string, number> = {};
  recentApplications: AdoptionApplicationResponse[] = [];
  loading = true;
  error: string | null = null;

  ngOnInit(): void {
    forkJoin({
      animals: this.animalsService.list(),
      applications: this.applicationsService.list()
    }).subscribe({
      next: ({ animals, applications }) => {
        this.buildStats(animals);
        this.recentApplications = applications
          .sort((a, b) => (b.createdAt ?? '').localeCompare(a.createdAt ?? ''))
          .slice(0, 10);
      },
      error: (err: HttpErrorResponse) => {
        this.error = this.apiErrorMessage(err, 'Could not load dashboard.');
      },
      complete: () => (this.loading = false)
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

  get hasStats(): boolean {
    return Object.keys(this.statsByStatus).length > 0;
  }

  private apiErrorMessage(err: HttpErrorResponse, fallback: string): string {
    if (err.status === 401) return 'Your session has expired. Please log in again.';
    if (err.status === 403) return 'You do not have permission to access the staff dashboard.';
    return fallback;
  }
}
