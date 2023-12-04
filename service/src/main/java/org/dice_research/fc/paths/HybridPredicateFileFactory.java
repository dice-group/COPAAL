package org.dice_research.fc.paths;

import org.apache.jena.rdf.model.Statement;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.sparql.restrict.BGPBasedVirtualTypeRestriction;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;
import org.dice_research.fc.sparql.restrict.VirtualTypeRestriction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class returns a {@link Predicate} , Hybrid mean it use both {@link PathBasedFactChecker} and {@link VirtualTypePredicateFactory}.
 * This class similar {@link HybridPredicateFactory} just it accept list of predicate in initialization phase in future it will use Tentris then instead  QueryExecutionFactory it will use TentrisAdapter
 *
 */

@Component
public class HybridPredicateFileFactory implements FactPreprocessor {

    Map<String, Predicate> allPredicatesMap;
    private boolean useBGPVirtualTypeRestriction = true;
    @Autowired
    public HybridPredicateFileFactory(Set<Predicate> allPredicates, boolean useBGPVirtualTypeRestriction) {
        this.useBGPVirtualTypeRestriction = useBGPVirtualTypeRestriction;
        allPredicatesMap = new HashMap<String, Predicate>();

        for(Predicate p:allPredicates){
            allPredicatesMap.put(p.getProperty().getURI(),p);
        }
    }

    @Override
    public Predicate generatePredicate(Statement triple){
        if(allPredicatesMap.containsKey(triple.getPredicate().getURI())){
            Predicate existPredicate = allPredicatesMap.get(triple.getPredicate().getURI());

            if(existPredicate.getDomain()==null){
                // use virtual type
                ITypeRestriction domain;
                if(!useBGPVirtualTypeRestriction){
                    domain = new VirtualTypeRestriction(true, triple.getPredicate().getURI());
                }else{
                    domain = new BGPBasedVirtualTypeRestriction(true, triple.getPredicate().getURI());
                }
                existPredicate.setDomain(domain);
            }

            if(existPredicate.getRange()==null){
                ITypeRestriction range;
                // use Virtual type
                if(!useBGPVirtualTypeRestriction){
                    range = new VirtualTypeRestriction(false, triple.getPredicate().getURI());
                }else{
                    range = new BGPBasedVirtualTypeRestriction(false, triple.getPredicate().getURI());
                }
                existPredicate.setRange(range);
            }

            return existPredicate;
        }
        return null;
    }
}
