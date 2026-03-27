import { ChangeDetectorRef, Component, NgZone, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { StaffApplicationsService } from '../../../core/services/staff-applications.service';
import { StaffAnimalsService } from '../../../core/services/staff-animals.service';
import { StaffEmployeesService } from '../../../core/services/staff-employees.service';
import type { AdoptionApplicationResponse } from '../../../core/models/adopter.model';
import type { AnimalResponse, UserResponse } from '../../../core/models/staff.model';

@Component({
  selector: 'app-staff-applications-list',
  standalone: true,
  imports: [DatePipe, FormsModule, RouterLink],
  templateUrl: './staff-applications-list.component.html',
  styleUrl: './staff-applications-list.component.css'
})
export class StaffApplicationsListComponent implements OnInit {
  private cdr = inject(ChangeDetectorRef);
  private ngZone = inject(NgZone);
  private applicationsService = inject(StaffApplicationsService);
  private animalsService = inject(StaffAnimalsService);
  private employeesService = inject(StaffEmployeesService);

  filtered: AdoptionApplicationResponse[] = [];
  animalsById: Record<string, AnimalResponse> = {};
  usersById: Record<string, UserResponse> = {};
  loading = true;
  error: string | null = null;
  actionError: string | null = null;
  actionSuccess: string | null = null;
  actionInProgressId: string | null = null;
  filterStatus = '';
  filterAnimalId = '';
  filterAdopterEmail = '';

  ngOnInit(): void {
    this.applyFilters();
  }

  applyFilters(): void {
    this.loading = true;
    this.error = null;
    this.actionError = null;
    forkJoin({
      applications: this.applicationsService.list({
        status: this.filterStatus || undefined,
        animalId: this.filterAnimalId || undefined,
        adopterEmail: this.filterAdopterEmail || undefined
      }),
      animals: this.animalsService.list(),
      users: this.employeesService.list()
    }).subscribe({
      next: ({ applications, animals, users }) => {
        this.ngZone.run(() => {
          this.filtered = applications;
          this.animalsById = this.toAnimalsById(animals);
          this.usersById = this.toUsersById(users);
          this.cdr.detectChanges();
        });
      },
      error: (err: HttpErrorResponse) => {
        this.ngZone.run(() => {
          this.error = this.apiErrorMessage(err, 'Could not load applications.');
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

  resetFilters(): void {
    this.filterStatus = '';
    this.filterAnimalId = '';
    this.filterAdopterEmail = '';
    this.applyFilters();
  }

  statusLabel(s: string): string {
    return s?.replace(/_/g, ' ') ?? s;
  }

  animalName(app: AdoptionApplicationResponse): string {
    return this.animalsById[app.animalId]?.name ?? 'Unknown animal';
  }

  adopterName(app: AdoptionApplicationResponse): string {
    const user = this.usersById[app.adopterUserId];
    return user?.displayName || user?.username || 'Unknown adopter';
  }

  adopterEmail(app: AdoptionApplicationResponse): string {
    return this.usersById[app.adopterUserId]?.email ?? 'N/A';
  }

  canApprove(app: AdoptionApplicationResponse): boolean {
    return app.status === 'SUBMITTED' || app.status === 'IN_REVIEW';
  }

  approve(app: AdoptionApplicationResponse): void {
    if (!this.canApprove(app) || this.actionInProgressId) return;
    this.actionError = null;
    this.actionSuccess = null;
    this.actionInProgressId = app.id;
    this.applicationsService.approve(app.id, {}).subscribe({
      next: (updated) => {
        this.ngZone.run(() => {
          const animalLabel = this.animalName(updated);
          this.actionSuccess = `Application for ${animalLabel} was approved successfully.`;
          this.filtered = this.filtered.map((item) => (item.id === app.id ? updated : item));
          // Refresh from API to ensure status/filter results are fully in sync.
          this.applyFilters();
          this.cdr.detectChanges();
        });
      },
      error: (err: HttpErrorResponse) => {
        this.ngZone.run(() => {
          this.actionError = this.apiErrorMessage(err, 'Approve failed.');
          this.cdr.detectChanges();
        });
      },
      complete: () => {
        this.ngZone.run(() => {
          this.actionInProgressId = null;
          this.cdr.detectChanges();
        });
      }
    });
  }

  private toAnimalsById(animals: AnimalResponse[]): Record<string, AnimalResponse> {
    return animals.reduce<Record<string, AnimalResponse>>((acc, animal) => {
      acc[animal.id] = animal;
      return acc;
    }, {});
  }

  private toUsersById(users: UserResponse[]): Record<string, UserResponse> {
    return users.reduce<Record<string, UserResponse>>((acc, user) => {
      acc[user.id] = user;
      return acc;
    }, {});
  }

  private apiErrorMessage(err: HttpErrorResponse, fallback: string): string {
    if (err.status === 401) return 'Your session has expired. Please log in again.';
    if (err.status === 403) return 'You do not have permission to access staff applications.';
    return fallback;
  }
}
