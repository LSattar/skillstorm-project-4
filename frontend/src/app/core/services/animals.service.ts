import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { apiBaseUrl } from '../api-config';
import type { Animal, AnimalPhoto } from '../models/animal.model';

export interface GetAnimalsParams {
  species?: string;
  ageMin?: number;
  ageMax?: number;
  goodWithKids?: boolean;
  goodWithOtherPets?: boolean;
  medicallyComplex?: boolean;
}

@Injectable({ providedIn: 'root' })
export class AnimalsService {
  private http = inject(HttpClient);

  getAnimals(params?: GetAnimalsParams): Observable<Animal[]> {
    let httpParams = new HttpParams();
    if (params?.species) httpParams = httpParams.set('species', params.species);
    if (params?.ageMin != null) httpParams = httpParams.set('ageMin', params.ageMin);
    if (params?.ageMax != null) httpParams = httpParams.set('ageMax', params.ageMax);
    if (params?.goodWithKids != null) httpParams = httpParams.set('goodWithKids', params.goodWithKids);
    if (params?.goodWithOtherPets != null) httpParams = httpParams.set('goodWithOtherPets', params.goodWithOtherPets);
    if (params?.medicallyComplex != null) httpParams = httpParams.set('medicallyComplex', params.medicallyComplex);
    return this.http.get<Animal[]>(`${apiBaseUrl}/animals`, { params: httpParams });
  }

  getAnimal(id: string): Observable<Animal> {
    return this.http.get<Animal>(`${apiBaseUrl}/animals/${id}`);
  }

  getAnimalPhotos(id: string): Observable<AnimalPhoto[]> {
    return this.http.get<AnimalPhoto[]>(`${apiBaseUrl}/animals/${id}/photos`);
  }
}
