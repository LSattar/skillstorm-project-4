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
    questionnaireSnapshotJson: ['']
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
    this.adopterService
      .createApplication({
        animalId: value.animalId ?? '',
        questionnaireSnapshotJson: value.questionnaireSnapshotJson?.trim() || null
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
