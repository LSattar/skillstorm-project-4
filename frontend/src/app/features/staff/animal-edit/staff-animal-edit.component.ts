import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
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
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private animalsService = inject(StaffAnimalsService);
  private router = inject(Router);

  form!: FormGroup;
  animalId: string | null = null;
  loading = true;
  saving = false;
  error: string | null = null;
  speciesOptions = SPECIES;
  sexOptions = SEX;
  statusOptions = STATUSES;

  ngOnInit(): void {
    this.animalId = this.route.snapshot.paramMap.get('id');
    if (!this.animalId) {
      this.error = 'Invalid animal.';
      this.loading = false;
      return;
    }
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
    this.animalsService.get(this.animalId).subscribe({
      next: (a) => {
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
      },
      error: () => {
        this.error = 'Could not load animal.';
        this.loading = false;
      },
      complete: () => (this.loading = false)
    });
  }

  onSubmit(): void {
    if (!this.animalId || this.form.invalid) return;
    this.error = null;
    this.saving = true;
    const value = this.form.value as UpdateAnimalRequest;
    this.animalsService.update(this.animalId, value).subscribe({
      next: () => {
        this.saving = false;
      },
      error: () => {
        this.error = 'Could not update animal.';
        this.saving = false;
      }
    });
  }
}
