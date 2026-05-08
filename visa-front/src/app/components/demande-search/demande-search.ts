import { ChangeDetectorRef, Component, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-demande-search',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './demande-search.html'
})
export class DemandeSearchComponent {
  numeroRecherche = '';
  errorMessage = '';

  constructor(
    private router: Router,
    private zone: NgZone,
    private cdr: ChangeDetectorRef
  ) {}

  rechercher() {
    const numero = this.numeroRecherche.trim();
    if (!numero) {
      this.errorMessage = 'Veuillez entrer un numero.';
      return;
    }

    this.errorMessage = '';
    this.zone.run(() => {
      this.router.navigate(['/demandes'], { queryParams: { numero } });
      this.cdr.detectChanges();
    });
  }
}
