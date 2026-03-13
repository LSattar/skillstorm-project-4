import { Component, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AnimalsService } from '../../core/services/animals.service';
import type { Animal } from '../../core/models/animal.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  animals = signal<Animal[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  constructor(private animalsService: AnimalsService) {}

  ngOnInit(): void {
    try {
      const list = this.animalsService.getAnimals();
      this.animals.set(list);
    } catch (e) {
      this.error.set('Failed to load animals.');
    } finally {
      this.loading.set(false);
    }
  }

  hasPhoto(animal: Animal): boolean {
    return !!animal.photoUrl;
  }
}
