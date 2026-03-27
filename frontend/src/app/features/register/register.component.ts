import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  loading = false;
  error: string | null = null;

  form = this.fb.group({
    username: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    displayName: [''],
    password: ['', [Validators.required, Validators.minLength(8)]]
  });

  onSubmit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.error = null;
    const value = this.form.getRawValue();
    this.auth.register({
      username: value.username ?? '',
      email: value.email ?? '',
      displayName: value.displayName?.trim() || undefined,
      password: value.password ?? ''
    }).subscribe({
      next: () => {
        const user = this.auth.currentUserValue;
        if (user?.roles?.includes('STAFF')) {
          this.router.navigate(['/staff']);
        } else {
          this.router.navigate(['/adopter/applications']);
        }
      },
      error: () => {
        this.error = 'Could not register with those details.';
        this.loading = false;
      }
    });
  }
}
