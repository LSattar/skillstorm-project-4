import { Component, OnInit, inject } from '@angular/core';
import { DatePipe, KeyValuePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
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
    this.animalsService.list().subscribe({
      next: (animals) => this.buildStats(animals),
      error: () => {
        this.error = 'Could not load animals.';
        this.loading = false;
      }
    });
    this.applicationsService.list().subscribe({
      next: (apps) => {
        this.recentApplications = apps
          .sort((a, b) => (b.createdAt ?? '').localeCompare(a.createdAt ?? ''))
          .slice(0, 10);
      },
      error: () => {},
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
}
