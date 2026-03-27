import { ChangeDetectorRef, Component, NgZone, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AdopterService } from '../../../core/services/adopter.service';
import { AnimalsService } from '../../../core/services/animals.service';
import type { Animal } from '../../../core/models/animal.model';

@Component({
  selector: 'app-adopter-application-create',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './adopter-application-create.component.html',
  styleUrl: './adopter-application-create.component.css'
})
export class AdopterApplicationCreateComponent implements OnInit {
  private cdr = inject(ChangeDetectorRef);
  private ngZone = inject(NgZone);
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private adopterService = inject(AdopterService);
  private animalsService = inject(AnimalsService);

  loading = false;
  animalsLoading = true;
  error: string | null = null;
  submitted = false;
  animals: Animal[] = [];

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
    this.animalsService.getAnimals().subscribe({
      next: (animals) => {
        this.ngZone.run(() => {
          this.animals = animals.filter((a) => this.isAvailableForAdoption(a));
          this.animalsLoading = false;
          this.cdr.detectChanges();
        });
      },
      error: () => {
        this.ngZone.run(() => {
          this.error = 'Could not load animals.';
          this.animalsLoading = false;
          this.cdr.detectChanges();
        });
      }
    });

    this.adopterService.getProfile().subscribe({
      next: (profile) => {
        this.ngZone.run(() => {
          this.form.patchValue({
            questionnaireHouseholdSize: profile.householdSize ?? null,
            questionnaireCity: profile.city ?? '',
            questionnaireState: profile.state ?? '',
            questionnaireZip: profile.zip ?? '',
            questionnaireHousingType: profile.housingType ?? '',
            questionnaireHasYard: profile.hasYard ?? false,
            questionnaireHasKids: profile.hasKids ?? false,
            questionnaireHasOtherPets: profile.hasOtherPets ?? false,
            questionnaireNeedsGoodWithKids: profile.needsGoodWithKids ?? false,
            questionnaireNeedsGoodWithOtherPets: profile.needsGoodWithOtherPets ?? false,
            questionnaireWillingMedicallyComplex: profile.willingMedicallyComplex ?? false,
            questionnaireNotes: profile.notes ?? ''
          });
          this.cdr.detectChanges();
        });
      },
      error: () => {
        // Keep the form usable if profile does not exist yet.
      }
    });

    this.route.queryParamMap.subscribe((params) => {
      const animalId = params.get('animalId');
      if (!animalId) return;
      // Fetch URL-selected animal immediately so apply links prefill reliably.
      this.populateAnimalFromUrl(animalId);
      if (this.animals.some((a) => a.id === animalId)) {
        this.form.patchValue({ animalId });
      }
    });
  }

  get selectedAnimalName(): string {
    const animalId = this.form.get('animalId')?.value ?? '';
    if (!animalId) return '';
    return this.animals.find((a) => a.id === animalId)?.name ?? '';
  }

  // Backward-compatible alias in case stale template is cached during HMR.
  get animalName(): string {
    return this.selectedAnimalName || 'Loading animal...';
  }

  private isAvailableForAdoption(animal: Animal): boolean {
    return !['ADOPTED', 'INACTIVE', 'TRANSFERRED'].includes(animal.status);
  }

  private populateAnimalFromUrl(animalId: string): void {
    this.animalsService.getAnimal(animalId).subscribe({
      next: (animal) => {
        this.ngZone.run(() => {
          const exists = this.animals.some((a) => a.id === animal.id);
          if (!exists) {
            this.animals = [animal, ...this.animals];
          }
          this.form.patchValue({ animalId: animal.id });
          this.animalsLoading = false;
          this.cdr.detectChanges();
        });
      },
      error: () => {
        // Keep list loading/error handling as the primary message.
      }
    });
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
      .pipe(
        finalize(() => {
          this.ngZone.run(() => {
            this.loading = false;
            this.cdr.detectChanges();
          });
        })
      )
      .subscribe({
        next: () => {
          this.ngZone.run(() => {
            this.submitted = true;
            this.cdr.detectChanges();
          });
        },
        error: () => {
          this.ngZone.run(() => {
            this.error = 'Could not submit application.';
            this.cdr.detectChanges();
          });
        }
      });
  }
}
