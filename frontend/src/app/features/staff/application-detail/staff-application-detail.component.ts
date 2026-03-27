import { ChangeDetectorRef, Component, NgZone, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { catchError, forkJoin, of } from 'rxjs';
import { StaffApplicationsService } from '../../../core/services/staff-applications.service';
import { StaffAdoptionsService } from '../../../core/services/staff-adoptions.service';
import { StaffAnimalsService } from '../../../core/services/staff-animals.service';
import { StaffEmployeesService } from '../../../core/services/staff-employees.service';
import type { AdoptionApplicationResponse } from '../../../core/models/adopter.model';
import type { AnimalResponse, UserResponse } from '../../../core/models/staff.model';

@Component({
  selector: 'app-staff-application-detail',
  standalone: true,
  imports: [DatePipe, FormsModule, RouterLink],
  templateUrl: './staff-application-detail.component.html',
  styleUrl: './staff-application-detail.component.css'
})
export class StaffApplicationDetailComponent implements OnInit {
  private cdr = inject(ChangeDetectorRef);
  private ngZone = inject(NgZone);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private applicationsService = inject(StaffApplicationsService);
  private adoptionsService = inject(StaffAdoptionsService);
  private animalsService = inject(StaffAnimalsService);
  private employeesService = inject(StaffEmployeesService);

  application: AdoptionApplicationResponse | null = null;
  animalName = 'Unknown animal';
  adopterName = 'Unknown adopter';
  questionnaireEntries: Array<{ label: string; value: string }> = [];
  loading = true;
  error: string | null = null;
  decisionNotes = '';
  actionInProgress: 'approve' | 'deny' | 'finalize' | null = null;

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const id = params.get('id');
      this.loading = true;
      this.error = null;
      this.application = null;
      if (!id) {
        this.error = 'Invalid application.';
        this.loading = false;
        this.cdr.detectChanges();
        return;
      }
      this.applicationsService.get(id).subscribe({
        next: (app) => {
          this.ngZone.run(() => {
            this.application = app;
            this.questionnaireEntries = this.parseQuestionnaireSnapshot(app.questionnaireSnapshotJson);
            this.loadRelatedNames(app);
            this.cdr.detectChanges();
          });
        },
        error: () => {
          this.ngZone.run(() => {
            this.error = 'Could not load application.';
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
    });
  }

  statusLabel(s: string): string {
    return s?.replace(/_/g, ' ') ?? s;
  }

  approve(): void {
    if (!this.application) return;
    this.error = null;
    this.actionInProgress = 'approve';
    this.applicationsService.approve(this.application.id, { decisionNotes: this.decisionNotes || undefined }).subscribe({
      next: (app) => {
        this.ngZone.run(() => {
          this.application = app;
          this.actionInProgress = null;
          this.cdr.detectChanges();
        });
      },
      error: () => {
        this.ngZone.run(() => {
          this.error = 'Approve failed.';
          this.actionInProgress = null;
          this.cdr.detectChanges();
        });
      }
    });
  }

  deny(): void {
    if (!this.application) return;
    this.error = null;
    this.actionInProgress = 'deny';
    this.applicationsService.deny(this.application.id, { decisionNotes: this.decisionNotes || undefined }).subscribe({
      next: (app) => {
        this.ngZone.run(() => {
          this.application = app;
          this.actionInProgress = null;
          this.cdr.detectChanges();
        });
      },
      error: () => {
        this.ngZone.run(() => {
          this.error = 'Deny failed.';
          this.actionInProgress = null;
          this.cdr.detectChanges();
        });
      }
    });
  }

  finalize(): void {
    if (!this.application) return;
    this.error = null;
    this.actionInProgress = 'finalize';
    this.adoptionsService.finalize({ applicationId: this.application.id }).subscribe({
      next: () => this.router.navigate(['/staff/applications']),
      error: () => {
        this.ngZone.run(() => {
          this.error = 'Finalize adoption failed.';
          this.actionInProgress = null;
          this.cdr.detectChanges();
        });
      }
    });
  }

  get canFinalize(): boolean {
    return this.application?.status === 'APPROVED';
  }

  private loadRelatedNames(app: AdoptionApplicationResponse): void {
    forkJoin({
      animal: this.animalsService.get(app.animalId).pipe(catchError(() => of(null))),
      adopter: this.employeesService.get(app.adopterUserId).pipe(catchError(() => of(null)))
    }).subscribe({
      next: ({ animal, adopter }) => {
        this.ngZone.run(() => {
          this.animalName = this.resolveAnimalName(animal);
          this.adopterName = this.resolveUserName(adopter);
          this.cdr.detectChanges();
        });
      }
    });
  }

  private resolveAnimalName(animal: AnimalResponse | null): string {
    return animal?.name?.trim() || 'Unknown animal';
  }

  private resolveUserName(user: UserResponse | null): string {
    return user?.displayName?.trim() || user?.username?.trim() || 'Unknown adopter';
  }

  private parseQuestionnaireSnapshot(snapshot: string | null | undefined): Array<{ label: string; value: string }> {
    if (!snapshot) return [];

    try {
      const parsed = JSON.parse(snapshot);
      if (!parsed || typeof parsed !== 'object' || Array.isArray(parsed)) {
        return [{ label: 'Snapshot', value: String(snapshot) }];
      }

      const source = parsed as Record<string, unknown>;
      const preferredOrder = [
        'householdSize',
        'phone',
        'city',
        'state',
        'zip',
        'housingType',
        'hasYard',
        'hasKids',
        'hasOtherPets',
        'needsGoodWithKids',
        'needsGoodWithOtherPets',
        'willingMedicallyComplex',
        'notes'
      ];

      const orderedKeys = [
        ...preferredOrder.filter((key) => key in source),
        ...Object.keys(source).filter((key) => !preferredOrder.includes(key))
      ];

      return orderedKeys.map((key) => ({
        label: this.labelForKey(key),
        value: this.formatSnapshotValue(source[key])
      }));
    } catch {
      return [{ label: 'Snapshot', value: snapshot }];
    }
  }

  private labelForKey(key: string): string {
    const knownLabels: Record<string, string> = {
      householdSize: 'Household Size',
      housingType: 'Housing Type',
      hasYard: 'Has Yard',
      hasKids: 'Has Kids',
      hasOtherPets: 'Has Other Pets',
      needsGoodWithKids: 'Needs Good With Kids',
      needsGoodWithOtherPets: 'Needs Good With Other Pets',
      willingMedicallyComplex: 'Willing to Adopt Medically Complex Animal'
    };

    if (knownLabels[key]) return knownLabels[key];
    return key.replace(/([A-Z])/g, ' $1').replace(/^./, (char) => char.toUpperCase());
  }

  private formatSnapshotValue(value: unknown): string {
    if (value === null || value === undefined || value === '') return 'Not provided';
    if (typeof value === 'boolean') return value ? 'Yes' : 'No';
    if (typeof value === 'string') return value;
    if (typeof value === 'number') return String(value);
    return JSON.stringify(value);
  }
}
