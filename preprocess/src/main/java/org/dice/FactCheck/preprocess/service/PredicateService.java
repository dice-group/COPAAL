package org.dice.FactCheck.preprocess.service;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;


import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.vocabulary.RDFS;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.paths.PredicateFactory;
import org.dice_research.fc.sparql.restrict.TypeBasedRestriction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.Iterator;

/*
*
* */

public class PredicateService implements IPredicateService{

    private static final Logger LOGGER = LoggerFactory.getLogger(PredicateService.class);

    private QueryExecutionFactory executioner;
    private PredicateFactory predicateFacroty;

    public PredicateService(QueryExecutionFactory executioner) {
        this.executioner = executioner;
        predicateFacroty = new PredicateFactory(executioner);
    }

    //this method fetch all predicates from KG
    @Override
    public Collection<Predicate> allPredicates(Collection<String> predicateFilter) {
        Set<Predicate> predicates = new HashSet<Predicate>();
        Set<String> predicatesIRIs = (Set<String>) allPredicateIRIs(predicateFilter);
        for(String iri : predicatesIRIs){
            predicates.add(convertToPredicate(iri));
        }
        return predicates;
    }

    @Override
    public Collection<Predicate> allPredicates(String fileName) {
        Set<Predicate> predicates = new HashSet<Predicate>();
        JSONParser parser = new JSONParser();
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream(fileName);
            Object obj = parser.parse(new InputStreamReader(is));

            // A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
            JSONArray Predicates = (JSONArray) obj;

            Iterator<JSONObject> iterator = Predicates.iterator();
            while (iterator.hasNext()) {
                JSONObject jsonPredicate = iterator.next();
                // get domain
                Set<String> domainsSet = new HashSet<>();
                JSONArray domainsJson = (JSONArray)jsonPredicate.get("Domain") ;
                for(int i = 0 ; i < domainsJson.size() ; i++){
                    domainsSet.add(domainsJson.get(i).toString());
                }
                TypeBasedRestriction domain = new TypeBasedRestriction(domainsSet);
                // get range
                Set<String> rangesSet = new HashSet<>();
                JSONArray rangesJson = (JSONArray)jsonPredicate.get("Range") ;
                for(int i = 0 ; i < rangesJson.size() ; i++){
                    rangesSet.add(rangesJson.get(i).toString());
                }
                TypeBasedRestriction range = new TypeBasedRestriction(rangesSet);

                Property p = new PropertyImpl(jsonPredicate.get("Predicate").toString());
                Predicate predicate = new Predicate(p,domain,range);
                predicates.add(predicate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return predicates;
    }

    public Collection<String> allDomainAndRanges(String fileName){
        Set<String> allDomainAndRange = new HashSet<String>();
        JSONParser parser = new JSONParser();
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream(fileName);
            Object obj = parser.parse(new InputStreamReader(is));

            // A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
            JSONArray Predicates = (JSONArray) obj;

            Iterator<JSONObject> iterator = Predicates.iterator();
            while (iterator.hasNext()) {
                JSONObject jsonPredicate = iterator.next();
                // get domain
                JSONArray domainsJson = (JSONArray)jsonPredicate.get("Domain") ;
                for(int i = 0 ; i < domainsJson.size() ; i++){
                    allDomainAndRange.add(domainsJson.get(i).toString());
                }

                // get range
                JSONArray rangesJson = (JSONArray)jsonPredicate.get("Range") ;
                for(int i = 0 ; i < rangesJson.size() ; i++){
                    allDomainAndRange.add(rangesJson.get(i).toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allDomainAndRange;
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
