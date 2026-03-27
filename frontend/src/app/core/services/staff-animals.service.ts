import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { apiBaseUrl } from '../api-config';
import type {
  AnimalResponse,
  CreateAnimalRequest,
  UpdateAnimalRequest,
  MoveToShelterRequest,
  MoveToFosterRequest,
  UpdateStatusRequest
} from '../models/staff.model';

@Injectable({ providedIn: 'root' })
export class StaffAnimalsService {
  private http = inject(HttpClient);

  list(params?: {
    status?: string;
    species?: string;
    shelterId?: number;
    fosterId?: string;
    medicallyComplex?: boolean;
    intakeDate?: string;
    adoptionStatus?: string;
  }): Observable<AnimalResponse[]> {
    let httpParams = new HttpParams();
    if (params?.status) httpParams = httpParams.set('status', params.status);
    if (params?.species) httpParams = httpParams.set('species', params.species);
    if (params?.shelterId != null) httpParams = httpParams.set('shelterId', params.shelterId);
    if (params?.fosterId) httpParams = httpParams.set('fosterId', params.fosterId);
    if (params?.medicallyComplex != null) httpParams = httpParams.set('medicallyComplex', params.medicallyComplex);
    if (params?.intakeDate) httpParams = httpParams.set('intakeDate', params.intakeDate);
    if (params?.adoptionStatus) httpParams = httpParams.set('adoptionStatus', params.adoptionStatus);
    return this.http.get<AnimalResponse[]>(`${apiBaseUrl}/staff/animals`, { params: httpParams });
  }

  get(id: string): Observable<AnimalResponse> {
    return this.http.get<AnimalResponse>(`${apiBaseUrl}/staff/animals/${id}`);
  }

  create(body: CreateAnimalRequest): Observable<AnimalResponse> {
    return this.http.post<AnimalResponse>(`${apiBaseUrl}/staff/animals`, body);
  }

  update(id: string, body: UpdateAnimalRequest): Observable<AnimalResponse> {
    return this.http.put<AnimalResponse>(`${apiBaseUrl}/staff/animals/${id}`, body);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${apiBaseUrl}/staff/animals/${id}`);
  }

  moveToShelter(id: string, body: MoveToShelterRequest): Observable<AnimalResponse> {
    return this.http.post<AnimalResponse>(`${apiBaseUrl}/staff/animals/${id}/move/shelter`, body);
  }

  moveToFoster(id: string, body: MoveToFosterRequest): Observable<AnimalResponse> {
    return this.http.post<AnimalResponse>(`${apiBaseUrl}/staff/animals/${id}/move/foster`, body);
  }

  updateStatus(id: string, body: UpdateStatusRequest): Observable<AnimalResponse> {
    return this.http.post<AnimalResponse>(`${apiBaseUrl}/staff/animals/${id}/status`, body);
  }
}
