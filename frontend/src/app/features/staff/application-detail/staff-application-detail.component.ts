import { Component, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { StaffApplicationsService } from '../../../core/services/staff-applications.service';
import { StaffAdoptionsService } from '../../../core/services/staff-adoptions.service';
import type { AdoptionApplicationResponse } from '../../../core/models/adopter.model';

@Component({
  selector: 'app-staff-application-detail',
  standalone: true,
  imports: [DatePipe, FormsModule, RouterLink],
  templateUrl: './staff-application-detail.component.html',
  styleUrl: './staff-application-detail.component.css'
})
export class StaffApplicationDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private applicationsService = inject(StaffApplicationsService);
  private adoptionsService = inject(StaffAdoptionsService);

  application: AdoptionApplicationResponse | null = null;
  loading = true;
  error: string | null = null;
  decisionNotes = '';
  actionInProgress: 'approve' | 'deny' | 'finalize' | null = null;

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.error = 'Invalid application.';
      this.loading = false;
      return;
    }
    this.applicationsService.get(id).subscribe({
      next: (app) => (this.application = app),
      error: () => {
        this.error = 'Could not load application.';
        this.loading = false;
      },
      complete: () => (this.loading = false)
    });
  }

  statusLabel(s: string): string {
    return s?.replace(/_/g, ' ') ?? s;
  }

  approve(): void {
    if (!this.application) return;
    this.error = null;
    this.actionInProgress = 'approve';
    this.applicationsService.approve(this.application.id, { decisionNotes: this.decisionNotes || undefined }).subscribe({
      next: (app) => {
        this.application = app;
        this.actionInProgress = null;
      },
      error: () => {
        this.error = 'Approve failed.';
        this.actionInProgress = null;
      }
    });
  }

  deny(): void {
    if (!this.application) return;
    this.error = null;
    this.actionInProgress = 'deny';
    this.applicationsService.deny(this.application.id, { decisionNotes: this.decisionNotes || undefined }).subscribe({
      next: (app) => {
        this.application = app;
        this.actionInProgress = null;
      },
      error: () => {
        this.error = 'Deny failed.';
        this.actionInProgress = null;
      }
    });
  }

  finalize(): void {
    if (!this.application) return;
    this.error = null;
    this.actionInProgress = 'finalize';
    this.adoptionsService.finalize({ applicationId: this.application.id }).subscribe({
      next: () => this.router.navigate(['/staff/applications']),
      error: () => {
        this.error = 'Finalize adoption failed.';
        this.actionInProgress = null;
      }
    });
  }

  get canFinalize(): boolean {
    return this.application?.status === 'APPROVED';
  }
}
