export class CgTriple {
  subject: string;
  property: string;
  object: string;

  constructor(subject: string, property: string, object: string) {
    this.subject = subject;
    this.property = property;
    this.object = object;
  }
}
