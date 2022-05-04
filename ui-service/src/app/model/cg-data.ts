import {CgPath} from './cg-path';
import {CgTriple} from './cg-triple';

export class CgData {
  piecesOfEvidence: CgPath[];
  veracityValue: number;
  fact: string;
  finalJudgement?: boolean;
}
