import { ChangeDetectorRef, Component, NgZone, OnInit, inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs/operators';

import { AdopterService } from '../../../core/services/adopter.service';
import type { AdopterRecommendation } from '../../../core/models/adopter.model';

@Component({
  selector: 'app-adopter-recommendations',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './adopter-recommendations.component.html',
  styleUrl: './adopter-recommendations.component.css'
})
export class AdopterRecommendationsComponent implements OnInit {
  private cdr = inject(ChangeDetectorRef);
  private ngZone = inject(NgZone);
  private adopterService = inject(AdopterService);

  recommendations: AdopterRecommendation[] = [];
  loading = true;
  error: string | null = null;

  ngOnInit(): void {
    this.adopterService
      .getRecommendations(10)
      .pipe(
        finalize(() => {
          this.ngZone.run(() => {
            this.loading = false;
            this.cdr.detectChanges();
          });
        })
      )
      .subscribe({
        next: (items) => {
          this.ngZone.run(() => {
            this.recommendations = items;
            this.cdr.detectChanges();
          });
        },
        error: (err: HttpErrorResponse) => {
          this.ngZone.run(() => {
            this.error = this.apiErrorMessage(err, 'Could not load recommendations.');
            this.cdr.detectChanges();
          });
        }
      });
  }

  toDisplayReason(reasonCode: string): string {
    return reasonCode.replace(/_/g, ' ').toLowerCase();
  }

  private apiErrorMessage(err: HttpErrorResponse, fallback: string): string {
    if (err.status === 401) return 'Your session has expired. Please log in again.';
    if (err.status === 403) return 'You do not have permission to view recommendations.';
    return fallback;
  }
}
