import { ChangeDetectorRef, Component, NgZone, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { StaffAnimalsService } from '../../../core/services/staff-animals.service';
import { StaffEmployeesService } from '../../../core/services/staff-employees.service';
import type { AnimalResponse, UserResponse } from '../../../core/models/staff.model';

@Component({
  selector: 'app-staff-animals-list',
  standalone: true,
  imports: [DatePipe, FormsModule, RouterLink],
  templateUrl: './staff-animals-list.component.html',
  styleUrl: './staff-animals-list.component.css'
})
export class StaffAnimalsListComponent implements OnInit {
  private cdr = inject(ChangeDetectorRef);
  private ngZone = inject(NgZone);
  private animalsService = inject(StaffAnimalsService);
  private employeesService = inject(StaffEmployeesService);

  filtered: AnimalResponse[] = [];
  usersById: Record<string, UserResponse> = {};
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
    forkJoin({
      animals: this.animalsService.list({
        status: this.filterStatus || undefined,
        species: this.filterSpecies || undefined,
        shelterId: this.filterShelterId ? Number(this.filterShelterId) : undefined,
        fosterId: this.filterFosterId || undefined,
        medicallyComplex:
          this.filterMedicallyComplex === '' ? undefined : this.filterMedicallyComplex === 'true',
        intakeDate: this.filterIntakeDate || undefined,
        adoptionStatus: this.filterAdoptionStatus || undefined
      }),
      users: this.employeesService.list()
    }).subscribe({
      next: ({ animals, users }) => {
        this.ngZone.run(() => {
          this.filtered = animals;
          this.usersById = users.reduce<Record<string, UserResponse>>((acc, user) => {
            acc[user.id] = user;
            return acc;
          }, {});
          this.cdr.detectChanges();
        });
      },
      error: (err: HttpErrorResponse) => {
        this.ngZone.run(() => {
          this.error = this.apiErrorMessage(err, 'Could not load animals.');
          this.loading = false;
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

  locationLabel(animal: AnimalResponse): string {
    if (animal.currentShelterName) return animal.currentShelterName;
    if (animal.currentShelterId != null) return `Shelter #${animal.currentShelterId}`;
    if (animal.currentFosterUserId) {
      const foster = this.usersById[animal.currentFosterUserId];
      return foster?.displayName || foster?.username || `Foster ${animal.currentFosterUserId}`;
    }
    return '—';
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
