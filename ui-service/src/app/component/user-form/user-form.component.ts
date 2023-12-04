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


      public predicates: String[];
      constructor(public eventService: EventProviderService, public restService: RestService, fb: FormBuilder, public dialog: MatDialog, private autoCompleteService: AutocompleteService) {
        this.predicates = [];
        this.predicates.push('starring');
        this.predicates.push('birthPlace');
        this.predicates.push('award');
        this.predicates.push('deathPlace');
        this.predicates.push('subsidiary');
        this.predicates.push('publication');
        this.predicates.push('spouse');
        this.predicates.push('foundation');
        this.predicates.push('affiliation');
        this.predicates.push('chancellor');
        this.predicates.push('city');
        this.predicates.push('director');
        this.predicates.push('producer');
        this.predicates.push('productionCompany');
        this.predicates.push('academicDiscipline');
        this.predicates.push('writer');
        this.predicates.push('nationality');

      this.exampleArr = this.insertSamples();

    this.subjectFc = new FormControl(this.exampleArr[0].subject, Validators.required);
    this.propertyFc = new FormControl(this.exampleArr[0].property, Validators.required);
    this.objectFc = new FormControl(this.exampleArr[0].object, Validators.required);
    this.verbalizeFc =  new FormControl(false);
    this.complexForm = fb.group({
      'subject' : this.subjectFc,
      'predicate': this.propertyFc,
      'object' : this.objectFc,
      'verbalize' : this.verbalizeFc
    });

        this.complexForm.patchValue({'subject': this.exampleArr[0].subject , 'predicate': 16 , 'object': this.exampleArr[0].object});
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

  insertSamples() {
    let temp: CgTriple[] = [];
    let exObj: CgTriple = new CgTriple('Barack_Obama',
      'nationality', 'United_States');
    temp.push(exObj);

    exObj = new CgTriple('Predator_(film)',
      'starring', 'Carl_Weathers');
    temp.push(exObj);

    exObj = new CgTriple('Frank_Zappa',
      'birthPlace', 'Baltimore');
    temp.push(exObj);

    exObj = new CgTriple('Robert_Andrews_Millikan',
      'award', 'Nobel_Prize_in_Physics');
    temp.push(exObj);

    exObj = new CgTriple('Richard_Rodgers',
      'deathPlace', 'New_York_City');
    temp.push(exObj);

    exObj = new CgTriple('BGI_Group',
      'subsidiary', 'Complete_Genomics');
    temp.push(exObj);

    exObj = new CgTriple('Krista_Allen',
      'spouse', 'Mams_Taylor');
    temp.push(exObj);

    exObj = new CgTriple('University_of_Queensland',
      'affiliation', 'Washington_University_in_St._Louis');
    temp.push(exObj);


    exObj = new CgTriple('Pori_(film)',
      'writer', 'Subramaniam_Siva');
    temp.push(exObj);

    return temp;
  }

  submitForm(value: any): void {
    // tslint:disable-next-line:radix
    value.subject = "http://dbpedia.org/resource/"+value.subject;
    value.property = "http://dbpedia.org/ontology/"+this.predicates[parseInt(value.predicate)];
    value.object = "http://dbpedia.org/resource/"+value.object;
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

  onOptionsSelected(value: string) {
    const curSel: CgTriple = this.exampleArr[value];
    this.subjectFc.setValue(curSel.subject);
    this.propertyFc.setValue(curSel.property);
    this.objectFc.setValue(curSel.object);
    // tslint:disable-next-line:max-line-length
    this.complexForm.patchValue({
      'subject': curSel.subject,
      'predicate': this.predicates.findIndex(p => p == curSel.property),
      'object': curSel.object
    });
  }
  onInputChange(input, query) {
    // Call the autoCompleteService to search for options based on the input and query
    this.autoCompleteService.search(input, query).subscribe(options => {
      // Update the searchResults with the retrieved options
      this.searchResults = options;
    });
  }

/*  onSubjectSelected(option: any): void {
    // add the selected option and convert it to the uri, also replace space with _
    const uri = environment.dbpediaUrlBaseI + option.replace(/\s+/g, '_');
    this.subjectFc.setValue(uri);
  }*/

/*  onObjectSelected(option: any): void {
    // add the selected option and convert it to the uri, also replace space with _
    const uri = environment.dbpediaUrlBaseI + option.replace(/\s+/g, '_');
    this.objectFc.setValue(uri);
  }*/

}
