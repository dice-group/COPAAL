import {CgPath} from './cg-path';
import {CgTriple} from './cg-triple';

export class CgData {
  pathList: CgPath[];
  graphScore: number;
  inputTriple: CgTriple;
  finalJudgement?: boolean;
}
