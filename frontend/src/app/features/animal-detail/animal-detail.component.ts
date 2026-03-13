import { Component, OnInit, signal, computed } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AnimalsService } from '../../core/services/animals.service';
import type { Animal, AnimalPhoto } from '../../core/models/animal.model';

@Component({
  selector: 'app-animal-detail',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './animal-detail.component.html',
  styleUrl: './animal-detail.component.css'
})
export class AnimalDetailComponent implements OnInit {
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
    private animalsService: AnimalsService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.notFound.set(true);
      return;
    }
    const animal = this.animalsService.getAnimal(id);
    if (!animal) {
      this.notFound.set(true);
      return;
    }
    this.animal.set(animal);
    this.photos.set(this.animalsService.getAnimalPhotos(id));
  }
}
