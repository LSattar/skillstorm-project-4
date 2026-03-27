import { Component, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { StaffAnimalsService } from '../../../core/services/staff-animals.service';
import type { AnimalResponse } from '../../../core/models/staff.model';

@Component({
  selector: 'app-staff-animals-list',
  standalone: true,
  imports: [DatePipe, FormsModule, RouterLink],
  templateUrl: './staff-animals-list.component.html',
  styleUrl: './staff-animals-list.component.css'
})
export class StaffAnimalsListComponent implements OnInit {
  private animalsService = inject(StaffAnimalsService);

  filtered: AnimalResponse[] = [];
  loading = true;
  error: string | null = null;
  filterStatus = '';
  filterSpecies = '';
  filterShelterId = '';
  filterFosterId = '';
  filterMedicallyComplex = '';
  filterIntakeDate = '';
  filterAdoptionStatus = '';

  ngOnInit(): void {
    this.load();
  }

  applyFilters(): void {
    this.load();
  }

  private load(): void {
    this.loading = true;
    this.error = null;
    this.animalsService.list({
      status: this.filterStatus || undefined,
      species: this.filterSpecies || undefined,
      shelterId: this.filterShelterId ? Number(this.filterShelterId) : undefined,
      fosterId: this.filterFosterId || undefined,
      medicallyComplex:
        this.filterMedicallyComplex === '' ? undefined : this.filterMedicallyComplex === 'true',
      intakeDate: this.filterIntakeDate || undefined,
      adoptionStatus: this.filterAdoptionStatus || undefined
    }).subscribe({
      next: (list) => {
        this.filtered = list;
      },
      error: (err: HttpErrorResponse) => {
        this.error = this.apiErrorMessage(err, 'Could not load animals.');
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
    if (err.status === 403) return 'You do not have permission to access staff animals.';
    return fallback;
  }
}
