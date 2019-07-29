import { Component, OnInit } from '@angular/core';
import {MatDialogRef} from '@angular/material';

@Component({
  selector: 'app-help-desc',
  templateUrl: './help-desc.component.html',
  styleUrls: ['./help-desc.component.css']
})
export class HelpDescComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<HelpDescComponent>) { }

  ngOnInit() {
  }

  onOkClick(): void {
    this.dialogRef.close();
  }

}
