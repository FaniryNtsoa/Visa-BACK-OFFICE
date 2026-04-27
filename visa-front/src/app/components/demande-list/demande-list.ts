import { ChangeDetectorRef, Component, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DemandeService } from '../../services/demande';
import { timeout } from 'rxjs/operators';

@Component({
  selector: 'app-demande-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './demande-list.html'
})
export class DemandeListComponent {
  demandes: any[] = [];
  numeroRecherche: string = '';
  errorMessage = '';
  isLoading = false;

  constructor(
    private demandeService: DemandeService,
    private route: ActivatedRoute,
    private zone: NgZone,
    private cdr: ChangeDetectorRef
  ) {
    this.route.queryParamMap.subscribe((params) => {
      const numero = params.get('numero')?.trim();
      if (numero) {
        this.numeroRecherche = numero;
        this.rechercher();
      }
    });
  }

  rechercher() {
    const numero = this.numeroRecherche.trim();
    if (!numero) {
      this.errorMessage = 'Veuillez entrer un numero.';
      return;
    }

    this.errorMessage = '';
    this.isLoading = true;
    this.demandeService.getDemandesByNumero(numero)
      .pipe(
        timeout(10000)
      )
      .subscribe({
        next: (data) => {
          this.zone.run(() => {
            this.demandes = Array.isArray(data) ? data : [];
            this.isLoading = false;
            this.cdr.detectChanges();
          });
        },
        error: () => {
          this.zone.run(() => {
            this.demandes = [];
            this.errorMessage = 'Aucune demande trouvee.';
            this.isLoading = false;
            this.cdr.detectChanges();
          });
        }
      });
  }
}