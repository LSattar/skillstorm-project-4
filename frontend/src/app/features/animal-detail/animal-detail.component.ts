import { Component, OnInit, signal, computed, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AnimalsService } from '../../core/services/animals.service';
import type { Animal, AnimalPhoto } from '../../core/models/animal.model';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-animal-detail',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './animal-detail.component.html',
  styleUrl: './animal-detail.component.css'
})
export class AnimalDetailComponent implements OnInit {
  private platformId = inject(PLATFORM_ID);
  animal = signal<Animal | null>(null);
  photos = signal<AnimalPhoto[]>([]);
  notFound = signal(false);

  primaryPhoto = computed(() => {
    const list = this.photos();
    const primary = list.find((p) => p.isPrimary);
    return primary ?? list[0] ?? null;
  });

  constructor(
    private route: ActivatedRoute,
    private animalsService: AnimalsService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.notFound.set(true);
      return;
    }
    this.animalsService.getAnimal(id).subscribe({
      next: (animal) => {
        this.animal.set(animal);
        this.notFound.set(false);
      },
      error: () => this.notFound.set(true)
    });
    this.animalsService.getAnimalPhotos(id).subscribe({
      next: (photos) => this.photos.set(photos),
      error: () => this.photos.set([])
    });
  }

  get canApply(): boolean {
    return this.authService.isAdopter();
  }

  get isLoggedIn(): boolean {
    return this.authService.isLoggedIn;
  }
}
