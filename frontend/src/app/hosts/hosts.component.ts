import { Component } from '@angular/core';
import { PlaybookService } from '../services/playbook.service';
import { FormsModule } from '@angular/forms';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-hosts',
  standalone: true,
  imports: [FormsModule, NgIf],
  templateUrl: './hosts.component.html',
  styleUrl: './hosts.component.css'
})
export class HostsComponent {
  result: string = '';
  disableSaveButton: boolean = false;
  isLoading: boolean = true;

  constructor(private playbookService: PlaybookService) {}

  ngOnInit(): void {
    this.playbookService.loadHostsFile().subscribe({
      next: response => {
        this.result = response.output;
        this.isLoading = false;
      },
      error: error => {
        this.isLoading = false;
      }
    });
  }

  save(){
    this.playbookService.updateHostsFile(this.result).subscribe(response => {
      alert('Changes in file hosts saved!')
    })
  }

}
