package org.dice_research.fc.paths;

import org.apache.jena.rdf.model.Statement;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;
import org.dice_research.fc.sparql.restrict.VirtualTypeRestriction;
import org.springframework.stereotype.Component;

/**
 * This class returns a {@link Predicate} with virtual type restrictions.
 *
 */
@Component
public class VirtualTypePredicateFactory implements FactPreprocessor {

    @Override
    public Predicate generatePredicate(Statement triple) {
        ITypeRestriction domain = new VirtualTypeRestriction(true, triple.getPredicate().getURI());
        ITypeRestriction range = new VirtualTypeRestriction(false, triple.getPredicate().getURI());

        return new Predicate(triple.getPredicate(), domain, range);
    }

}
