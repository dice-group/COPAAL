import {Component, Input, OnInit} from '@angular/core';
import {CgData} from '../../model/cg-data';
import {EventProviderService} from '../../service/event/event-provider.service';
import {HelpDescComponent} from '../help-desc/help-desc.component';
import {MatDialog} from '@angular/material';
import {GraphDescComponent} from '../graph-desc/graph-desc.component';

@Component({
  selector: 'app-result-view',
  templateUrl: './result-view.component.html',
  styleUrls: ['./result-view.component.css']
})
export class ResultViewComponent implements OnInit {
  @Input()
  graphData: CgData;
  constructor(public eventService: EventProviderService,  public dialog: MatDialog) { }

  ngOnInit() {
  }

  showForm() {
    this.eventService.viewChangeEvent.emit( false );
  }

  openInfoPopup() {
    this.dialog.open(GraphDescComponent);
  }

}
