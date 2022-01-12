package org.dice_research.fc.paths;

import org.apache.jena.rdf.model.Statement;
import org.dice_research.fc.data.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class returns a {@link Predicate} , Hybrid mean it use both {@link PathBasedFactChecker} and {@link VirtualTypePredicateFactory}.
 * This class similar {@link HybridPredicateFactory} just it use Tentris then instead  QueryExecutionFactory it use TentrisAdapter
 *
 */

@Component
public class HybridPredicateTentrisFactory implements FactPreprocessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(PredicateFactory.class);

    Map<String, Predicate> allPredicatesMap;

    @Autowired
    public HybridPredicateTentrisFactory(Set<Predicate> allPredicates) {

        allPredicatesMap = new HashMap<String, Predicate>();

        for(Predicate p:allPredicates){
            allPredicatesMap.put(p.getProperty().getURI(),p);
        }
    }

    @Override
    public Predicate generatePredicate(Statement triple){
        if(allPredicatesMap.containsKey(triple.getPredicate().getURI())){
            return allPredicatesMap.get(triple.getPredicate().getURI());
        }
        return null;
    }
}
