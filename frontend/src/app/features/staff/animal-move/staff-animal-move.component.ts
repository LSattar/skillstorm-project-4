import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { StaffAnimalsService } from '../../../core/services/staff-animals.service';
import { StaffSheltersService } from '../../../core/services/staff-shelters.service';
import { StaffEmployeesService } from '../../../core/services/staff-employees.service';
import type { ShelterResponse, UserResponse } from '../../../core/models/staff.model';

const STATUSES = ['IN_SHELTER', 'IN_FOSTER', 'ADOPTION_PENDING', 'ON_HOLD', 'INACTIVE', 'TRANSFERRED'];

@Component({
  selector: 'app-staff-animal-move',
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule, RouterLink],
  templateUrl: './staff-animal-move.component.html',
  styleUrl: './staff-animal-move.component.css'
})
export class StaffAnimalMoveComponent implements OnInit {
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
  statusOptions = STATUSES;

  moveShelterForm: FormGroup = this.fb.group({
    toShelterId: [null as number | null],
    notes: ['']
  });
  moveFosterForm: FormGroup = this.fb.group({
    toFosterUserId: [null as string | null],
    notes: ['']
  });
  statusForm: FormGroup = this.fb.group({
    status: [''],
    notes: ['']
  });

  ngOnInit(): void {
    this.animalId = this.route.snapshot.paramMap.get('id');
    if (!this.animalId) {
      this.error = 'Invalid animal.';
      this.loading = false;
      return;
    }
    this.animalsService.get(this.animalId).subscribe({
      next: (a) => (this.animalName = a.name ?? ''),
      error: () => {
        this.error = 'Could not load animal.';
        this.loading = false;
      }
    });
    this.sheltersService.list().subscribe({
      next: (list) => (this.shelters = list),
      error: () => {}
    });
    this.employeesService.list().subscribe({
      next: (list) => (this.fosters = list.filter((u) => u.roles?.includes('FOSTER'))),
      error: () => {},
      complete: () => (this.loading = false)
    });
  }

  moveToShelter(): void {
    const toShelterId = this.moveShelterForm.get('toShelterId')?.value;
    if (toShelterId == null || !this.animalId) return;
    this.error = null;
    this.submitting = true;
    this.animalsService
      .moveToShelter(this.animalId, { toShelterId: Number(toShelterId), notes: this.moveShelterForm.get('notes')?.value || null })
      .subscribe({
        next: () => this.router.navigate(['/staff/animals', this.animalId, 'edit']),
        error: () => {
          this.error = 'Move failed.';
          this.submitting = false;
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
      .subscribe({
        next: () => this.router.navigate(['/staff/animals', this.animalId, 'edit']),
        error: () => {
          this.error = 'Move failed.';
          this.submitting = false;
        }
      });
  }

  updateStatus(): void {
    const status = this.statusForm.get('status')?.value;
    if (!status || !this.animalId) return;
    this.error = null;
    this.submitting = true;
    this.animalsService
      .updateStatus(this.animalId, { status, notes: this.statusForm.get('notes')?.value || null })
      .subscribe({
        next: () => this.router.navigate(['/staff/animals', this.animalId, 'edit']),
        error: () => {
          this.error = 'Status update failed.';
          this.submitting = false;
        }
      });
  }
}
