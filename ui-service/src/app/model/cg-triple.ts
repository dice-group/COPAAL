export class CgTriple {
  subject: string;
  property: string;
  object: string;

  constructor(subject: string, property: string, object: string) {
    this.subject = subject;
    this.property = property;
    this.object = object;
  }

  static map(input: string) {
    let parts: string[];
    parts = input.replace('[','').replace(']','').split(', ');
    return new CgTriple(parts[0], parts[1], parts[2]);
  }
}
