import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UniqueIdProviderService {

  id = 1;

  constructor() {
    console.log('Unique Id service initiated.');
  }

  public getUniqueId(): number {
    return this.id++;
  }
}
