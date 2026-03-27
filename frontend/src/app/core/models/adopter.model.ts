export interface AdopterProfileResponse {
  userId: string;
  addressLine1?: string | null;
  addressLine2?: string | null;
  city?: string | null;
  state?: string | null;
  zip?: string | null;
  householdSize?: number | null;
  housingType?: string | null;
  hasYard?: boolean | null;
  hasKids?: boolean | null;
  hasOtherPets?: boolean | null;
  needsGoodWithKids?: boolean | null;
  needsGoodWithOtherPets?: boolean | null;
  willingMedicallyComplex?: boolean | null;
  notes?: string | null;
}

export interface UpdateAdopterProfileRequest {
  addressLine1?: string | null;
  addressLine2?: string | null;
  city?: string | null;
  state?: string | null;
  zip?: string | null;
  householdSize?: number | null;
  housingType?: string | null;
  hasYard?: boolean | null;
  hasKids?: boolean | null;
  hasOtherPets?: boolean | null;
  needsGoodWithKids?: boolean | null;
  needsGoodWithOtherPets?: boolean | null;
  willingMedicallyComplex?: boolean | null;
  notes?: string | null;
}

export interface AdopterQuestionnaireResponse {
  id: string;
  userId: string;
  schemaVersion?: number | null;
  householdSize?: number | null;
  phone?: string | null;
  city?: string | null;
  state?: string | null;
  zip?: string | null;
  housingType?: string | null;
  hasYard?: boolean | null;
  hasKids?: boolean | null;
  hasOtherPets?: boolean | null;
  needsGoodWithKids?: boolean | null;
  needsGoodWithOtherPets?: boolean | null;
  willingMedicallyComplex?: boolean | null;
  notes?: string | null;
  createdAt?: string | null;
  updatedAt?: string | null;
}

export interface UpsertQuestionnaireRequest {
  schemaVersion?: number;
  householdSize?: number | null;
  phone?: string | null;
  city?: string | null;
  state?: string | null;
  zip?: string | null;
  housingType?: string | null;
  hasYard?: boolean | null;
  hasKids?: boolean | null;
  hasOtherPets?: boolean | null;
  needsGoodWithKids?: boolean | null;
  needsGoodWithOtherPets?: boolean | null;
  willingMedicallyComplex?: boolean | null;
  notes?: string | null;
}

export interface AdoptionApplicationResponse {
  id: string;
  animalId: string;
  adopterUserId: string;
  status: string;
  questionnaireSnapshotJson?: string | null;
  staffReviewerUserId?: string | null;
  decisionNotes?: string | null;
  createdAt?: string | null;
  updatedAt?: string | null;
}

export interface CreateApplicationRequest {
  animalId: string;
  questionnaireAnswers?: UpsertQuestionnaireRequest | null;
  questionnaireSnapshotJson?: string | null;
}
