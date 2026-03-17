import { Component, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { StaffEmployeesService } from '../../../core/services/staff-employees.service';
import type { UserResponse } from '../../../core/models/staff.model';

@Component({
  selector: 'app-staff-employees-list',
  standalone: true,
  imports: [DatePipe, RouterLink],
  templateUrl: './staff-employees-list.component.html',
  styleUrl: './staff-employees-list.component.css'
})
export class StaffEmployeesListComponent implements OnInit {
  private employeesService = inject(StaffEmployeesService);

  employees: UserResponse[] = [];
  loading = true;
  error: string | null = null;

  ngOnInit(): void {
    this.employeesService.list().subscribe({
      next: (list) => (this.employees = list),
      error: () => {
        this.error = 'Could not load employees.';
        this.loading = false;
      },
      complete: () => (this.loading = false)
    });
  }
}
