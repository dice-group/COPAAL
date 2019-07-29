import { Component, OnInit } from '@angular/core';
import {MatDialogRef} from '@angular/material';

@Component({
  selector: 'app-graph-desc',
  templateUrl: './graph-desc.component.html',
  styleUrls: ['./graph-desc.component.css']
})
export class GraphDescComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<GraphDescComponent>) { }

  ngOnInit() {
  }

  onOkClick(): void {
    this.dialogRef.close();
  }

}
