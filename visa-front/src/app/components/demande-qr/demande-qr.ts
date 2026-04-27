import { ChangeDetectorRef, Component, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DemandeService } from '../../services/demande';

@Component({
  selector: 'app-demande-qr',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './demande-qr.html'
})
export class DemandeQrComponent {
  demandeId: number | null = null;
  statusHistory: any[] = [];
  qrContent = '';
  loading = true;

  constructor(
    private route: ActivatedRoute,
    private demandeService: DemandeService,
    private zone: NgZone,
    private cdr: ChangeDetectorRef
  ) {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!Number.isNaN(id)) {
      this.demandeId = id;
      this.demandeService.getDemandeDetails(id).subscribe({
        next: (data) => {
          this.zone.run(() => {
            this.statusHistory = data?.demandeStatusHistory ?? [];
            this.qrContent = this.buildQrContent(this.statusHistory);
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

  get qrImageUrl() {
    const payload = encodeURIComponent(this.qrContent || 'Aucune information');
    return `https://api.qrserver.com/v1/create-qr-code/?size=240x240&data=${payload}`;
  }

  private buildQrContent(history: any[]) {
    if (!history || history.length === 0) {
      return 'Aucun statut disponible';
    }

    return history
      .map((item: any) => {
        const date = this.formatDate(item.dateChangementStatus);
        const label = item.statusLabel || `Statut ${item.idStatus}`;
        return `${date} : ${label}`;
      })
      .join('\n');
  }

  private formatDate(value: string) {
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
