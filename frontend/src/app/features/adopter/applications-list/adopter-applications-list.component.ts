import { Component, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { AdopterService } from '../../../core/services/adopter.service';
import type { AdoptionApplicationResponse } from '../../../core/models/adopter.model';

@Component({
  selector: 'app-adopter-applications-list',
  standalone: true,
  imports: [DatePipe, RouterLink],
  templateUrl: './adopter-applications-list.component.html',
  styleUrl: './adopter-applications-list.component.css'
})
export class AdopterApplicationsListComponent implements OnInit {
  private adopterService = inject(AdopterService);

  applications: AdoptionApplicationResponse[] = [];
  loading = true;
  error: string | null = null;

  ngOnInit(): void {
    this.adopterService.getApplications().subscribe({
      next: (list) => (this.applications = list),
      error: (err: HttpErrorResponse) => {
        this.error = this.apiErrorMessage(err, 'Could not load applications.');
        this.loading = false;
      },
      complete: () => (this.loading = false)
    });
  }

  statusLabel(status: string): string {
    return status?.replace(/_/g, ' ') ?? status;
  }

  private apiErrorMessage(err: HttpErrorResponse, fallback: string): string {
    if (err.status === 401) return 'Your session has expired. Please log in again.';
    if (err.status === 403) return 'You do not have permission to view this page.';
    return fallback;
  }
}
