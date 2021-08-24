package org.dice_research.fc.paths.verbalizer;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.dice_research.fc.data.FactCheckingResult;
import org.dice_research.fc.data.IPieceOfEvidence;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Implementations of this interface should describe the retrieval of the verbalized output.
 * 
 * @author Alexandra Silva
 *
 */
@Component
@Scope(value = "prototype")
public interface IPathVerbalizer {

  /**
   * Verbalizes a {@link FactCheckingResult}
   * 
   * @param result
   */
  default void verbalizeResult(FactCheckingResult result) {
    Statement fact = result.getFact();
    for (IPieceOfEvidence curPath : result.getPiecesOfEvidence()) {
      verbalizePaths(fact.getSubject(), fact.getObject().asResource(), curPath);
    }
  };

  /**
   * Retrieves the verbalized output of a {@link IPieceOfEvidence} and sets it to the path object.
   * 
   * @param subject The fact's subject.
   * @param object The fact's object.
   * @param path The path between subject and object.
   * @return
   */
  String verbalizePaths(Resource subject, Resource object, IPieceOfEvidence path);

}
