import { Component, OnInit, signal, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AnimalsService } from '../../core/services/animals.service';
import type { Animal } from '../../core/models/animal.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, FormsModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  private platformId = inject(PLATFORM_ID);
  animals = signal<Animal[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  filterSpecies = '';
  filterAgeMin: number | null = null;
  filterAgeMax: number | null = null;
  filterGoodWithKids: '' | 'true' | 'false' = '';
  filterGoodWithOtherPets: '' | 'true' | 'false' = '';
  filterMedicallyComplex: '' | 'true' | 'false' = '';

  constructor(private animalsService: AnimalsService) {}

  ngOnInit(): void {
    if (!isPlatformBrowser(this.platformId)) {
      this.loading.set(false);
      return;
    }
    this.loadAnimals();
  }

  loadAnimals(): void {
    this.loading.set(true);
    this.error.set(null);
    this.animalsService
      .getAnimals({
        species: this.filterSpecies || undefined,
        ageMin: this.filterAgeMin ?? undefined,
        ageMax: this.filterAgeMax ?? undefined,
        goodWithKids: this.toBool(this.filterGoodWithKids),
        goodWithOtherPets: this.toBool(this.filterGoodWithOtherPets),
        medicallyComplex: this.toBool(this.filterMedicallyComplex)
      })
      .subscribe({
        next: (list) => this.animals.set(list),
        error: () => this.error.set('Failed to load animals.'),
        complete: () => this.loading.set(false)
      });
  }

  resetFilters(): void {
    this.filterSpecies = '';
    this.filterAgeMin = null;
    this.filterAgeMax = null;
    this.filterGoodWithKids = '';
    this.filterGoodWithOtherPets = '';
    this.filterMedicallyComplex = '';
    this.loadAnimals();
  }

  hasPhoto(animal: Animal): boolean {
    return !!animal.photoUrl;
  }

  private toBool(value: '' | 'true' | 'false'): boolean | undefined {
    if (value === '') return undefined;
    return value === 'true';
  }
}
