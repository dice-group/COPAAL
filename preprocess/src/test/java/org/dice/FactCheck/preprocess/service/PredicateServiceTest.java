package org.dice.FactCheck.preprocess.service;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;

import org.apache.jena.rdf.model.Property;
import org.dice.FactCheck.preprocess.utilities;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class PredicateServiceTest {
    @Test
    public void serviceShouldBringAllThePredicatesCorrect(){
        QueryExecutionFactory qef;
        Model model = ModelFactory.createDefaultModel();
        model.read("tempPredicates.nt");
        qef = new QueryExecutionFactoryModel(model);

        PredicateService service = new PredicateService(qef);
        List<String> filters = new ArrayList<>();
        filters.add("http://www.example.org");
        Set<Predicate> actual = (Set<Predicate>) service.allPredicates(filters);

        Assert.assertEquals(5, actual.size());
    }

    public static void save(Collection<Predicate> obj, String path) throws Exception {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(
                    new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
            for (Predicate s : obj) {
                pw.println(s.getProperty().toString());
            }
            pw.flush();
        } finally {
            pw.close();
        }
    }

    public static void saveFromString(Collection<String> obj, String path) throws Exception {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(
                    new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
            for (String s : obj) {
                pw.println(s);
            }
            pw.flush();
        } finally {
            pw.close();
        }
    }

    // when need run add @Test
    public void saveAllPredicatesInFile() {
        PredicateService service = new PredicateService(null);

        Set<Predicate> actual = (Set<Predicate>) service.allPredicates("collected_predicates.json");

        try {
            save(actual,"allPredicates.txt");
        }catch (Exception ex){
            String ss = ex.getMessage();
            System.out.println(ss);
        }
    }

    // when need run add @Test
    public void saveAllDomainAndRangesInFile() {
        PredicateService service = new PredicateService(null);

        Set<String> actual = (Set<String>) service.allDomainAndRanges("collected_predicates.json");

        try {
            saveFromString(actual,"allDomainAndRanges.txt");
        }catch (Exception ex){
            String ss = ex.getMessage();
            System.out.println(ss);
        }
    }

    @Test
    public void serviceShouldBringAllThePredicatesFromFileCorrect(){
        PredicateService service = new PredicateService(null);

        Set<Predicate> actual = (Set<Predicate>) service.allPredicates("collected_predicates_TEST.json");


        Assert.assertEquals(3, actual.size());
        // The First Property
        //Property
        Model model = ModelFactory.createDefaultModel();
        Property property1 = model.createProperty("http://dbpedia.org/ontology/novel");

        //Domain
        ITypeRestriction domain1 = utilities.makeITypeRestriction("http://dbpedia.org/ontology/FictionalCharacter");

        //Range
        ITypeRestriction range1 = utilities.makeITypeRestriction("http://dbpedia.org/ontology/Novel");

        Predicate peredicate1 = new Predicate(property1,domain1,range1);

        Assert.assertEquals(true, actual.contains(peredicate1));

        // The second Property
        //Property
        Property property2 = model.createProperty("http://dbpedia.org/ontology/ground");

        //Domain
        ITypeRestriction domain2 = utilities.makeITypeRestriction("http://dbpedia.org/ontology/SoccerClub");

        //Range
        ITypeRestriction range2 = utilities.makeITypeRestriction("http://dbpedia.org/ontology/Place");

        Predicate peredicate2 = new Predicate(property2,domain2,range2);

        Assert.assertEquals(true, actual.contains(peredicate2));

        // The third Property
        //Property
        Property property3 = model.createProperty("http://dbpedia.org/ontology/starring");

        //Domain
        ITypeRestriction domain3 = utilities.makeITypeRestriction("http://dbpedia.org/ontology/Work");

        //Range
        ITypeRestriction range3 = utilities.makeITypeRestriction("http://dbpedia.org/ontology/Actor");

        Predicate peredicate3 = new Predicate(property3,domain3,range3);

        Assert.assertEquals(true, actual.contains(peredicate3));

    }
}
