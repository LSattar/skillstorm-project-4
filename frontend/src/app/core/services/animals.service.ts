import { Injectable } from '@angular/core';
// import { HttpClient } from '@angular/common/http';
// import { Observable } from 'rxjs';
// import { apiBaseUrl } from '../api-config';
import type { Animal, AnimalPhoto } from '../models/animal.model';

const MOCK_PHOTO_URL = '/images/image.png';

const MOCK_ANIMALS: Animal[] = [
  {
    id: '1',
    name: 'Buddy',
    species: 'DOG',
    breed: 'Labrador',
    sex: 'MALE',
    ageMonths: 24,
    goodWithKids: true,
    goodWithOtherPets: true,
    medicallyComplex: false,
    description: 'Friendly and energetic.',
    status: 'IN_SHELTER',
    photoUrl: MOCK_PHOTO_URL
  },
  {
    id: '2',
    name: 'Whiskers',
    species: 'CAT',
    breed: 'Tabby',
    sex: 'FEMALE',
    ageMonths: 18,
    goodWithKids: true,
    goodWithOtherPets: false,
    medicallyComplex: false,
    description: 'Calm and affectionate.',
    status: 'IN_SHELTER',
    photoUrl: MOCK_PHOTO_URL
  },
  {
    id: '3',
    name: 'Max',
    species: 'DOG',
    breed: 'Mixed',
    sex: 'MALE',
    ageMonths: 36,
    goodWithKids: false,
    goodWithOtherPets: true,
    medicallyComplex: false,
    description: 'Best in a quiet home.',
    status: 'IN_FOSTER',
    photoUrl: null
  },
  {
    id: '4',
    name: 'Luna',
    species: 'CAT',
    breed: 'Siamese',
    sex: 'FEMALE',
    ageMonths: 12,
    goodWithKids: true,
    goodWithOtherPets: true,
    medicallyComplex: false,
    description: 'Playful and social.',
    status: 'IN_SHELTER',
    photoUrl: MOCK_PHOTO_URL
  },
  {
    id: '5',
    name: 'Rex',
    species: 'DOG',
    breed: 'German Shepherd',
    sex: 'MALE',
    ageMonths: 48,
    goodWithKids: true,
    goodWithOtherPets: false,
    medicallyComplex: true,
    description: 'Loyal companion, needs experienced owner.',
    status: 'IN_SHELTER',
    photoUrl: null
  },
  {
    id: '6',
    name: 'Mittens',
    species: 'CAT',
    breed: null,
    sex: 'FEMALE',
    ageMonths: 8,
    goodWithKids: true,
    goodWithOtherPets: true,
    medicallyComplex: false,
    description: 'Kitten ready for a forever home.',
    status: 'IN_SHELTER',
    photoUrl: MOCK_PHOTO_URL
  }
];

const MOCK_PHOTOS_BY_ANIMAL: Record<string, AnimalPhoto[]> = {
  '1': [{ id: 'p1', animalId: '1', url: MOCK_PHOTO_URL, isPrimary: true }],
  '2': [{ id: 'p2', animalId: '2', url: MOCK_PHOTO_URL, isPrimary: true }],
  '3': [],
  '4': [{ id: 'p4', animalId: '4', url: MOCK_PHOTO_URL, isPrimary: true }],
  '5': [],
  '6': [{ id: 'p6', animalId: '6', url: MOCK_PHOTO_URL, isPrimary: true }]
};

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
  // constructor(private http: HttpClient) {}

  getAnimals(_params?: GetAnimalsParams): Animal[] {
    // Simulated: return mock array. When using real API, uncomment below and remove return.
    // const params = _params as Record<string, string | number | boolean | undefined>;
    // return this.http.get<Animal[]>(`${apiBaseUrl}/animals`, { params }) as Observable<Animal[]>;
    return [...MOCK_ANIMALS];
  }

  getAnimal(id: string): Animal | null {
    // Simulated: find in mock array. When using real API, uncomment below and remove return.
    // return this.http.get<Animal>(`${apiBaseUrl}/animals/${id}`) as Observable<Animal>;
    return MOCK_ANIMALS.find((a) => a.id === id) ?? null;
  }

  getAnimalPhotos(id: string): AnimalPhoto[] {
    // Simulated: return mock photos for this animal. When using real API, uncomment below and remove return.
    // return this.http.get<AnimalPhoto[]>(`${apiBaseUrl}/animals/${id}/photos`) as Observable<AnimalPhoto[]>;
    return MOCK_PHOTOS_BY_ANIMAL[id] ?? [];
  }
}
