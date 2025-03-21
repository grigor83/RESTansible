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
  oldContent: string = '';
  content: string = '';
  disableUpdateButton: boolean = true;
  isModalOpen: boolean = false
  filename: string = '';
  isLoading: boolean = false;

  constructor(private playbookService: PlaybookService, private userService: UserService) {}

  ngOnInit(): void {
    this.playbookService.getPlaybooks(this.userService.activeUser?.id).subscribe({
      next: response => {
        this.playbooks = response;
        this.selectedPlaybook = this.playbooks[0];
        this.load();
      },
      error: error => {
        this.isLoading = false;
      }
    });
  }

  onTextChange(newValue: string) {
    if (this.content != this.oldContent)
      this.disableUpdateButton = false;
    else
      this.disableUpdateButton = true;
  }

  onPlaybookSelected(event: any): void {
    this.content = '';
    this.load();
  }

  load() {
    this.content = '';
    this.isLoading = true;
    this.disableUpdateButton = true;
    this.playbookService.loadPlaybookContent(this.selectedPlaybook)
    .subscribe({
      next: response => {
        this.content = response;
        this.oldContent = response;
        this.isLoading = false;
      },
      error: error => {
        this.content = "Error loading playbook content!";
        this.isLoading = false;
      }
    });
  }

  update(){
    this.disableUpdateButton = true;
    this.isLoading = true;
    this.playbookService.updatePlaybookContent(this.selectedPlaybook.id, this.content)
    .subscribe({
      next: response => {
        this.isLoading = false;
        alert('Changes in file' + this.selectedPlaybook.filename + ' saved!')
      },
      error: error => {
        this.isLoading = false;
        alert("Cannot update playbook file in resources!");
        this.content = this.oldContent;
      }
    });
  }

  openModal(){
    this.content = ""
    this.isModalOpen = true;
  }

  createPlaybook() {
    if (this.filename == '' || !this.filename.trim()) {
      alert('Filename is required!');
      return;
    }

    this.isLoading = false;
    this.oldContent = this.content;
    if (this.filename.includes(".")){
      this.filename = this.filename.split(".")[0];
    }
    this.filename = this.filename + ".yaml";

    this.playbookService.createPlaybook(this.userService.activeUser?.id, this.filename, this.content)
      .subscribe({
        next: response => {
          alert('Created new playbook ' + this.filename + ' succesfully!')
          this.playbooks.push(response);
          this.selectedPlaybook = response;
          this.isLoading = false;
          this.closeModal();
        },
        error: error => {
          alert("Error in creating new playbook file!")
          this.isLoading = false;
          this.closeModal();
        }
      });
  }

  closeModal() {
    this.filename = '';
    this.isModalOpen = false;
  }

  delete(){
    this.isLoading = true;

    this.playbookService.deletePlaybook(this.selectedPlaybook.id)
    .subscribe({
      next: response => {
        alert('Deleted playbook ' + this.selectedPlaybook.filename + ' succesfully!')
        this.playbooks = this.playbooks.filter(playbook => playbook.id != this.selectedPlaybook.id)
        this.selectedPlaybook = this.playbooks[0];
        this.load();
      },
      error: error => {
        alert("Error in deleting playbook file!")
        this.isLoading = false;
      }
    });
  }

}
