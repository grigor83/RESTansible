import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgFor, NgIf } from '@angular/common';
import { InventoryService } from '../services/inventory.service';
import { UserService } from '../services/user.service';

@Component({
  selector: 'app-hosts',
  standalone: true,
  imports: [FormsModule, NgIf, NgFor],
  templateUrl: './hosts.component.html',
  styleUrl: './hosts.component.css'
})
export class HostsComponent {
  inventories: any [] = [];
  selectedInventory: any;
  result: string = '';
  disableUpdateButton: boolean = false;
  isLoading: boolean = true;
  isModalOpen: boolean = false
  filename!: string

  constructor(private inventoryService: InventoryService, private userService: UserService) {}

  ngOnInit(): void {
    this.inventoryService.getInventories(this.userService.activeUser?.id).subscribe({
      next: response => {
        this.inventories = response;
        this.selectedInventory = this.inventories[0];
        this.isLoading = false;
      },
      error: error => {
        this.isLoading = false;
      }
    });
  }

  onInventorySelected(event: any): void {
    this.result = '';
    this.disableUpdateButton = true;
  }

  load() {
    this.result = '';
    this.isLoading = true;
    this.inventoryService.loadInventoryContent(this.selectedInventory)
    .subscribe({
      next: response => {
        this.result = response;
        this.disableUpdateButton = false;
        this.isLoading = false;
      },
      error: error => {
        this.result = "Error loading inventory content!";
        this.isLoading = false;
      }
    });
  }

  update(){
    this.inventoryService.updateInventoryContent(this.selectedInventory.id, this.result)
    .subscribe({
      next: response => {
        alert('Changes in file' + this.selectedInventory.filename + ' saved!')
        this.disableUpdateButton = true;
        this.isLoading = false;
      },
      error: error => {
        alert("Cannot update inventory file in resources!")
        this.disableUpdateButton = true;
        this.isLoading = false;
      }
    });
  }

  openModal(){
    this.result = ""
    this.isModalOpen = true;
  }

  createInventory() {
    if (!this.filename.trim()) {
      alert('Filename is required!');
      return;
    }

    this.isLoading = false;
    if (this.filename.includes(".")){
      this.filename = this.filename.split(".")[0];
    }

    this.inventoryService.createInventory(this.userService.activeUser?.id, this.filename, this.result)
      .subscribe({
        next: response => {
          alert('Created new inventory ' + this.filename + ' succesfully!')
          this.inventories.push(response);
          this.selectedInventory = response;
          this.disableUpdateButton = true;
          this.isLoading = false;
          this.closeModal();
        },
        error: error => {
          alert("Error in creating new inventory file!")
          this.disableUpdateButton = true;
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

    this.inventoryService.deleteInventory(this.selectedInventory.id)
    .subscribe({
      next: response => {
        alert('Deleted inventory ' + this.selectedInventory.filename + ' succesfully!')
        this.inventories = this.inventories.filter(inv => inv.id != this.selectedInventory.id)
        this.selectedInventory = this.inventories[0];
        this.disableUpdateButton = true;
        this.result = '';
        this.isLoading = false;
      },
      error: error => {
        alert("Error in deleting inventory file!")
        this.disableUpdateButton = true;
        this.isLoading = false;
      }
    });
  }



}
