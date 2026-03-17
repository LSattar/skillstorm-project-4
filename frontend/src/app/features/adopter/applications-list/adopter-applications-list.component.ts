import { Component, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
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
      error: () => {
        this.error = 'Could not load applications.';
        this.loading = false;
      },
      complete: () => (this.loading = false)
    });
  }

  statusLabel(status: string): string {
    return status?.replace(/_/g, ' ') ?? status;
  }
}
