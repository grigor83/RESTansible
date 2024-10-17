import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class PlaybookService {

  private url = 'http://localhost:8080/ansible';

  constructor(private http: HttpClient) { }

  play(playbookId: number) {
    return this.http.get<any>(`${this.url}/${playbookId}`);
  }

  loadPlaybookContent(playbookId: number) {
    const url = 'http://localhost:8080/ansible/playbooks';
    return this.http.get<any>(`${url}/${playbookId}`);
  }

  updatePlaybookContent(playbookId: number, newContent: any) {
    const url = 'http://localhost:8080/ansible/playbooks';
    return this.http.put<any>(`${url}/${playbookId}`, newContent);
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

  loadDevicesNames() {
    const url = 'http://localhost:8080/data/devices';
    return this.http.get<any>(url);
  }

  loadPlaybookNames() {
    const url = 'http://localhost:8080/data/playbooks';
    return this.http.get<any>(url);
  }

}
