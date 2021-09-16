import {AfterContentInit, Component} from '@angular/core';

import {GRAPHDATA} from './model/mock-data';
import {EventProviderService} from './service/event/event-provider.service';
import {CgData} from './model/cg-data';
import {CgTriple} from './model/cg-triple';
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  graphData: CgData;
  showGraph = false;
  title = 'cg-ui';
  constructor(eventService: EventProviderService) {
    this.graphData = GRAPHDATA;
    eventService.viewChangeEvent.subscribe((graphBool: boolean) => {
      this.setShowGraph(graphBool);
    });

    eventService.updateDataEvent.subscribe((graphData: CgData) => {
      this.graphData = graphData;
    });
  }
  getGraphData() {
    return this.graphData;
  }

  setShowGraph(val: boolean) {
    this.showGraph = val;
  }
}
