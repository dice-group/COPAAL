package org.dice_research.fc.sparql.path;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.util.Pair;
import org.apache.jena.rdf.model.Property;
import org.dice_research.fc.data.QRestrictedPath;

/**
 * This implementation of a {@link IPathClauseGenerator} uses basic graph patterns (BGPs) to express
 * the given q-retricted paths. Hence, it works with SPARQL 1.0 endpoints.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class BGPBasedPathClauseGenerator implements IPathClauseGenerator {

  /**
   * If this flag is {@code true}, the clause generator may add additional filters to avoid loops.
   */
  private boolean forbidLoops;

  public BGPBasedPathClauseGenerator() {
    super();
  }

  public BGPBasedPathClauseGenerator(boolean forbidLoops) {
    super();
    this.forbidLoops = forbidLoops;
  }

  @Override
  public void addPath(QRestrictedPath path, StringBuilder queryBuilder, String subjectVariable,
      String objectVariable, String intermediateName) {
    List<String> objVariables = new ArrayList<>();
    for (int i = 0; i < path.getPathElements().size(); i++) {
      Pair<Property, Boolean> pair = path.getPathElements().get(i);

      String tempSubject = intermediateName.concat(String.valueOf(i));
      String tempObject = intermediateName.concat(String.valueOf(i + 1));

      if (i == 0) {
        tempSubject = subjectVariable;
      }

      if (i + 1 == path.getPathElements().size()) {
        tempObject = objectVariable;
      }
      objVariables.add(tempObject);

      if (!pair.getSecond()) {
        String temp = tempSubject;
        tempSubject = tempObject;
        tempObject = temp;
      }
      queryBuilder.append('?');
      queryBuilder.append(tempSubject);
      queryBuilder.append(' ');
      queryBuilder.append('<');
      queryBuilder.append(pair.getFirst().getURI());
      queryBuilder.append('>');
      queryBuilder.append(' ');
      queryBuilder.append('?');
      queryBuilder.append(tempObject);
      queryBuilder.append(' ');
      queryBuilder.append('.');

      if (i + 1 < path.getPathElements().size()) {
        // space before first next variable
        queryBuilder.append(' ');
      }
    }

    if (forbidLoops) {
      if((objVariables.size())>0) {
        queryBuilder.append(" FILTER(");
        boolean first = true;
        // All intermediate nodes do not equal the subject (the object could equal)
        for (int i = 0; i < (objVariables.size() - 1); ++i) {
          if (first) {
            first = false;
          } else {
            queryBuilder.append(" && ");
          }
          queryBuilder.append('?');
          queryBuilder.append(subjectVariable);
          queryBuilder.append(" != ?");
          queryBuilder.append(objVariables.get(i));
        }

        // All intermediate nodes do not equal each other and do not equal the object variable
        for (int i = 0; i < objVariables.size(); ++i) {
          for (int j = i + 1; j < objVariables.size(); ++j) {
            queryBuilder.append(" && ?");
            queryBuilder.append(objVariables.get(i));
            queryBuilder.append(" != ?");
            queryBuilder.append(objVariables.get(j));
          }
        }
        queryBuilder.append(")");
      }
    }
  }

  /**
   * @return the forbidLoops
   */
  public boolean isForbidLoops() {
    return forbidLoops;
  }

  /**
   * @param forbidLoops the forbidLoops to set
   */
  public void setForbidLoops(boolean forbidLoops) {
    this.forbidLoops = forbidLoops;
  }

}
