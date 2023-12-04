import { TestBed } from '@angular/core/testing';

import { AutocompleteService } from './autocomplete.service';

describe('AutocompleteService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: AutocompleteService = TestBed.get(AutocompleteService);
    expect(service).toBeTruthy();
  });
});
