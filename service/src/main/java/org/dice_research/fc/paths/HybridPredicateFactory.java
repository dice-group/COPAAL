package org.dice_research.fc.paths;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.sparql.filter.IRIFilter;
import org.dice_research.fc.sparql.restrict.BGPBasedVirtualTypeRestriction;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;
import org.dice_research.fc.sparql.restrict.TypeBasedRestriction;
import org.dice_research.fc.sparql.restrict.VirtualTypeRestriction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
/**
 * This class returns a {@link Predicate} , Hybrid mean it use both {@link PathBasedFactChecker} and {@link VirtualTypePredicateFactory}.
 *
 */
@Component
public class HybridPredicateFactory implements FactPreprocessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(PredicateFactory.class);

    private QueryExecutionFactory executioner;

    private boolean useBGPVirtualTypeRestriction = true;

    private Collection<IRIFilter> filters;

    @Autowired
    public HybridPredicateFactory(QueryExecutionFactory qef) {
        this.executioner = qef;
    }

    @Autowired
    public HybridPredicateFactory(QueryExecutionFactory qef, boolean useBGPVirtualTypeRestriction,Collection<IRIFilter> filters) {
        this.executioner = qef;
        this.useBGPVirtualTypeRestriction = useBGPVirtualTypeRestriction;
        this.filters = filters;
    }

    @Override
    public Predicate generatePredicate(Statement triple) {
        Set<String> dTypes = getDomain(triple);
        ITypeRestriction domain;
        LOGGER.trace("Found the following classes for the domain: {}", dTypes);
        if(dTypes.size() == 0){
            // use Virtual type
            if(!useBGPVirtualTypeRestriction){
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
            if(!useBGPVirtualTypeRestriction){
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
    public Set<String> getDomain(Statement triple) {
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
    public Set<String> getRange(Statement triple) {
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
    public Set<String> getObjects(Resource subject, Property predicate) {
        Set<String> types = new HashSet<String>();
        StringBuilder selectBuilder = new StringBuilder();
        selectBuilder.append("SELECT  *\n");
        selectBuilder.append("WHERE\n");
        selectBuilder.append("{ <"+subject+">\n");
        selectBuilder.append("<"+predicate+">  ?x.\n");
        for (IRIFilter f:filters) {
            f.addFilter("x", selectBuilder);
        }
        selectBuilder.append("}");

        Query query = QueryFactory.create(selectBuilder.toString());
        try (QueryExecution queryExecution = executioner.createQueryExecution(query)) {
            ResultSet resultSet = queryExecution.execSelect();
            while (resultSet.hasNext()) {
                types.add(resultSet.next().get("x").asResource().getURI());
            }
        }
        return types;
    }
}
