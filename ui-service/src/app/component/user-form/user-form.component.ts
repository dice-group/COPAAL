import { Component, OnInit } from '@angular/core';
import {EventProviderService} from '../../service/event/event-provider.service';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {RestService} from '../../service/rest/rest.service';
import {CgTriple} from '../../model/cg-triple';
import {GraphViewComponent} from '../graph-view/graph-view.component';
import {MatDialog, MatSelectChange} from '@angular/material';
import {HelpDescComponent} from '../help-desc/help-desc.component';
import {AutocompleteService} from "../../service/autocomplete/autocomplete.service";
import {environment} from "../../../environments/environment";

@Component({
  selector: 'app-user-form',
  templateUrl: './user-form.component.html',
  styleUrls: ['./user-form.component.css']
})
export class UserFormComponent implements OnInit {
  complexForm: FormGroup;
  subjectFc: FormControl;
  propertyFc: FormControl;
  objectFc: FormControl;
  verbalizeFc: FormControl;

  public showBar = false;

  public exampleArr: CgTriple[];
  public searchResults: string[] = [];


  constructor(public eventService: EventProviderService, public restService: RestService, fb: FormBuilder, public dialog: MatDialog, private autoCompleteService: AutocompleteService) {
    this.exampleArr = [];
/*    let exObj: CgTriple = new CgTriple('http://rdf.frockg.eu/resource/fdaers/case/8779990',
      'http://rdf.frockg.eu/resource/fdaers/occupation', 'http://rdf.frockg.eu/resource/fdaers/occupation/Y');
    this.exampleArr.push(exObj);
    exObj = new CgTriple('http://rdf.frockg.eu/resource/snomed/id/2674479021',
      'http://rdf.frockg.eu/resource/snomed/field/destination', 'http://rdf.frockg.eu/resource/snomed/id/955009');*/

    let exObj: CgTriple = new CgTriple('http://dbpedia.org/resource/Barack_Obama',
      'http://dbpedia.org/ontology/nationality', 'http://dbpedia.org/resource/United_States');
    this.exampleArr.push(exObj);
    exObj = new CgTriple('http://dbpedia.org/resource/Berkshire_Hathaway',
      'http://dbpedia.org/ontology/keyPerson', 'http://dbpedia.org/resource/Warren_Buffett');
    this.exampleArr.push(exObj);


    this.exampleArr.push(exObj);

    this.subjectFc = new FormControl(this.exampleArr[0].subject, Validators.required);
    this.propertyFc = new FormControl(this.exampleArr[0].property, Validators.required);
    this.objectFc = new FormControl(this.exampleArr[0].object, Validators.required);
    this.verbalizeFc =  new FormControl(false);
    this.complexForm = fb.group({
      'subject' : this.subjectFc,
      'property': this.propertyFc,
      'object' : this.objectFc,
      'verbalize' : this.verbalizeFc
    });
  }

  ngOnInit() {
    this.restService.requestEvnt.subscribe(val => { this.toggleProgressBar(val); });
  }
  toggleProgressBar(showBar) {
    this.showBar = showBar;
  }

  showGraph() {
    this.eventService.viewChangeEvent.emit( true );
  }

  submitForm(value: any): void {
    this.restService.getRequest('validate', value).subscribe((jsonVal) => {
      this.eventService.updateDataEvent.emit(jsonVal);
      this.eventService.viewChangeEvent.emit( true );
    });
  }

  getUriStr(uri: string) {
    return GraphViewComponent.getUriName(uri);
  }

  selectChange(event: MatSelectChange) {
    const curSel: CgTriple = this.exampleArr[event.value];
    this.subjectFc.setValue(curSel.subject);
    this.propertyFc.setValue(curSel.property);
    this.objectFc.setValue(curSel.object);
  }

  openHelpPopup() {
    this.dialog.open(HelpDescComponent);
  }

  onInputChange(input, query) {
    // Call the autoCompleteService to search for options based on the input and query
    this.autoCompleteService.search(input, query).subscribe(options => {
      // Update the searchResults with the retrieved options
      this.searchResults = options;
    });
  }

  onSubjectSelected(option: any): void {
    // add the selected option and convert it to the uri, also replace space with _
    const uri = environment.dbpediaUrlBaseI + option.replace(/\s+/g, '_');
    this.subjectFc.setValue(uri);
  }

  onObjectSelected(option: any): void {
    // add the selected option and convert it to the uri, also replace space with _
    const uri = environment.dbpediaUrlBaseI + option.replace(/\s+/g, '_');
    this.objectFc.setValue(uri);
  }

}
