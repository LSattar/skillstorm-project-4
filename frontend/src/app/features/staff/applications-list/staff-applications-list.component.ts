import { Component, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
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
      error: () => {
        this.error = 'Could not load applications.';
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
}
