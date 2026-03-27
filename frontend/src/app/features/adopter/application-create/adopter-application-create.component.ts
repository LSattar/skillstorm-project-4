import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AdopterService } from '../../../core/services/adopter.service';

@Component({
  selector: 'app-adopter-application-create',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './adopter-application-create.component.html',
  styleUrl: './adopter-application-create.component.css'
})
export class AdopterApplicationCreateComponent implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private adopterService = inject(AdopterService);

  loading = false;
  error: string | null = null;
  submitted = false;

  form = this.fb.group({
    animalId: ['', Validators.required],
    questionnaireHouseholdSize: [null as number | null],
    questionnairePhone: [''],
    questionnaireCity: [''],
    questionnaireState: [''],
    questionnaireZip: [''],
    questionnaireHousingType: [''],
    questionnaireHasYard: [false],
    questionnaireHasKids: [false],
    questionnaireHasOtherPets: [false],
    questionnaireNeedsGoodWithKids: [false],
    questionnaireNeedsGoodWithOtherPets: [false],
    questionnaireWillingMedicallyComplex: [false],
    questionnaireNotes: ['']
  });

  ngOnInit(): void {
    const animalId = this.route.snapshot.queryParamMap.get('animalId');
    if (animalId) {
      this.form.patchValue({ animalId });
    }
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.error = null;
    this.submitted = false;

    const value = this.form.getRawValue();
    const hasQuestionnaire =
      value.questionnaireHouseholdSize != null ||
      !!value.questionnairePhone ||
      !!value.questionnaireCity ||
      !!value.questionnaireState ||
      !!value.questionnaireZip ||
      !!value.questionnaireHousingType ||
      !!value.questionnaireNotes ||
      value.questionnaireHasYard ||
      value.questionnaireHasKids ||
      value.questionnaireHasOtherPets ||
      value.questionnaireNeedsGoodWithKids ||
      value.questionnaireNeedsGoodWithOtherPets ||
      value.questionnaireWillingMedicallyComplex;

    this.adopterService
      .createApplication({
        animalId: value.animalId ?? '',
        questionnaireAnswers: hasQuestionnaire
          ? {
              schemaVersion: 1,
              householdSize: value.questionnaireHouseholdSize ?? null,
              phone: value.questionnairePhone?.trim() || null,
              city: value.questionnaireCity?.trim() || null,
              state: value.questionnaireState?.trim() || null,
              zip: value.questionnaireZip?.trim() || null,
              housingType: value.questionnaireHousingType?.trim() || null,
              hasYard: value.questionnaireHasYard ?? null,
              hasKids: value.questionnaireHasKids ?? null,
              hasOtherPets: value.questionnaireHasOtherPets ?? null,
              needsGoodWithKids: value.questionnaireNeedsGoodWithKids ?? null,
              needsGoodWithOtherPets: value.questionnaireNeedsGoodWithOtherPets ?? null,
              willingMedicallyComplex: value.questionnaireWillingMedicallyComplex ?? null,
              notes: value.questionnaireNotes?.trim() || null
            }
          : null
      })
      .subscribe({
        next: (app) => {
          this.submitted = true;
          this.router.navigate(['/adopter/applications', app.id]);
        },
        error: () => {
          this.error = 'Could not submit application.';
          this.loading = false;
        },
        complete: () => {
          this.loading = false;
        }
      });
  }
}
