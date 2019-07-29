import { TestBed } from '@angular/core/testing';

import { UniqueIdProviderService } from './unique-id-provider.service';

describe('UniqueIdProviderService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: UniqueIdProviderService = TestBed.get(UniqueIdProviderService);
    expect(service).toBeTruthy();
  });
});
