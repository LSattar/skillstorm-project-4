import { ChangeDetectorRef, Component, NgZone, OnInit, inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { finalize, timeout } from 'rxjs';
import { AdopterService } from '../../../core/services/adopter.service';
import { AnimalsService } from '../../../core/services/animals.service';
import type { AdoptionApplicationResponse } from '../../../core/models/adopter.model';
import type { Animal } from '../../../core/models/animal.model';

@Component({
  selector: 'app-adopter-application-detail',
  standalone: true,
  imports: [DatePipe, RouterLink],
  templateUrl: './adopter-application-detail.component.html',
  styleUrl: './adopter-application-detail.component.css'
})
export class AdopterApplicationDetailComponent implements OnInit {
  private cdr = inject(ChangeDetectorRef);
  private ngZone = inject(NgZone);
  private route = inject(ActivatedRoute);
  private adopterService = inject(AdopterService);
  private animalsService = inject(AnimalsService);

  application: AdoptionApplicationResponse | null = null;
  animalName = 'Unknown animal';
  questionnaireEntries: Array<{ label: string; value: string }> = [];
  loading = true;
  error: string | null = null;

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const id = params.get('id');
      this.loading = true;
      this.error = null;
      this.application = null;
      this.animalName = 'Unknown animal';
      this.questionnaireEntries = [];
      if (!id) {
        this.error = 'Invalid application.';
        this.loading = false;
        this.cdr.detectChanges();
        return;
      }
      this.adopterService.getApplication(id).pipe(
        timeout(10000),
        finalize(() => {
          this.ngZone.run(() => {
            this.loading = false;
            this.cdr.detectChanges();
          });
        })
      ).subscribe({
        next: (app) => {
          this.ngZone.run(() => {
            this.application = app;
            this.questionnaireEntries = this.parseQuestionnaireSnapshot(app.questionnaireSnapshotJson);
            this.cdr.detectChanges();
          });
          this.loadAnimalName(app.animalId);
        },
        error: (err: unknown) => {
          this.ngZone.run(() => {
            this.error = this.apiErrorMessage(err, 'Could not load application.');
            this.cdr.detectChanges();
          });
        }
      });
    });
  }

  statusLabel(status: string): string {
    return status?.replace(/_/g, ' ') ?? status;
  }

  private loadAnimalName(animalId: string): void {
    this.animalsService.getAnimal(animalId).pipe(timeout(10000)).subscribe({
      next: (animal: Animal) => {
        this.ngZone.run(() => {
          this.animalName = animal.name?.trim() || 'Unknown animal';
          this.cdr.detectChanges();
        });
      },
      error: () => {
        this.ngZone.run(() => {
          this.animalName = 'Unknown animal';
          this.cdr.detectChanges();
        });
      }
    });
  }

  private parseQuestionnaireSnapshot(snapshot: string | null | undefined): Array<{ label: string; value: string }> {
    if (!snapshot) return [];

    try {
      const parsed = JSON.parse(snapshot);
      if (!parsed || typeof parsed !== 'object' || Array.isArray(parsed)) {
        return [{ label: 'Snapshot', value: String(snapshot) }];
      }

      const source = parsed as Record<string, unknown>;
      const orderedKeys = [
        'householdSize',
        'housingType',
        'hasYard',
        'hasKids',
        'hasOtherPets',
        'needsGoodWithKids',
        'needsGoodWithOtherPets',
        'willingMedicallyComplex',
        'phone',
        'city',
        'state',
        'zip',
        'notes'
      ];

      const keys = [
        ...orderedKeys.filter((key) => key in source),
        ...Object.keys(source).filter((key) => !orderedKeys.includes(key))
      ];

      return keys.map((key) => ({
        label: this.labelForKey(key),
        value: this.formatValue(source[key])
      }));
    } catch {
      return [{ label: 'Snapshot', value: snapshot }];
    }
  }

  private labelForKey(key: string): string {
    const customLabels: Record<string, string> = {
      householdSize: 'Household Size',
      housingType: 'Housing Type',
      hasYard: 'Has Yard',
      hasKids: 'Has Kids',
      hasOtherPets: 'Has Other Pets',
      needsGoodWithKids: 'Needs Good With Kids',
      needsGoodWithOtherPets: 'Needs Good With Other Pets',
      willingMedicallyComplex: 'Willing to Adopt Medically Complex Animal'
    };
    if (customLabels[key]) return customLabels[key];
    return key.replace(/([A-Z])/g, ' $1').replace(/^./, (char) => char.toUpperCase());
  }

  private formatValue(value: unknown): string {
    if (value === null || value === undefined || value === '') return 'Not provided';
    if (typeof value === 'boolean') return value ? 'Yes' : 'No';
    if (typeof value === 'number') return String(value);
    if (typeof value === 'string') return value;
    return JSON.stringify(value);
  }

  private apiErrorMessage(err: unknown, fallback: string): string {
    if (err && typeof err === 'object' && 'name' in err && (err as { name?: string }).name === 'TimeoutError') {
      return 'Application details took too long to load. Please try again.';
    }
    if (!(err instanceof HttpErrorResponse)) {
      return fallback;
    }
    if (err.status === 401) return 'Your session has expired. Please log in again.';
    if (err.status === 403) return 'You do not have permission to view this application.';
    return fallback;
  }
}
