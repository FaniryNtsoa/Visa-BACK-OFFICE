import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})

export class DemandeService {
  private apiUrl = 'http://localhost:8080/api/demandes';

  constructor(private http: HttpClient) { }

  getDemandesByNumero(numero: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${numero}`);
  }

  getDemandeDetails(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/details/${id}`);
  }
}