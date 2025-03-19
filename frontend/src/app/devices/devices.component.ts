import { NgFor, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { PlaybookService } from '../services/playbook.service';
import { FormsModule } from '@angular/forms';
import { UserService } from '../services/user.service';
import { InventoryService } from '../services/inventory.service';

@Component({
  selector: 'app-devices',
  standalone: true,
  imports: [NgFor, FormsModule, NgIf],
  templateUrl: './devices.component.html',
  styleUrl: './devices.component.css'
})
export class DevicesComponent implements OnInit {

  hosts: any[] = [];
  selectedHost: any;
  playbooks: any[] = [];
  selectedPlaybook: any;
  result: string = '';
  isLoading: boolean = false;

  constructor(private inventoryService: InventoryService, private playbookService: PlaybookService, private userService: UserService) {}

  ngOnInit(): void {
    if (this.userService.activeUser != null){
      this.inventoryService.getHostsAndPlaybooks(this.userService.activeUser.id).subscribe({
        next: response => {
          this.hosts = response;
          this.selectedHost = this.hosts[0]
          this.playbooks = this.selectedHost.playbooks;
          this.selectedPlaybook = this.playbooks[0];
        },
        error: error => {
          this.isLoading = false;
        }
      });
     }
  }

  onHostChange() {
    this.playbooks = this.selectedHost.playbooks;
    this.selectedPlaybook = this.playbooks[0];
  }

  play() {
    this.result = '';
    this.isLoading = true;
    this.playbookService.play(this.selectedPlaybook).subscribe({
      next: response => {
        this.result = response.output;
        this.isLoading = false
      },
      error: error => {
        this.isLoading = false;
      }
    });
  }

}
