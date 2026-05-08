import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})

export class DemandeService {
  private readonly apiUrl = this.buildApiUrl();

  constructor(private http: HttpClient) { }

  private buildApiUrl(): string {
    if (typeof window === 'undefined' || !window.location) {
      return 'http://localhost:8080/api/demandes';
    }
    return `${window.location.protocol}//${window.location.hostname}:8080/api/demandes`;
  }

  getDemandesByNumero(numero: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${numero}`);
  }

  getDemandeDetails(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/details/${id}`);
  }
}