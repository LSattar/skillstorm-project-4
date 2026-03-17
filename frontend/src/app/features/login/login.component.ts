import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  form: FormGroup = this.fb.group({
    username: ['', [Validators.required]],
    password: ['', [Validators.required]]
  });

  loading = false;
  error: string | null = null;

  onSubmit(): void {
    if (this.form.invalid) return;
    this.error = null;
    this.loading = true;
    this.auth.login(this.form.value).subscribe({
      next: () => {
        const user = this.auth.currentUserValue;
        if (user?.roles?.includes('STAFF')) {
          this.router.navigate(['/staff']);
        } else if (user?.roles?.includes('ADOPTER')) {
          this.router.navigate(['/adopter/applications']);
        } else {
          this.router.navigate(['/']);
        }
      },
      error: () => {
        this.error = 'Invalid username or password.';
        this.loading = false;
      }
    });
  }
}
