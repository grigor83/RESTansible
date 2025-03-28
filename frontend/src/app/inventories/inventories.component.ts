import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgFor, NgIf } from '@angular/common';
import { InventoryService } from '../services/inventory.service';
import { UserService } from '../services/user.service';

@Component({
  selector: 'app-inventories',
  standalone: true,
  imports: [FormsModule, NgIf, NgFor],
  templateUrl: './inventories.component.html',
  styleUrl: './inventories.component.css'
})
export class InventoriesComponent {
  inventories: any [] = [];
  selectedInventory: any;
  content: string = '';
  oldContent: string = '';
  disableUpdateButton: boolean = true;
  isLoading: boolean = true;
  isModalOpen: boolean = false
  filename: string = '';

  constructor(private inventoryService: InventoryService, private userService: UserService) {}

  ngOnInit(): void {
    this.inventoryService.getInventories(this.userService.activeUser?.id).subscribe({
      next: response => {
        this.inventories = response;
        this.selectedInventory = this.inventories[0];
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

  onTabPress(event: KeyboardEvent) {
    if (event.key === 'Tab') {
      event.preventDefault(); // Sprečite podrazumevano ponašanje Tab-a (prebacivanje fokusa)
      
      // Dodajte Tab karakter u textarea
      const textarea = event.target as HTMLTextAreaElement;
      const start = textarea.selectionStart;
      const end = textarea.selectionEnd;
  
      // Umetnite Tab karakter na poziciju kursora
      textarea.value = textarea.value.substring(0, start) + '\t' + textarea.value.substring(end);
  
      // Premestite kursor nakon unosa Tab karaktera
      textarea.selectionStart = textarea.selectionEnd = start + 1;
    }
  }
  

  onInventorySelected(event: any): void {
    this.content = '';
    this.load();
    //this.disableUpdateButton = true;
  }

  load() {
    this.content = '';
    this.oldContent = '';
    this.isLoading = true;
    this.disableUpdateButton = true;
    this.inventoryService.loadInventoryContent(this.selectedInventory)
    .subscribe({
      next: response => {
        this.content = response;
        this.oldContent = response;
        this.isLoading = false;
      },
      error: error => {
        this.content = "Error loading inventory content!";
        this.isLoading = false;
      }
    });
  }

  update(){
    this.disableUpdateButton = true;
    this.isLoading = true;
    this.inventoryService.updateInventoryContent(this.selectedInventory.id, this.content)
    .subscribe({
      next: response => {
        alert('Changes in file' + this.selectedInventory.filename + ' saved!')
        this.isLoading = false;
      },
      error: error => {
        this.isLoading = false;
        this.content = this.oldContent
        alert("Cannot update inventory file in resources!")
      }
    });
  }

  openModal(){
    this.content = ""
    this.isModalOpen = true;
  }

  createInventory() {
    if (this.filename == '' || !this.filename.trim()) {
      alert('Filename is required!');
      return;
    }

    this.isLoading = false;
    this.oldContent = this.content;
    if (this.filename.includes(".")){
      this.filename = this.filename.split(".")[0];
    }

    this.inventoryService.createInventory(this.userService.activeUser?.id, this.filename, this.content)
      .subscribe({
        next: response => {
          alert('Created new inventory ' + this.filename + ' succesfully!')
          this.inventories.push(response);
          this.selectedInventory = response;
          this.isLoading = false;
          this.closeModal();
        },
        error: error => {
          alert("Error in creating new inventory file!")
          this.isLoading = false;
          this.closeModal();
        }
      });
  }

  closeModal() {
    this.filename = '';
    this.content = this.oldContent;
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
        this.load();
      },
      error: error => {
        alert("Error in deleting inventory file!")
        this.isLoading = false;
      }
    });
  }



}
