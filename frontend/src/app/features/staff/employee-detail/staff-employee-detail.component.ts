import { ChangeDetectorRef, Component, NgZone, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
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
  private cdr = inject(ChangeDetectorRef);
  private ngZone = inject(NgZone);
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private employeesService = inject(StaffEmployeesService);

  employee: UserResponse | null = null;
  form!: FormGroup;
  loading = true;
  saving = false;
  error: string | null = null;
  success = false;
  actionInProgress: string | null = null;

  ngOnInit(): void {
    this.form = this.fb.group({
      email: [''],
      displayName: [''],
      phone: ['']
    });
    this.route.paramMap.subscribe((params) => {
      const id = params.get('id');
      this.loading = true;
      this.error = null;
      this.success = false;
      this.employee = null;
      if (!id) {
        this.error = 'Invalid employee.';
        this.loading = false;
        this.cdr.detectChanges();
        return;
      }
      this.employeesService.get(id).subscribe({
        next: (e) => {
          this.ngZone.run(() => {
            this.employee = e;
            this.form.patchValue({
              email: e.email ?? '',
              displayName: e.displayName ?? '',
              phone: e.phone ?? ''
            });
            this.cdr.detectChanges();
          });
        },
        error: () => {
          this.ngZone.run(() => {
            this.error = 'Could not load employee.';
            this.loading = false;
            this.cdr.detectChanges();
          });
        },
        complete: () => {
          this.ngZone.run(() => {
            this.loading = false;
            this.cdr.detectChanges();
          });
        }
      });
    });
  }

  onSubmit(): void {
    if (!this.employee) return;
    this.error = null;
    this.success = false;
    this.saving = true;
    const value = this.form.value as UpdateEmployeeRequest;
    this.employeesService.update(this.employee.id, value).pipe(
      finalize(() => {
        this.ngZone.run(() => {
          this.saving = false;
          this.cdr.detectChanges();
        });
      })
    ).subscribe({
      next: (e) => {
        this.ngZone.run(() => {
          this.employee = e;
          this.success = true;
          this.cdr.detectChanges();
        });
      },
      error: () => {
        this.ngZone.run(() => {
          this.error = 'Could not update employee.';
          this.cdr.detectChanges();
        });
      }
    });
  }

  deactivate(): void {
    if (!this.employee) return;
    this.error = null;
    this.actionInProgress = 'deactivate';
    this.employeesService.deactivate(this.employee.id).subscribe({
      next: () => this.employeesService.get(this.employee!.id).subscribe((e) => {
        this.ngZone.run(() => {
          this.employee = e;
          this.cdr.detectChanges();
        });
      }),
      error: () => {
        this.ngZone.run(() => {
          this.error = 'Deactivate failed.';
          this.cdr.detectChanges();
        });
      },
      complete: () => {
        this.ngZone.run(() => {
          this.actionInProgress = null;
          this.cdr.detectChanges();
        });
      }
    });
  }

  reactivate(): void {
    if (!this.employee) return;
    this.error = null;
    this.actionInProgress = 'reactivate';
    this.employeesService.reactivate(this.employee.id).subscribe({
      next: () => this.employeesService.get(this.employee!.id).subscribe((e) => {
        this.ngZone.run(() => {
          this.employee = e;
          this.cdr.detectChanges();
        });
      }),
      error: () => {
        this.ngZone.run(() => {
          this.error = 'Reactivate failed.';
          this.cdr.detectChanges();
        });
      },
      complete: () => {
        this.ngZone.run(() => {
          this.actionInProgress = null;
          this.cdr.detectChanges();
        });
      }
    });
  }

  resetPassword(): void {
    if (!this.employee) return;
    this.error = null;
    this.actionInProgress = 'reset';
    this.employeesService.resetPassword(this.employee.id).subscribe({
      next: () => {
        this.ngZone.run(() => {
          this.actionInProgress = null;
          this.cdr.detectChanges();
        });
      },
      error: () => {
        this.ngZone.run(() => {
          this.error = 'Reset password failed.';
          this.actionInProgress = null;
          this.cdr.detectChanges();
        });
      }
    });
  }
}
