import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class PlaybookService {

  private url = 'http://localhost:8080/ansible';

  constructor(private http: HttpClient) { }



  play(playbook: any) {
    return this.http.post<any>(`${this.url}`, playbook);
  }

  getPlaybooks(userId: number|undefined) {
    const url = 'http://localhost:8080/playbooks';
    return this.http.get<any>(`${url}/${userId}`);
  }

  loadPlaybookContent(playbook: any) {
    const url = 'http://localhost:8080/playbooks/content';
    return this.http.get(`${url}/${playbook.id}`, { responseType: 'text' });
  }

  updatePlaybookContent(playbookId: number, newContent: any) {
    const url = 'http://localhost:8080/playbooks';
    return this.http.put(`${url}/${playbookId}`, newContent, { responseType: 'text' });
  }

  createPlaybook(userId: number|undefined, filename: string, content: string) {
    const url = 'http://localhost:8080/playbooks';
    return this.http.post(`${url}`, { userId, filename, content });
  }




  

  loadHostsFile() {
    const url = 'http://localhost:8080/ansible/hosts';
    return this.http.get<any>(url);
  }

  updateHostsFile(newContent: string) {
    console.log('update hosts')

    const url = 'http://localhost:8080/ansible/hosts';
    return this.http.put<any>(`${url}`, newContent);
  }


}
