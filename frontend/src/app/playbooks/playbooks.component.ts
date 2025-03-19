import { NgFor, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { PlaybookService } from '../services/playbook.service';
import { UserService } from '../services/user.service';

@Component({
  selector: 'app-playbooks',
  standalone: true,
  imports: [FormsModule, NgFor, NgIf],
  templateUrl: './playbooks.component.html',
  styleUrl: './playbooks.component.css'
})
export class PlaybooksComponent implements OnInit {

  playbooks: any[] = [];
  selectedPlaybook: any;
  result: string = '';
  disableSaveButton: boolean = true;
  isModalOpen: boolean = false
  filename!: string
  isLoading: boolean = false;

  constructor(private playbookService: PlaybookService, private userService: UserService) {}

  ngOnInit(): void {
    this.playbookService.getPlaybooks(this.userService.activeUser?.id).subscribe({
      next: response => {
        this.playbooks = response;
        this.selectedPlaybook = this.playbooks[0];
      },
      error: error => {
        this.isLoading = false;
      }
    });
  }

  onPlaybookSelected(event: any): void {
    this.result = '';
    this.disableSaveButton = true;
  }

  load() {
    this.result = '';
    this.isLoading = true;
    this.playbookService.loadPlaybookContent(this.selectedPlaybook)
    .subscribe({
      next: response => {
        this.result = response;
        this.disableSaveButton = false;
        this.isLoading = false;
      },
      error: error => {
        this.result = "Error loading playbook content!";
        this.isLoading = false;
      }
    });
  }

  save(){
    this.playbookService.updatePlaybookContent(this.selectedPlaybook.id, this.result)
    .subscribe({
      next: response => {
        alert('Changes in file' + this.selectedPlaybook.filename + ' saved!')
        this.disableSaveButton = true;
        this.isLoading = false;
      },
      error: error => {
        alert("Cannot update playbook file in resources!")
        this.disableSaveButton = true;
        this.isLoading = false;
      }
    });
  }

  openModal(){
    this.result = ""
    this.isModalOpen = true;
  }

  createPlaybook() {
    if (!this.filename.trim()) {
      alert('Filename is required!');
      return;
    }

    if (this.filename.includes(".")){
      this.filename = this.filename.split(".")[0];
    }
    this.filename = this.filename + ".yaml";

    this.playbookService.createPlaybook(this.userService.activeUser?.id, this.filename, this.result)
      .subscribe({
        next: response => {
          alert('Created new playbook ' + this.filename + ' succesfully!')
          this.playbooks.push(response);
          this.selectedPlaybook = response;
          this.disableSaveButton = true;
          this.isLoading = false;
          this.closeModal();
        },
        error: error => {
          alert("Error in creating new playbook file!")
          this.disableSaveButton = true;
          this.isLoading = false;
          this.closeModal();
        }
      });
  }

  closeModal() {
    this.filename = '';
    this.isModalOpen = false;
  }

}
