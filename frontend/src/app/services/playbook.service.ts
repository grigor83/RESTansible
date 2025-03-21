import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class PlaybookService {

  private url = 'http://localhost:8080/playbooks';

  constructor(private http: HttpClient) { }


  play(playbook: any, inventory: any) {
    return this.http.get<any>(`${this.url}/${playbook.id}/${inventory.id}`)
  }

  getPlaybooks(userId: number|undefined) {
    return this.http.get<any>(`${this.url}/${userId}`);
  }

  loadPlaybookContent(playbook: any) {
    const url = 'http://localhost:8080/playbooks/content';
    return this.http.get(`${url}/${playbook.id}`, { responseType: 'text' });
  }

  updatePlaybookContent(playbookId: number, newContent: any) {
    return this.http.put(`${this.url}/${playbookId}`, newContent, { responseType: 'text' });
  }

  createPlaybook(userId: number|undefined, filename: string, content: string) {
    return this.http.post(`${this.url}`, { userId, filename, content });
  }

  deletePlaybook(playbookId: any) {
    return this.http.delete(`${this.url}/${playbookId}`, { responseType: 'text' });
  }


}
