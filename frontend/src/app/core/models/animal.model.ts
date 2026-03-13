export interface Animal {
  id: string;
  name: string;
  species: string;
  breed?: string | null;
  sex?: string | null;
  ageMonths?: number | null;
  goodWithKids?: boolean | null;
  goodWithOtherPets?: boolean | null;
  medicallyComplex?: boolean | null;
  description?: string | null;
  status: string;
  currentShelterId?: number | null;
  currentFosterUserId?: string | null;
  currentShelterName?: string | null;
  createdAt?: string | null;
  updatedAt?: string | null;
  /** For list/grid: optional primary photo URL (mock or from backend later) */
  photoUrl?: string | null;
}

export interface AnimalPhoto {
  id: string;
  animalId: string;
  url: string;
  s3Key?: string | null;
  isPrimary?: boolean | null;
  contentType?: string | null;
  fileSizeBytes?: number | null;
  createdAt?: string | null;
}
