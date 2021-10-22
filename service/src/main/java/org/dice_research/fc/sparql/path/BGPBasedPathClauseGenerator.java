package org.dice_research.fc.sparql.path;

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

  @Override
  public void addPath(QRestrictedPath path, StringBuilder queryBuilder, String subjectVariable,
      String objectVariable, String intermediateName) {
      for(int i = 0 ; i < path.getPathElements().size() ; i++){
        Pair<Property, Boolean> pair = path.getPathElements().get(i);

        String tempSubject = intermediateName.concat(String.valueOf(i));
        String tempObject = intermediateName.concat(String.valueOf(i+1));

        if(i==0){
          tempSubject = subjectVariable;
        }

        if(i+1 == path.getPathElements().size()){
          tempObject = objectVariable;
        }

        if(!pair.getSecond()){
          String temp = tempSubject;
          tempSubject = tempObject ;
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

        if(i+1 < path.getPathElements().size()){
          // space before first next variable
          queryBuilder.append(' ');
        }
      }
  }

}
