import { Component, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { StaffEmployeesService } from '../../../core/services/staff-employees.service';
import type { UserResponse, UpdateEmployeeRequest } from '../../../core/models/staff.model';

@Component({
  selector: 'app-staff-employee-detail',
  standalone: true,
  imports: [DatePipe, ReactiveFormsModule, RouterLink],
  templateUrl: './staff-employee-detail.component.html',
  styleUrl: './staff-employee-detail.component.css'
})
export class StaffEmployeeDetailComponent implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private employeesService = inject(StaffEmployeesService);

  employee: UserResponse | null = null;
  form!: FormGroup;
  loading = true;
  saving = false;
  error: string | null = null;
  actionInProgress: string | null = null;

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.error = 'Invalid employee.';
      this.loading = false;
      return;
    }
    this.form = this.fb.group({
      email: [''],
      displayName: [''],
      phone: ['']
    });
    this.employeesService.get(id).subscribe({
      next: (e) => {
        this.employee = e;
        this.form.patchValue({
          email: e.email ?? '',
          displayName: e.displayName ?? '',
          phone: e.phone ?? ''
        });
      },
      error: () => {
        this.error = 'Could not load employee.';
        this.loading = false;
      },
      complete: () => (this.loading = false)
    });
  }

  onSubmit(): void {
    if (!this.employee) return;
    this.error = null;
    this.saving = true;
    const value = this.form.value as UpdateEmployeeRequest;
    this.employeesService.update(this.employee.id, value).subscribe({
      next: (e) => {
        this.employee = e;
        this.saving = false;
      },
      error: () => {
        this.error = 'Could not update employee.';
        this.saving = false;
      }
    });
  }

  deactivate(): void {
    if (!this.employee) return;
    this.error = null;
    this.actionInProgress = 'deactivate';
    this.employeesService.deactivate(this.employee.id).subscribe({
      next: () => this.employeesService.get(this.employee!.id).subscribe((e) => (this.employee = e)),
      error: () => (this.error = 'Deactivate failed.'),
      complete: () => (this.actionInProgress = null)
    });
  }

  reactivate(): void {
    if (!this.employee) return;
    this.error = null;
    this.actionInProgress = 'reactivate';
    this.employeesService.reactivate(this.employee.id).subscribe({
      next: () => this.employeesService.get(this.employee!.id).subscribe((e) => (this.employee = e)),
      error: () => (this.error = 'Reactivate failed.'),
      complete: () => (this.actionInProgress = null)
    });
  }

  resetPassword(): void {
    if (!this.employee) return;
    this.error = null;
    this.actionInProgress = 'reset';
    this.employeesService.resetPassword(this.employee.id).subscribe({
      next: () => (this.actionInProgress = null),
      error: () => {
        this.error = 'Reset password failed.';
        this.actionInProgress = null;
      }
    });
  }
}
