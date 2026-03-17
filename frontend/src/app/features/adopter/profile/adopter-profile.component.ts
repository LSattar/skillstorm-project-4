import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { AdopterService } from '../../../core/services/adopter.service';
import type { AdopterProfileResponse, UpdateAdopterProfileRequest } from '../../../core/models/adopter.model';

const HOUSING_TYPES = ['HOUSE', 'APARTMENT', 'CONDO', 'OTHER'];

@Component({
  selector: 'app-adopter-profile',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './adopter-profile.component.html',
  styleUrl: './adopter-profile.component.css'
})
export class AdopterProfileComponent implements OnInit {
  private fb = inject(FormBuilder);
  private adopterService = inject(AdopterService);

  form!: FormGroup;
  loading = true;
  saving = false;
  error: string | null = null;
  success = false;
  housingTypes = HOUSING_TYPES;

  ngOnInit(): void {
    this.form = this.fb.group({
      addressLine1: [''],
      addressLine2: [''],
      city: [''],
      state: [''],
      zip: [''],
      householdSize: [null as number | null],
      housingType: [''],
      hasYard: [false],
      hasKids: [false],
      hasOtherPets: [false],
      needsGoodWithKids: [false],
      needsGoodWithOtherPets: [false],
      willingMedicallyComplex: [false],
      notes: ['']
    });
    this.adopterService.getProfile().subscribe({
      next: (p) => this.patchForm(p),
      error: () => {
        this.error = 'Could not load profile.';
        this.loading = false;
      },
      complete: () => (this.loading = false)
    });
  }

  private patchForm(p: AdopterProfileResponse): void {
    this.form.patchValue({
      addressLine1: p.addressLine1 ?? '',
      addressLine2: p.addressLine2 ?? '',
      city: p.city ?? '',
      state: p.state ?? '',
      zip: p.zip ?? '',
      householdSize: p.householdSize ?? null,
      housingType: p.housingType ?? '',
      hasYard: p.hasYard ?? false,
      hasKids: p.hasKids ?? false,
      hasOtherPets: p.hasOtherPets ?? false,
      needsGoodWithKids: p.needsGoodWithKids ?? false,
      needsGoodWithOtherPets: p.needsGoodWithOtherPets ?? false,
      willingMedicallyComplex: p.willingMedicallyComplex ?? false,
      notes: p.notes ?? ''
    });
  }

  onSubmit(): void {
    this.error = null;
    this.success = false;
    this.saving = true;
    const value = this.form.value as UpdateAdopterProfileRequest;
    this.adopterService.updateProfile(value).subscribe({
      next: () => {
        this.success = true;
        this.saving = false;
      },
      error: () => {
        this.error = 'Could not save profile.';
        this.saving = false;
      }
    });
  }
}
