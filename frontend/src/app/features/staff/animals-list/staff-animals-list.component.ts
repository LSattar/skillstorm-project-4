import { Component, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { StaffAnimalsService } from '../../../core/services/staff-animals.service';
import type { AnimalResponse } from '../../../core/models/staff.model';

@Component({
  selector: 'app-staff-animals-list',
  standalone: true,
  imports: [DatePipe, FormsModule, RouterLink],
  templateUrl: './staff-animals-list.component.html',
  styleUrl: './staff-animals-list.component.css'
})
export class StaffAnimalsListComponent implements OnInit {
  private animalsService = inject(StaffAnimalsService);

  animals: AnimalResponse[] = [];
  filtered: AnimalResponse[] = [];
  loading = true;
  error: string | null = null;
  filterStatus = '';
  filterSpecies = '';

  ngOnInit(): void {
    this.animalsService.list().subscribe({
      next: (list) => {
        this.animals = list;
        this.applyFilters();
      },
      error: () => {
        this.error = 'Could not load animals.';
        this.loading = false;
      },
      complete: () => (this.loading = false)
    });
  }

  applyFilters(): void {
    this.filtered = this.animals.filter((a) => {
      if (this.filterStatus && a.status !== this.filterStatus) return false;
      if (this.filterSpecies && a.species !== this.filterSpecies) return false;
      return true;
    });
  }

  statusLabel(s: string): string {
    return s?.replace(/_/g, ' ') ?? s;
  }
}
