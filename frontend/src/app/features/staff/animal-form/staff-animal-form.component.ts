import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { StaffAnimalsService } from '../../../core/services/staff-animals.service';
import type { CreateAnimalRequest } from '../../../core/models/staff.model';

const SPECIES = ['DOG', 'CAT', 'OTHER'];
const SEX = ['MALE', 'FEMALE'];
const STATUSES = ['IN_SHELTER', 'IN_FOSTER', 'ON_HOLD', 'INACTIVE'];

@Component({
  selector: 'app-staff-animal-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './staff-animal-form.component.html',
  styleUrl: './staff-animal-form.component.css'
})
export class StaffAnimalFormComponent {
  private fb = inject(FormBuilder);
  private animalsService = inject(StaffAnimalsService);
  private router = inject(Router);

  form: FormGroup = this.fb.group({
    name: ['', [Validators.required, Validators.maxLength(255)]],
    species: ['DOG', [Validators.required]],
    breed: [''],
    sex: [''],
    ageMonths: [null as number | null],
    goodWithKids: [false],
    goodWithOtherPets: [false],
    medicallyComplex: [false],
    description: [''],
    status: ['IN_SHELTER', [Validators.required]],
    currentShelterId: [null as number | null],
    currentFosterUserId: [null as string | null]
  });

  speciesOptions = SPECIES;
  sexOptions = SEX;
  statusOptions = STATUSES;
  saving = false;
  error: string | null = null;

  onSubmit(): void {
    if (this.form.invalid) return;
    this.error = null;
    this.saving = true;
    const value = this.form.value as CreateAnimalRequest;
    this.animalsService.create(value).pipe(
      finalize(() => {
        this.saving = false;
      })
    ).subscribe({
      next: (animal) => {
        this.router.navigate(['/staff/animals', animal.id, 'edit']);
      },
      error: () => {
        this.error = 'Could not create animal.';
      }
    });
  }
}
