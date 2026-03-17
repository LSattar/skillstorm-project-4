export interface AnimalResponse {
  id: string;
  name: string;
  species?: string | null;
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
}

export interface CreateAnimalRequest {
  name: string;
  species: string;
  breed?: string | null;
  sex?: string | null;
  ageMonths?: number | null;
  goodWithKids?: boolean;
  goodWithOtherPets?: boolean;
  medicallyComplex?: boolean;
  description?: string | null;
  status: string;
  currentShelterId?: number | null;
  currentFosterUserId?: string | null;
}

export interface UpdateAnimalRequest {
  name?: string | null;
  species?: string | null;
  breed?: string | null;
  sex?: string | null;
  ageMonths?: number | null;
  goodWithKids?: boolean | null;
  goodWithOtherPets?: boolean | null;
  medicallyComplex?: boolean | null;
  description?: string | null;
  status?: string | null;
  currentShelterId?: number | null;
  currentFosterUserId?: string | null;
}

export interface MoveToShelterRequest {
  toShelterId: number;
  notes?: string | null;
}

export interface MoveToFosterRequest {
  toFosterUserId: string;
  notes?: string | null;
}

export interface UpdateStatusRequest {
  status: string;
  notes?: string | null;
}

export interface ApproveApplicationRequest {
  decisionNotes?: string | null;
}

export interface DenyApplicationRequest {
  decisionNotes?: string | null;
}

export interface CreateAdoptionRequest {
  applicationId: string;
}

export interface UserResponse {
  id: string;
  username: string;
  email?: string | null;
  displayName?: string | null;
  phone?: string | null;
  isEnabled?: boolean;
  roles?: string[];
  createdAt?: string | null;
}

export interface CreateEmployeeRequest {
  username: string;
  email: string;
  password: string;
  displayName?: string | null;
  phone?: string | null;
}

export interface UpdateEmployeeRequest {
  email?: string | null;
  displayName?: string | null;
  phone?: string | null;
}

export interface AdoptionResponse {
  id: string;
  animalId: string;
  adopterUserId: string;
  applicationId: string;
  adoptedAt?: string | null;
  finalizedByUserId?: string | null;
  notes?: string | null;
}

export interface ShelterResponse {
  id: number;
  name: string;
  addressLine1?: string | null;
  addressLine2?: string | null;
  city?: string | null;
  state?: string | null;
  zip?: string | null;
  capacityTotal?: number | null;
  createdAt?: string | null;
  updatedAt?: string | null;
}
