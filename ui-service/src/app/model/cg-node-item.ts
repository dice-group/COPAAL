
export class CgNodeItem {
  cx: number;
  cy: number;
  uri: string;
  pathId?: number;
  constructor(cx: number, cy: number, uri: string) {
    this.cx = cx;
    this.cy = cy;
    this.uri = uri;
  }
}
