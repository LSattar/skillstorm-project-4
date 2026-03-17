import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { apiBaseUrl } from '../api-config';
import type { ShelterResponse } from '../models/staff.model';

@Injectable({ providedIn: 'root' })
export class StaffSheltersService {
  private http = inject(HttpClient);

  list(): Observable<ShelterResponse[]> {
    return this.http.get<ShelterResponse[]>(`${apiBaseUrl}/staff/shelters`);
  }

  get(id: number): Observable<ShelterResponse> {
    return this.http.get<ShelterResponse>(`${apiBaseUrl}/staff/shelters/${id}`);
  }
}
