package org.dice_research.fc.paths;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.sparql.restrict.BGPBasedVirtualTypeRestriction;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;
import org.dice_research.fc.sparql.restrict.TypeBasedRestriction;
import org.dice_research.fc.sparql.restrict.VirtualTypeRestriction;
import org.dice_research.fc.tentris.TentrisAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.json.*;

import java.util.HashSet;
import java.util.Set;

/**
 * This class returns a {@link Predicate} , Hybrid mean it use both {@link PathBasedFactChecker} and {@link VirtualTypePredicateFactory}.
 * This class similar {@link HybridPredicateFactory} just it use Tentris then instead  QueryExecutionFactory it use TentrisAdapter
 *
 */

@Component
public class HybridPredicateTentrisFactory implements FactPreprocessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(PredicateFactory.class);
    private static final String RESULTS_KEY_WORD = "results";
    private static final String BINDINGS_KEY_WORD = "bindings";

    private TentrisAdapter adapter;

    private boolean useBGPVirtualTypeRestriction = true;

    @Autowired
    public HybridPredicateTentrisFactory(TentrisAdapter adapter) {
        this.adapter = adapter;
    }

    @Autowired
    public HybridPredicateTentrisFactory(TentrisAdapter adapter, boolean useBGPVirtualTypeRestriction) {
        this.adapter = adapter;
        this.useBGPVirtualTypeRestriction = useBGPVirtualTypeRestriction;
    }

    @Override
    public Predicate generatePredicate(Statement triple) throws Exception {
        Set<String> dTypes = getDomain(triple);
        ITypeRestriction domain;
        LOGGER.trace("Found the following classes for the domain: {}", dTypes);
        if(dTypes.size() == 0){
            // use Virtual type
            if(useBGPVirtualTypeRestriction){
                domain = new VirtualTypeRestriction(true, triple.getPredicate().getURI());
            }else{
                domain = new BGPBasedVirtualTypeRestriction(true, triple.getPredicate().getURI());
            }
        }else{
            domain = new TypeBasedRestriction(dTypes);
        }


        Set<String> rTypes = getRange(triple);
        ITypeRestriction range;
        LOGGER.trace("Found the following classes for the range: {}", rTypes);
        if(rTypes.size() == 0){
            // use Virtual type
            if(useBGPVirtualTypeRestriction){
                range = new VirtualTypeRestriction(false, triple.getPredicate().getURI());
            }else{
                range = new BGPBasedVirtualTypeRestriction(false, triple.getPredicate().getURI());
            }
        }else{
            range = new TypeBasedRestriction(rTypes);
        }

        return new Predicate(triple.getPredicate(), domain, range);
    }

    /**
     * Retrieves the domain of a given predicate or the subject types if empty
     *
     * @param triple
     * @return the domain as a set of URIs
     */
    public Set<String> getDomain(Statement triple) throws Exception {
        Set<String> sTypes = getObjects(triple.getPredicate(), RDFS.domain);
        if (sTypes.isEmpty()) {
            sTypes = getObjects(triple.getSubject(), RDF.type);
        }
        return sTypes;
    }

    /**
     * Retrieves the range of a given predicate or the object types if empty
     *
     * @param triple
     * @return the range as a set of URIs
     */
    public Set<String> getRange(Statement triple) throws Exception {
        Set<String> oTypes = getObjects(triple.getPredicate(), RDFS.range);
        if (oTypes.isEmpty()) {
            oTypes = getObjects(triple.getObject().asResource(), RDF.type);
        }
        return oTypes;
    }

    /**
     * Retrieves the objects present in the graph with a given subject and predicate
     *
     * @param subject the subject we want to check the objects for
     * @param predicate the predicate we want to check the objects for
     * @return Returns a set of the objects' URIs
     */
    public Set<String> getObjects(Resource subject, Property predicate) throws Exception {
        Set<String> types = new HashSet<String>();
        StringBuilder selectBuilder = new StringBuilder();

        selectBuilder.append("select * where {<");
        selectBuilder.append(subject.getURI());
        selectBuilder.append("> <");
        selectBuilder.append(predicate.getURI());
        selectBuilder.append("> ?x .}");

        String json = adapter.executeQuery(selectBuilder);
        if(json.equals("")){
            throw new Exception("could not get object from tenteris adapter");
        }
        types = parseJsonResult(json);

        return types;
    }

    //{"head":{"vars":["x"]},"results":{"bindings":[{"x":{"type":"uri","value":"http://dbpedia.org/ontology/Person"}}]}}
    public Set<String> parseJsonResult(String jsonString) {
        LOGGER.info("json file is "+jsonString);
        Set<String> resultSet = new HashSet<>();
        JSONObject obj = new JSONObject(jsonString);

        JSONObject results =  obj.getJSONObject("results");
        JSONArray bindings = results.getJSONArray("bindings");

        for(int i = 0 ; i < bindings.length();i++){
            String uri = bindings.getJSONObject(i).getJSONObject("x").getString("value");
            resultSet.add(uri);
        }

        return resultSet;
    }
}
