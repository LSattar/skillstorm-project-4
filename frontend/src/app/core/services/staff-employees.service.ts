import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { apiBaseUrl } from '../api-config';
import type { UserResponse, CreateEmployeeRequest, UpdateEmployeeRequest } from '../models/staff.model';

@Injectable({ providedIn: 'root' })
export class StaffEmployeesService {
  private http = inject(HttpClient);

  list(): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(`${apiBaseUrl}/staff/employees`);
  }

  get(id: string): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${apiBaseUrl}/staff/employees/${id}`);
  }

  create(body: CreateEmployeeRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(`${apiBaseUrl}/staff/employees`, body);
  }

  update(id: string, body: UpdateEmployeeRequest): Observable<UserResponse> {
    return this.http.put<UserResponse>(`${apiBaseUrl}/staff/employees/${id}`, body);
  }

  deactivate(id: string): Observable<void> {
    return this.http.patch<void>(`${apiBaseUrl}/staff/employees/${id}/deactivate`, {});
  }

  reactivate(id: string): Observable<void> {
    return this.http.patch<void>(`${apiBaseUrl}/staff/employees/${id}/reactivate`, {});
  }

  resetPassword(id: string, newPassword?: string): Observable<void> {
    return this.http.post<void>(`${apiBaseUrl}/staff/employees/${id}/reset-password`, newPassword ?? 'changeme');
  }
}
