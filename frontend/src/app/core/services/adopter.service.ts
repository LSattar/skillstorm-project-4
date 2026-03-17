import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { apiBaseUrl } from '../api-config';
import type {
  AdopterProfileResponse,
  UpdateAdopterProfileRequest,
  AdopterQuestionnaireResponse,
  UpsertQuestionnaireRequest,
  AdoptionApplicationResponse,
  CreateApplicationRequest
} from '../models/adopter.model';

@Injectable({ providedIn: 'root' })
export class AdopterService {
  private http = inject(HttpClient);

  getProfile(): Observable<AdopterProfileResponse> {
    return this.http.get<AdopterProfileResponse>(`${apiBaseUrl}/adopter/profile`);
  }

  updateProfile(body: UpdateAdopterProfileRequest): Observable<AdopterProfileResponse> {
    return this.http.put<AdopterProfileResponse>(`${apiBaseUrl}/adopter/profile`, body);
  }

  getQuestionnaire(): Observable<AdopterQuestionnaireResponse> {
    return this.http.get<AdopterQuestionnaireResponse>(`${apiBaseUrl}/adopter/questionnaire`);
  }

  upsertQuestionnaire(body: UpsertQuestionnaireRequest): Observable<AdopterQuestionnaireResponse> {
    return this.http.put<AdopterQuestionnaireResponse>(`${apiBaseUrl}/adopter/questionnaire`, body);
  }

  getApplications(): Observable<AdoptionApplicationResponse[]> {
    return this.http.get<AdoptionApplicationResponse[]>(`${apiBaseUrl}/adopter/applications`);
  }

  getApplication(id: string): Observable<AdoptionApplicationResponse> {
    return this.http.get<AdoptionApplicationResponse>(`${apiBaseUrl}/adopter/applications/${id}`);
  }

  createApplication(body: CreateApplicationRequest): Observable<AdoptionApplicationResponse> {
    return this.http.post<AdoptionApplicationResponse>(`${apiBaseUrl}/adopter/applications`, body);
  }
}
