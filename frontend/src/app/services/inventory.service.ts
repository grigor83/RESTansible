import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class InventoryService {

  private url = 'http://localhost:8080/inventory';
  constructor(private http: HttpClient) { }

  getHostsAndPlaybooks(userId: number) {
    return this.http.get<any>(`${this.url}/${userId}/data`);
  }
}
