import { Component, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { StaffApplicationsService } from '../../../core/services/staff-applications.service';
import type { AdoptionApplicationResponse } from '../../../core/models/adopter.model';

@Component({
  selector: 'app-staff-applications-list',
  standalone: true,
  imports: [DatePipe, FormsModule, RouterLink],
  templateUrl: './staff-applications-list.component.html',
  styleUrl: './staff-applications-list.component.css'
})
export class StaffApplicationsListComponent implements OnInit {
  private applicationsService = inject(StaffApplicationsService);

  applications: AdoptionApplicationResponse[] = [];
  filtered: AdoptionApplicationResponse[] = [];
  loading = true;
  error: string | null = null;
  filterStatus = '';

  ngOnInit(): void {
    this.applicationsService.list().subscribe({
      next: (list) => {
        this.applications = list;
        this.applyFilters();
      },
      error: (err: HttpErrorResponse) => {
        this.error = this.apiErrorMessage(err, 'Could not load applications.');
        this.loading = false;
      },
      complete: () => (this.loading = false)
    });
  }

  applyFilters(): void {
    this.filtered = this.filterStatus
      ? this.applications.filter((a) => a.status === this.filterStatus)
      : [...this.applications];
  }

  statusLabel(s: string): string {
    return s?.replace(/_/g, ' ') ?? s;
  }

  private apiErrorMessage(err: HttpErrorResponse, fallback: string): string {
    if (err.status === 401) return 'Your session has expired. Please log in again.';
    if (err.status === 403) return 'You do not have permission to access staff applications.';
    return fallback;
  }
}
