import { ChangeDetectorRef, Component, NgZone, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { StaffAnimalsService } from '../../../core/services/staff-animals.service';
import type { UpdateAnimalRequest } from '../../../core/models/staff.model';

const SPECIES = ['DOG', 'CAT', 'OTHER'];
const SEX = ['MALE', 'FEMALE'];
const STATUSES = ['IN_SHELTER', 'IN_FOSTER', 'ADOPTION_PENDING', 'ADOPTED', 'ON_HOLD', 'INACTIVE', 'TRANSFERRED'];

@Component({
  selector: 'app-staff-animal-edit',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './staff-animal-edit.component.html',
  styleUrl: './staff-animal-edit.component.css'
})
export class StaffAnimalEditComponent implements OnInit {
  private cdr = inject(ChangeDetectorRef);
  private ngZone = inject(NgZone);
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private animalsService = inject(StaffAnimalsService);
  private router = inject(Router);

  form!: FormGroup;
  animalId: string | null = null;
  loading = true;
  saving = false;
  error: string | null = null;
  success = false;
  speciesOptions = SPECIES;
  sexOptions = SEX;
  statusOptions = STATUSES;

  ngOnInit(): void {
    this.form = this.fb.group({
      name: [''],
      species: [''],
      breed: [''],
      sex: [''],
      ageMonths: [null as number | null],
      goodWithKids: [false],
      goodWithOtherPets: [false],
      medicallyComplex: [false],
      description: [''],
      status: [''],
      currentShelterId: [null as number | null],
      currentFosterUserId: [null as string | null]
    });
    this.route.paramMap.subscribe((params) => {
      this.animalId = params.get('id');
      this.loading = true;
      this.error = null;
      this.success = false;
      if (!this.animalId) {
        this.error = 'Invalid animal.';
        this.loading = false;
        this.cdr.detectChanges();
        return;
      }
      this.animalsService.get(this.animalId).subscribe({
        next: (a) => {
          this.ngZone.run(() => {
            this.form.patchValue({
              name: a.name ?? '',
              species: a.species ?? '',
              breed: a.breed ?? '',
              sex: a.sex ?? '',
              ageMonths: a.ageMonths ?? null,
              goodWithKids: a.goodWithKids ?? false,
              goodWithOtherPets: a.goodWithOtherPets ?? false,
              medicallyComplex: a.medicallyComplex ?? false,
              description: a.description ?? '',
              status: a.status ?? '',
              currentShelterId: a.currentShelterId ?? null,
              currentFosterUserId: a.currentFosterUserId ?? null
            });
            this.cdr.detectChanges();
          });
        },
        error: () => {
          this.ngZone.run(() => {
            this.error = 'Could not load animal.';
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

  onSubmit(): void {
    if (!this.animalId || this.form.invalid) return;
    this.error = null;
    this.success = false;
    this.saving = true;
    const value = this.form.value as UpdateAnimalRequest;
    this.animalsService.update(this.animalId, value).pipe(
      finalize(() => {
        this.ngZone.run(() => {
          this.saving = false;
          this.cdr.detectChanges();
        });
      })
    ).subscribe({
      next: () => {
        this.ngZone.run(() => {
          this.success = true;
          this.cdr.detectChanges();
        });
      },
      error: () => {
        this.ngZone.run(() => {
          this.error = 'Could not update animal.';
          this.cdr.detectChanges();
        });
      }
    });
  }
}
