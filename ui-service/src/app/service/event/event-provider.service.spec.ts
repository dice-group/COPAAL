import { TestBed } from '@angular/core/testing';

import { EventProviderService } from './event-provider.service';

describe('EventProviderService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: EventProviderService = TestBed.get(EventProviderService);
    expect(service).toBeTruthy();
  });
});
