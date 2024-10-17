import { NgFor, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { PlaybookService } from '../services/playbook.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-devices',
  standalone: true,
  imports: [NgFor, FormsModule, NgIf],
  templateUrl: './devices.component.html',
  styleUrl: './devices.component.css'
})
export class DevicesComponent implements OnInit {

  devicesNames: string[] = [];
  selectedDevices: string = '';
  playbookNames: string[] = [];
  selectedPlaybook: number = 0;
  result: string = '';
  isLoading: boolean = false;

  constructor(private playbookService: PlaybookService) {}

  ngOnInit(): void {
    this.playbookService.loadDevicesNames().subscribe({
      next: response => {
        this.devicesNames = response;
      },
      error: error => {
        this.isLoading = false;
      }
    });

    this.playbookService.loadPlaybookNames().subscribe({
      next: response => {
        this.playbookNames = response;
      },
      error: error => {
        this.isLoading = false;
      }
    });
  }

  onDeviceSelected(event: any): void {
    this.selectedDevices = event.target.value;
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
