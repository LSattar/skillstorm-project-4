import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { apiBaseUrl } from '../api-config';
import type { AdoptionApplicationResponse } from '../models/adopter.model';
import type { ApproveApplicationRequest, DenyApplicationRequest } from '../models/staff.model';

@Injectable({ providedIn: 'root' })
export class StaffApplicationsService {
  private http = inject(HttpClient);

  list(params?: { status?: string; animalId?: string; adopterEmail?: string }): Observable<AdoptionApplicationResponse[]> {
    let httpParams = new HttpParams();
    if (params?.status) httpParams = httpParams.set('status', params.status);
    if (params?.animalId) httpParams = httpParams.set('animalId', params.animalId);
    if (params?.adopterEmail) httpParams = httpParams.set('adopterEmail', params.adopterEmail);
    return this.http.get<AdoptionApplicationResponse[]>(`${apiBaseUrl}/staff/applications`, { params: httpParams });
  }

  get(id: string): Observable<AdoptionApplicationResponse> {
    return this.http.get<AdoptionApplicationResponse>(`${apiBaseUrl}/staff/applications/${id}`);
  }

  approve(id: string, body?: ApproveApplicationRequest): Observable<AdoptionApplicationResponse> {
    return this.http.post<AdoptionApplicationResponse>(`${apiBaseUrl}/staff/applications/${id}/approve`, body ?? {});
  }

  deny(id: string, body?: DenyApplicationRequest): Observable<AdoptionApplicationResponse> {
    return this.http.post<AdoptionApplicationResponse>(`${apiBaseUrl}/staff/applications/${id}/deny`, body ?? {});
  }
}
