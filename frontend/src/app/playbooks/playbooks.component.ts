import { NgFor, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { PlaybookService } from '../services/playbook.service';

@Component({
  selector: 'app-playbooks',
  standalone: true,
  imports: [FormsModule, NgFor, NgIf],
  templateUrl: './playbooks.component.html',
  styleUrl: './playbooks.component.css'
})
export class PlaybooksComponent implements OnInit {

  playbookNames: string[] = [];
  selectedPlaybook: number = 0;
  result: string = '';
  disableSaveButton: boolean = true;
  isLoading: boolean = false;

  constructor(private playbookService: PlaybookService) {}

  ngOnInit(): void {
    this.playbookService.loadPlaybookNames().subscribe({
      next: response => {
        this.playbookNames = response;
      },
      error: error => {
        this.isLoading = false;
      }
    });
  }

  load() {
    this.result = '';
    this.isLoading = true;
    this.playbookService.loadPlaybookContent(this.selectedPlaybook).subscribe({
      next: response => {
        this.result = response.output;
        this.disableSaveButton = false;
        this.isLoading = false;
      },
      error: error => {
        this.isLoading = false;
      }
    });
  }

  save(){
    this.playbookService.updatePlaybookContent(this.selectedPlaybook, this.result).subscribe(response => {
      alert('Changes in file' + this.playbookNames[this.selectedPlaybook] + ' saved!')
      this.disableSaveButton = true;
    })
  }

  onPlaybookSelected(event: any): void {
    this.result = '';
    this.disableSaveButton = true;
  }

}
