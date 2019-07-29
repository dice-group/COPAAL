import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import { GraphViewComponent } from './component/graph-view/graph-view.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {
  MatButtonModule,
  MatCardModule,
  MatCheckboxModule, MatDialogModule, MatExpansionModule,
  MatFormFieldModule,
  MatIconModule,
  MatInputModule, MatListModule, MatProgressBarModule, MatProgressSpinnerModule, MatSelectModule,
  MatTooltipModule
} from '@angular/material';
import {FlexLayoutModule} from '@angular/flex-layout';
import { UserFormComponent } from './component/user-form/user-form.component';
import { DetailViewComponent } from './component/detail-view/detail-view.component';
import { ResultViewComponent } from './component/result-view/result-view.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import { HelpDescComponent } from './component/help-desc/help-desc.component';
import { GraphDescComponent } from './component/graph-desc/graph-desc.component';

@NgModule({
  declarations: [
    AppComponent,
    GraphViewComponent,
    UserFormComponent,
    DetailViewComponent,
    ResultViewComponent,
    HelpDescComponent,
    GraphDescComponent
  ],
  imports: [
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    BrowserModule,
    BrowserAnimationsModule,
    FlexLayoutModule,
    MatCardModule,
    MatCheckboxModule,
    MatIconModule,
    MatTooltipModule,
    MatExpansionModule,
    MatListModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    MatProgressBarModule,
    MatSelectModule,
    MatDialogModule
  ],
  providers: [],
  bootstrap: [AppComponent],
  entryComponents: [HelpDescComponent, GraphDescComponent]
})
export class AppModule { }
