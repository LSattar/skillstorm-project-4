import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { AdopterService } from '../../../core/services/adopter.service';
import type { AdopterQuestionnaireResponse, UpsertQuestionnaireRequest } from '../../../core/models/adopter.model';

const HOUSING_TYPES = ['HOUSE', 'APARTMENT', 'CONDO', 'OTHER'];

@Component({
  selector: 'app-adopter-questionnaire',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './adopter-questionnaire.component.html',
  styleUrl: './adopter-questionnaire.component.css'
})
export class AdopterQuestionnaireComponent implements OnInit {
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
      schemaVersion: [1],
      householdSize: [null as number | null],
      housingType: [''],
      hasYard: [false],
      hasKids: [false],
      hasOtherPets: [false],
      needsGoodWithKids: [false],
      needsGoodWithOtherPets: [false],
      willingMedicallyComplex: [false],
      notes: [''],
      phone: [''],
      city: [''],
      state: [''],
      zip: ['']
    });
    this.adopterService.getQuestionnaire().subscribe({
      next: (q) => this.patchForm(q),
      error: () => {
        this.error = 'Could not load questionnaire. You may need to fill it out first.';
        this.loading = false;
      },
      complete: () => (this.loading = false)
    });
  }

  private patchForm(q: AdopterQuestionnaireResponse): void {
    this.form.patchValue({
      schemaVersion: q.schemaVersion ?? 1,
      householdSize: q.householdSize ?? null,
      housingType: q.housingType ?? '',
      hasYard: q.hasYard ?? false,
      hasKids: q.hasKids ?? false,
      hasOtherPets: q.hasOtherPets ?? false,
      needsGoodWithKids: q.needsGoodWithKids ?? false,
      needsGoodWithOtherPets: q.needsGoodWithOtherPets ?? false,
      willingMedicallyComplex: q.willingMedicallyComplex ?? false,
      notes: q.notes ?? '',
      phone: q.phone ?? '',
      city: q.city ?? '',
      state: q.state ?? '',
      zip: q.zip ?? ''
    });
  }

  onSubmit(): void {
    this.error = null;
    this.success = false;
    this.saving = true;
    const value = this.form.value as UpsertQuestionnaireRequest;
    this.adopterService.upsertQuestionnaire(value).subscribe({
      next: () => {
        this.success = true;
        this.saving = false;
      },
      error: () => {
        this.error = 'Could not save questionnaire.';
        this.saving = false;
      }
    });
  }
}
