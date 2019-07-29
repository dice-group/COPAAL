export class CgLineItem {
  id: number;
  cx1: number;
  cy1: number;
  cx2: number;
  cy2: number;
  uri: string;
  isCurved: boolean;
  cpx: number;
  cpy: number;
  pathScore: number;
  isDotted: boolean;
  pathId: number;
  constructor(cx1: number, cy1: number, cx2: number, cy2: number, uri: string, pathScore: number, id, pathId) {
    this.id = id;
    this.cx1 = cx1;
    this.cy1 = cy1;
    this.cx2 = cx2;
    this.cy2 = cy2;
    this.uri = uri;
    this.pathScore = pathScore;
    this.pathId = pathId;
  }
}
