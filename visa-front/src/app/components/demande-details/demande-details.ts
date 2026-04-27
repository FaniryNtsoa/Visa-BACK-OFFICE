import { ChangeDetectorRef, Component, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DemandeService } from '../../services/demande';

@Component({
  selector: 'app-demande-details',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './demande-details.html'
})
export class DemandeDetailsComponent {
  demande: any = null;
  demandeur: any = null;
  typeVisa: any = null;
  typeDemande: any = null;
  statusHistory: any[] = [];
  loading = true;

  constructor(
    private route: ActivatedRoute,
    private demandeService: DemandeService,
    private zone: NgZone,
    private cdr: ChangeDetectorRef
  ) {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!Number.isNaN(id)) {
      this.demandeService.getDemandeDetails(id).subscribe({
        next: (data) => {
          this.zone.run(() => {
            this.demande = data?.demande ?? null;
            this.demandeur = data?.demandeur ?? null;
            this.typeVisa = data?.typeVisa ?? null;
            this.typeDemande = data?.typeDemande ?? null;
            this.statusHistory = data?.demandeStatusHistory ?? [];
            this.loading = false;
            this.cdr.detectChanges();
          });
        },
        error: () => {
          this.zone.run(() => {
            this.loading = false;
            this.cdr.detectChanges();
          });
        }
      });
    } else {
      this.loading = false;
    }
  }

  formatDate(value: string) {
    if (!value) {
      return '-';
    }
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return value;
    }
    return date.toLocaleDateString('fr-FR');
  }
}
