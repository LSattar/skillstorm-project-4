import { Component, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AdopterService } from '../../../core/services/adopter.service';
import type { AdoptionApplicationResponse } from '../../../core/models/adopter.model';

@Component({
  selector: 'app-adopter-application-detail',
  standalone: true,
  imports: [DatePipe, RouterLink],
  templateUrl: './adopter-application-detail.component.html',
  styleUrl: './adopter-application-detail.component.css'
})
export class AdopterApplicationDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private adopterService = inject(AdopterService);

  application: AdoptionApplicationResponse | null = null;
  loading = true;
  error: string | null = null;

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.error = 'Invalid application.';
      this.loading = false;
      return;
    }
    this.adopterService.getApplication(id).subscribe({
      next: (app) => (this.application = app),
      error: () => {
        this.error = 'Could not load application.';
        this.loading = false;
      },
      complete: () => (this.loading = false)
    });
  }

  statusLabel(status: string): string {
    return status?.replace(/_/g, ' ') ?? status;
  }
}
