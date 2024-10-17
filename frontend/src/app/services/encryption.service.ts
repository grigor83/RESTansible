import { Injectable } from '@angular/core';
import * as CryptoJS from 'crypto-js';


@Injectable({
  providedIn: 'root'
})
export class EncryptionService {

  private secretKey: string = 'rest_ansible';

  constructor() { }

  encryptData(data: any): string {
    try {
      return CryptoJS.AES.encrypt(JSON.stringify(data), this.secretKey).toString();
    } catch (e) {
      console.error('Error during encryption:', e);
      return '';
    }
  }

  decryptData(encryptedData: string): any {
    try {
      const bytes = CryptoJS.AES.decrypt(encryptedData, this.secretKey);
      if (bytes.toString()) {
        return JSON.parse(bytes.toString(CryptoJS.enc.Utf8));
      }
      return null;
    } catch (e) {
      console.error('Error during decryption:', e);
      return null;
    }
  }
}
