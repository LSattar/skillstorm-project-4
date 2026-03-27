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

  filtered: AdoptionApplicationResponse[] = [];
  loading = true;
  error: string | null = null;
  filterStatus = '';
  filterAnimalId = '';
  filterAdopterEmail = '';

  ngOnInit(): void {
    this.applyFilters();
  }

  applyFilters(): void {
    this.loading = true;
    this.error = null;
    this.applicationsService.list({
      status: this.filterStatus || undefined,
      animalId: this.filterAnimalId || undefined,
      adopterEmail: this.filterAdopterEmail || undefined
    }).subscribe({
      next: (list) => {
        this.filtered = list;
      },
      error: (err: HttpErrorResponse) => {
        this.error = this.apiErrorMessage(err, 'Could not load applications.');
        this.loading = false;
      },
      complete: () => (this.loading = false)
    });
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
