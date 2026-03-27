import { ChangeDetectorRef, Component, NgZone, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { StaffAnimalsService } from '../../../core/services/staff-animals.service';
import { StaffSheltersService } from '../../../core/services/staff-shelters.service';
import { StaffEmployeesService } from '../../../core/services/staff-employees.service';
import type { ShelterResponse, UserResponse } from '../../../core/models/staff.model';

@Component({
  selector: 'app-staff-animal-move',
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule, RouterLink],
  templateUrl: './staff-animal-move.component.html',
  styleUrl: './staff-animal-move.component.css'
})
export class StaffAnimalMoveComponent implements OnInit {
  private cdr = inject(ChangeDetectorRef);
  private ngZone = inject(NgZone);
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private animalsService = inject(StaffAnimalsService);
  private sheltersService = inject(StaffSheltersService);
  private employeesService = inject(StaffEmployeesService);
  private router = inject(Router);

  animalId: string | null = null;
  animalName = '';
  shelters: ShelterResponse[] = [];
  fosters: UserResponse[] = [];
  loading = true;
  submitting = false;
  error: string | null = null;

  moveShelterForm: FormGroup = this.fb.group({
    toShelterId: [null as number | null],
    notes: ['']
  });
  moveFosterForm: FormGroup = this.fb.group({
    toFosterUserId: [null as string | null],
    notes: ['']
  });

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      this.animalId = params.get('id');
      this.loading = true;
      this.error = null;
      this.animalName = '';
      if (!this.animalId) {
        this.error = 'Invalid animal.';
        this.loading = false;
        this.cdr.detectChanges();
        return;
      }
      this.animalsService.get(this.animalId).subscribe({
        next: (a) => {
          this.ngZone.run(() => {
            this.animalName = a.name ?? '';
            this.cdr.detectChanges();
          });
        },
        error: () => {
          this.ngZone.run(() => {
            this.error = 'Could not load animal.';
            this.loading = false;
            this.cdr.detectChanges();
          });
        }
      });
      this.sheltersService.list().subscribe({
        next: (list) => {
          this.ngZone.run(() => {
            this.shelters = list;
            this.cdr.detectChanges();
          });
        },
        error: () => {}
      });
      this.employeesService.list().subscribe({
        next: (list) => {
          this.ngZone.run(() => {
            this.fosters = list.filter((u) => u.roles?.includes('FOSTER'));
            this.cdr.detectChanges();
          });
        },
        error: () => {},
        complete: () => {
          this.ngZone.run(() => {
            this.loading = false;
            this.cdr.detectChanges();
          });
        }
      });
    });
  }

  moveToShelter(): void {
    const toShelterId = this.moveShelterForm.get('toShelterId')?.value;
    if (toShelterId == null || !this.animalId) return;
    this.error = null;
    this.submitting = true;
    this.animalsService
      .moveToShelter(this.animalId, { toShelterId: Number(toShelterId), notes: this.moveShelterForm.get('notes')?.value || null })
      .pipe(
        finalize(() => {
          this.ngZone.run(() => {
            this.submitting = false;
            this.cdr.detectChanges();
          });
        })
      )
      .subscribe({
        next: () => this.router.navigate(['/staff/animals', this.animalId, 'edit']),
        error: () => {
          this.ngZone.run(() => {
            this.error = 'Move failed.';
            this.cdr.detectChanges();
          });
        }
      });
  }

  moveToFoster(): void {
    const toFosterUserId = this.moveFosterForm.get('toFosterUserId')?.value;
    if (!toFosterUserId || !this.animalId) return;
    this.error = null;
    this.submitting = true;
    this.animalsService
      .moveToFoster(this.animalId, { toFosterUserId, notes: this.moveFosterForm.get('notes')?.value || null })
      .pipe(
        finalize(() => {
          this.ngZone.run(() => {
            this.submitting = false;
            this.cdr.detectChanges();
          });
        })
      )
      .subscribe({
        next: () => this.router.navigate(['/staff/animals', this.animalId, 'edit']),
        error: () => {
          this.ngZone.run(() => {
            this.error = 'Move failed.';
            this.cdr.detectChanges();
          });
        }
      });
  }

}
