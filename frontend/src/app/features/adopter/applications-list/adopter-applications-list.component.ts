import { ChangeDetectorRef, Component, NgZone, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';
import { AdopterService } from '../../../core/services/adopter.service';
import { AnimalsService } from '../../../core/services/animals.service';
import type { AdoptionApplicationResponse } from '../../../core/models/adopter.model';
import type { Animal } from '../../../core/models/animal.model';

@Component({
  selector: 'app-adopter-applications-list',
  standalone: true,
  imports: [DatePipe, RouterLink],
  templateUrl: './adopter-applications-list.component.html',
  styleUrl: './adopter-applications-list.component.css'
})
export class AdopterApplicationsListComponent implements OnInit {
  private cdr = inject(ChangeDetectorRef);
  private ngZone = inject(NgZone);
  private adopterService = inject(AdopterService);
  private animalsService = inject(AnimalsService);

  applications: AdoptionApplicationResponse[] = [];
  animalNames: Record<string, string> = {};
  loading = true;
  error: string | null = null;

  ngOnInit(): void {
    forkJoin({
      applications: this.adopterService.getApplications(),
      animals: this.animalsService.getAnimals().pipe(
        catchError(() => of([] as Animal[]))
      )
    }).pipe(
      finalize(() => {
        this.ngZone.run(() => {
          this.loading = false;
          this.cdr.detectChanges();
        });
      })
    ).subscribe({
      next: ({ applications, animals }) => {
        this.ngZone.run(() => {
          this.applications = applications;
          this.animalNames = animals.reduce<Record<string, string>>((acc, animal) => {
            acc[animal.id] = animal.name;
            return acc;
          }, {});
          this.cdr.detectChanges();
        });
      },
      error: (err: HttpErrorResponse) => {
        this.ngZone.run(() => {
          this.error = this.apiErrorMessage(err, 'Could not load applications.');
          this.cdr.detectChanges();
        });
      }
    });
  }

  statusLabel(status: string): string {
    return status?.replace(/_/g, ' ') ?? status;
  }

  animalNameFor(animalId: string): string {
    return this.animalNames[animalId] ?? animalId;
  }

  private apiErrorMessage(err: HttpErrorResponse, fallback: string): string {
    if (err.status === 401) return 'Your session has expired. Please log in again.';
    if (err.status === 403) return 'You do not have permission to view this page.';
    return fallback;
  }
}
