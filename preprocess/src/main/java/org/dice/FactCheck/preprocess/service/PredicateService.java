package org.dice.FactCheck.preprocess.service;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;

import java.util.*;


import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.paths.PredicateFactory;
import org.dice_research.fc.sparql.restrict.TypeBasedRestriction;
import org.hibernate.tuple.PropertyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
* this class fetch all predicates from KG
* */

public class PredicateService implements IPredicateService{

    private static final Logger LOGGER = LoggerFactory.getLogger(PredicateService.class);

    private QueryExecutionFactory executioner;
    private PredicateFactory predicateFacroty;

    public PredicateService(QueryExecutionFactory executioner) {
        this.executioner = executioner;
        predicateFacroty = new PredicateFactory(executioner);
    }

    @Override
    public Collection<Predicate> allPredicates(Collection<String> predicateFilter) {
        Set<Predicate> predicates = new HashSet<Predicate>();
        Set<String> predicatesIRIs = (Set<String>) allPredicateIRIs(predicateFilter);
        for(String iri : predicatesIRIs){
            predicates.add(convertToPredicate(iri));
        }
        return predicates;
    }

    private Predicate convertToPredicate(String iri) {
        // get domain
        TypeBasedRestriction domain = new TypeBasedRestriction(getDomain(iri));
        // get range
        TypeBasedRestriction range = new TypeBasedRestriction(getRange(iri));

        Property p = new PropertyImpl(iri);
        Predicate predicate = new Predicate(p,domain,range);
        return predicate;
    }

    public Set<String> getDomain(String iri) {
        Set<String> sTypes = getObjects(ResourceFactory.createProperty(iri), RDFS.domain);
        if (sTypes.isEmpty()) {
            // select type of subject for this predicate as domain
            //sTypes = typeOfSubjectsForPredicate(iri);
            // TODO : what now ?
        }
        return sTypes;
    }

    public Set<String> getRange(String iri) {
        Set<String> sTypes = getObjects(ResourceFactory.createProperty(iri), RDFS.range);
        if (sTypes.isEmpty()) {
            // select type of subject for this predicate as domain
            //sTypes = typeOfSubjectsForPredicate(iri);
            // TODO : what now ?
        }
        return sTypes;
    }


    public Set<String> getObjects(Resource subject, Property predicate) {
        Set<String> types = new HashSet<String>();
        SelectBuilder selectBuilder = new SelectBuilder();
        selectBuilder.addWhere(subject, predicate, NodeFactory.createVariable("x"));

        Query query = selectBuilder.build();
        try (QueryExecution queryExecution = executioner.createQueryExecution(query)) {
            ResultSet resultSet = queryExecution.execSelect();
            while (resultSet.hasNext()) {
                types.add(resultSet.next().get("x").asResource().getURI());
            }
        }
        return types;
    }

    private Collection<String> allPredicateIRIs(Collection<String> predicateFilter) {
        Set<String> predicateIRIs = new HashSet<String>();
        SelectBuilder selectBuilder = new SelectBuilder();
        selectBuilder.addVar(NodeFactory.createVariable("p"));
        selectBuilder.setDistinct(true);
        selectBuilder.addWhere(NodeFactory.createVariable("s"), NodeFactory.createVariable("p"), NodeFactory.createVariable("o"));
        try {
            for (String filter : predicateFilter) {
                selectBuilder.addFilter("strstarts(str(?p),'" + filter + "')");
            }
        }catch (Exception ex){
            LOGGER.error(ex.getMessage());
        }

        Query query = selectBuilder.build();
        try (QueryExecution queryExecution = executioner.createQueryExecution(query)) {
            ResultSet resultSet = queryExecution.execSelect();
            while (resultSet.hasNext()) {
                String URI  = resultSet.next().get("p").asResource().getURI();
                predicateIRIs.add(URI);
            }
        }
        return predicateIRIs;

    }
}
