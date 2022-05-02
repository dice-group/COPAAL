package org.dice_research.fc.sparql.path;

import org.apache.commons.math3.util.Pair;
import org.apache.jena.rdf.model.Property;
import org.dice_research.fc.data.QRestrictedPath;

/**
 * This implementation of a {@link IPathClauseGenerator} uses property paths as defined in SPARQL 1.1
 * to express the given q-retricted paths.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class PropPathBasedPathClauseGenerator implements IPathClauseGenerator {

  @Override
  public void addPath(QRestrictedPath path, StringBuilder queryBuilder, String subjectVariable,
      String objectVariable, String intermediateName) {
    // Start with the subject variable
    queryBuilder.append(" ?");
    queryBuilder.append(subjectVariable);
    queryBuilder.append(' ');
    // Add the path as property path
    boolean first = true;
    for (Pair<Property, Boolean> p : path.getPathElements()) {
      if (first) {
        first = false;
      } else {
        queryBuilder.append('/');
      }
      if (!p.getSecond()) {
        queryBuilder.append('^');
      }
      queryBuilder.append('<');
      queryBuilder.append(p.getFirst().getURI());
      queryBuilder.append('>');
    }
    // End with the object property
    queryBuilder.append(" ?");
    queryBuilder.append(objectVariable);
    queryBuilder.append(" .\n");
  }

}
