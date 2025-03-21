import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class InventoryService {

  private url = 'http://localhost:8080/inventory';
  constructor(private http: HttpClient) { }

  getHostsAndPlaybooks(userId: number) {
    return this.http.get<any>(`${this.url}/${userId}/names`);
  }

  getInventories(userId: number|undefined) {
    return this.http.get<any>(`${this.url}/${userId}`);
  }

  loadInventoryContent(inventory: any) {
    const url = 'http://localhost:8080/inventory/content';
    return this.http.get(`${url}/${inventory.id}`, { responseType: 'text' });
  }

  updateInventoryContent(inventoryId: number, newContent: any) {
    return this.http.put(`${this.url}/${inventoryId}`, newContent, { responseType: 'text' });
  }

  createInventory(userId: number|undefined, filename: string, content: string) {
    return this.http.post(`${this.url}`, { userId, filename, content });
  }

  deleteInventory(inventoryId: any) {
    return this.http.delete(`${this.url}/${inventoryId}`, { responseType: 'text' });
  }


}
