import { Routes } from '@angular/router';
import { DemandeSearchComponent } from './components/demande-search/demande-search';
import { DemandeListComponent } from './components/demande-list/demande-list';
import { DemandeDetailsComponent } from './components/demande-details/demande-details';
import { DemandeQrComponent } from './components/demande-qr/demande-qr';

export const routes: Routes = [
	{ path: '', component: DemandeSearchComponent },
	{ path: 'demandes', component: DemandeListComponent },
	{ path: 'details/:id', component: DemandeDetailsComponent },
	{ path: 'qr/:id', component: DemandeQrComponent },
	{ path: '**', redirectTo: '' }
];
