package org.dice_research.fc.data;

import java.util.Collection;
import java.util.Iterator;

import org.apache.jena.rdf.model.Statement;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * The result of a fact checker.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class FactCheckingResult {
  
  /**
   * The main result of the fact checking is a veracity value
   */ 
  private double veracityValue;
  
  /**
   * The pieces of evidence that have been used to come to the veracity value
   */  
  private Collection<? extends IPieceOfEvidence> piecesOfEvidence;

  /**
   * The fact we just checked
   */
  @JsonSerialize(using = ToStringSerializer.class)
  private Statement fact;

  public FactCheckingResult(double veracityValue,
      Collection<? extends IPieceOfEvidence> piecesOfEvidence, Statement fact) {
    super();
    this.veracityValue = veracityValue;
    this.piecesOfEvidence = piecesOfEvidence;
    this.fact = fact;
  }

  /**
   * @return the veracityValue
   */
  public double getVeracityValue() {
    return veracityValue;
  }

  /**
   * @param veracityValue the veracityValue to set
   */
  public void setVeracityValue(double veracityValue) {
    this.veracityValue = veracityValue;
  }

  /**
   * @return the piecesOfEvidence
   */
  public Collection<? extends IPieceOfEvidence> getPiecesOfEvidence() {
    return piecesOfEvidence;
  }

  /**
   * @param piecesOfEvidence the piecesOfEvidence to set
   */
  public void setPiecesOfEvidence(Collection<? extends IPieceOfEvidence> piecesOfEvidence) {
    this.piecesOfEvidence = piecesOfEvidence;
  }

  /**
   * @return the fact
   */
  public Statement getFact() {
    return fact;
  }

  /**
   * @param fact the fact we just checked
   */
  public void setFact(Statement fact) {
    this.fact = fact;
  }

  public String getRdfStarVersion(){
    StringBuilder rdfStarResult = new StringBuilder();
    /*t    :veracityValue 0.6;
     :copaal:score 0.42, -0.1, 0.6;
     :evidence "evidence 3", "evidence 2", "evidence 1";
     :explanation "explanation is disabled".*/
    rdfStarResult = addFact(rdfStarResult);
    rdfStarResult.append(System.getProperty("line.separator"));
    rdfStarResult = addVeracityValue(rdfStarResult,false);
    rdfStarResult.append(System.getProperty("line.separator"));
    rdfStarResult = addScores(rdfStarResult,false);
    rdfStarResult.append(System.getProperty("line.separator"));
    rdfStarResult = addEvidences(rdfStarResult,false);
    rdfStarResult.append(System.getProperty("line.separator"));
    if(explanationsAreSimilar()){
      rdfStarResult = addSingleExplanation(rdfStarResult,true);
    }else{
      rdfStarResult = addExplanation(rdfStarResult,true);
    }
    return rdfStarResult.toString();
  }

  private boolean explanationsAreSimilar() {
    Iterator<IPieceOfEvidence> iterator = (Iterator<IPieceOfEvidence>) piecesOfEvidence.iterator();
    if(piecesOfEvidence.size()<2){
      return true;
    }
    IPieceOfEvidence piece1 = iterator.next();
    while(iterator.hasNext()){
      IPieceOfEvidence piece2 = iterator.next();
      if(!piece1.getVerbalizedOutput().equals(piece2.getVerbalizedOutput())){
        return false;
      }
      piece1 = piece2;
    }
    return true;
  }

  private StringBuilder addSingleExplanation(StringBuilder sb, boolean isTheFinalPart) {
    sb.append(":explanation \"");
    Iterator<IPieceOfEvidence> iterator = (Iterator<IPieceOfEvidence>) piecesOfEvidence.iterator();
    if(iterator.hasNext()){
      IPieceOfEvidence piece = iterator.next();
      sb.append(piece.getVerbalizedOutput());
    }
    sb.append("\"");
    if(isTheFinalPart){
      sb.append(".");
    }else{
      sb.append(";");
    }
    return sb;
  }

  private StringBuilder addExplanation(StringBuilder sb, boolean isTheFinalPart) {
    sb.append(":explanation \"");
    Iterator<IPieceOfEvidence> iterator = (Iterator<IPieceOfEvidence>) piecesOfEvidence.iterator();
    while(iterator.hasNext()){
      IPieceOfEvidence piece = iterator.next();
      sb.append(piece.getVerbalizedOutput());
      if(iterator.hasNext()) {
        sb.append("\", \"");
      }
    }
    sb.append("\"");
    if(isTheFinalPart){
      sb.append(".");
    }else{
      sb.append(";");
    }
    return sb;
  }

  private StringBuilder addEvidences(StringBuilder sb, boolean isTheFinalPart) {
    sb.append("\n:evidence \"");
    Iterator<IPieceOfEvidence> iterator = (Iterator<IPieceOfEvidence>) piecesOfEvidence.iterator();
    while(iterator.hasNext()){
      IPieceOfEvidence piece = iterator.next();
      sb.append(piece.getEvidence());
      if(iterator.hasNext()) {
        sb.append("\", \"");
      }
    }
    sb.append("\"");
    if(isTheFinalPart){
      sb.append(".");
    }else{
      sb.append(";");
    }
    return sb;
  }

  private StringBuilder addScores(StringBuilder sb, boolean isTheFinalPart) {
    sb.append("\n:copaal:score ");
    Iterator<IPieceOfEvidence> iterator = (Iterator<IPieceOfEvidence>) piecesOfEvidence.iterator();
    while(iterator.hasNext()){
      IPieceOfEvidence piece = iterator.next();
      sb.append(piece.getScore());
      if(iterator.hasNext()) {
        sb.append("\", \"");
      }
    }
    if(isTheFinalPart){
      sb.append(".");
    }else{
      sb.append(";");
    }
    return sb;
  }

  public StringBuilder addVeracityValue(StringBuilder sb ,boolean isTheFinalPart){
    sb.append(":veracityValue ");
    sb.append(veracityValue);
    if(isTheFinalPart){
      sb.append(".");
    }else{
      sb.append(";");
    }
    return sb;
  }

  public StringBuilder addFact(StringBuilder sb){
    sb.append("<< ");
    sb.append(fact.getSubject());
    sb.append(" ");
    sb.append(fact.getPredicate().getURI());
    sb.append(" ");
    sb.append(fact.getObject());
    sb.append(" >> ");
    return sb;
  }

/*  t    :veracityValue 0.6;
     :hasEvidence _:1, _:2, _:3 .

  _:1 :copaal:score -0.1;
     :evidence "evidence 1";
     :explanation "explanation is disabled".

  _:2 :copaal:score 0.6;
     :evidence "evidence 2";
     :explanation "explanation is disabled".*/
  //additional resources
  public String getRdfStarVersionAR(){
    StringBuilder rdfStarResult = new StringBuilder();
    rdfStarResult = addFact(rdfStarResult);
    rdfStarResult.append(System.getProperty("line.separator"));
    rdfStarResult.append(":hasEvidence");
    rdfStarResult = addEvidencesResourcesId(rdfStarResult);
    rdfStarResult.append(System.getProperty("line.separator"));
    Iterator<IPieceOfEvidence> iterator = (Iterator<IPieceOfEvidence>) piecesOfEvidence.iterator();
    int counter = 1;
    while (iterator.hasNext()){
      IPieceOfEvidence piece = iterator.next();
      rdfStarResult = addResource(rdfStarResult,piece,counter);
      counter = counter + 1;
    }
    return rdfStarResult.toString();
  }

  private StringBuilder addResource(StringBuilder sb, IPieceOfEvidence piece, int counter) {
    sb.append(" _:");
    sb.append(counter);
    sb.append(" :copaal:score ");
    sb.append(piece.getScore());
    sb.append(";");
    sb.append(System.getProperty("line.separator"));
    sb.append(" :evidence \"");
    sb.append(piece.getEvidence());
    sb.append("\";");
    sb.append(System.getProperty("line.separator"));
    sb.append(" :explanation \"");
    sb.append(piece.getVerbalizedOutput());
    sb.append("\".");
    sb.append(System.getProperty("line.separator"));
    return sb;
  }

  private StringBuilder addEvidencesResourcesId(StringBuilder sb) {
    for(int i = 0 ; i < piecesOfEvidence.size() ; i++){
      sb.append(" _:");
      sb.append(i+1);
      if(i+1<piecesOfEvidence.size()){
        sb.append(",");
      }
      sb.append(".");
    }
    return sb;
  }
}
