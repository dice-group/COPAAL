
export class CgNodeItem {
  cx: number;
  cy: number;
  uri: string;
  pathId?: number;
  imagePath?: string;
  constructor(cx: number, cy: number, uri: string, imagePath?: string) {
    this.cx = cx;
    this.cy = cy;
    this.uri = uri;
    this.imagePath = imagePath;
  }
}
