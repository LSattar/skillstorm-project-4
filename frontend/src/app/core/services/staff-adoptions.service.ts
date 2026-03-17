import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { apiBaseUrl } from '../api-config';
import type { AdoptionResponse, CreateAdoptionRequest } from '../models/staff.model';

@Injectable({ providedIn: 'root' })
export class StaffAdoptionsService {
  private http = inject(HttpClient);

  list(): Observable<AdoptionResponse[]> {
    return this.http.get<AdoptionResponse[]>(`${apiBaseUrl}/staff/adoptions`);
  }

  get(id: string): Observable<AdoptionResponse> {
    return this.http.get<AdoptionResponse>(`${apiBaseUrl}/staff/adoptions/${id}`);
  }

  finalize(body: CreateAdoptionRequest): Observable<AdoptionResponse> {
    return this.http.post<AdoptionResponse>(`${apiBaseUrl}/staff/adoptions`, body);
  }
}
