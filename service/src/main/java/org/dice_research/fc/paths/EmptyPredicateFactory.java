package org.dice_research.fc.paths;

import java.util.HashSet;
import java.util.Set;
import org.apache.jena.rdf.model.Statement;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;
import org.dice_research.fc.sparql.restrict.TypeBasedRestriction;

/**
 * This class returns a {@link Predicate} object without type restrictions
 *
 */
public class EmptyPredicateFactory implements FactPreprocessor {

  @Override
  public Predicate generatePredicate(Statement triple) {
    Set<String> dTypes = new HashSet<String>();
    Set<String> rTypes = new HashSet<String>();

    ITypeRestriction domain = new TypeBasedRestriction(dTypes);
    ITypeRestriction range = new TypeBasedRestriction(rTypes);

    return new Predicate(triple.getPredicate(), domain, range);
  }

}
