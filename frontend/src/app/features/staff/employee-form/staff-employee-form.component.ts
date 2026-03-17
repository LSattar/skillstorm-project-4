import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { StaffEmployeesService } from '../../../core/services/staff-employees.service';
import type { CreateEmployeeRequest } from '../../../core/models/staff.model';

@Component({
  selector: 'app-staff-employee-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './staff-employee-form.component.html',
  styleUrl: './staff-employee-form.component.css'
})
export class StaffEmployeeFormComponent {
  private fb = inject(FormBuilder);
  private employeesService = inject(StaffEmployeesService);
  private router = inject(Router);

  form: FormGroup = this.fb.group({
    username: ['', [Validators.required, Validators.maxLength(255)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    displayName: [''],
    phone: ['']
  });

  saving = false;
  error: string | null = null;

  onSubmit(): void {
    if (this.form.invalid) return;
    this.error = null;
    this.saving = true;
    const value = this.form.value as CreateEmployeeRequest;
    this.employeesService.create(value).subscribe({
      next: (e) => this.router.navigate(['/staff/employees', e.id]),
      error: () => {
        this.error = 'Could not create employee.';
        this.saving = false;
      }
    });
  }
}
