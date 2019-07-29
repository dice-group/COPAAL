import {EventEmitter, Injectable} from '@angular/core';
import {CgPath} from '../../model/cg-path';
import {CgData} from '../../model/cg-data';

@Injectable({
  providedIn: 'root'
})
export class EventProviderService {
  viewChangeEvent = new EventEmitter<boolean>();
  sendDetailEvent = new EventEmitter<CgPath[]>();
  detailClickEvent = new EventEmitter<number>();
  pathClickEvent = new EventEmitter<number>();
  updateDataEvent = new EventEmitter<CgData>();
  constructor() { }
}
