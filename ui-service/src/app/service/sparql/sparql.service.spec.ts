import { TestBed } from '@angular/core/testing';

import { SparqlService } from './sparql.service';

describe('SparqlService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: SparqlService = TestBed.get(SparqlService);
    expect(service).toBeTruthy();
  });
});
