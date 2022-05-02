package org.dice.FactCheck.preprocess.service;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.dice.FactCheck.preprocess.model.CountQueries;
import org.dice.FactCheck.preprocess.model.Path;
import org.dice.FactCheck.preprocess.utilities;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;
import org.dice_research.fc.sparql.restrict.TypeBasedRestriction;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.*;

public class CounterQueryGeneratorServiceTest {
    @Test
    public void ServiceShouldForPredicateAndPathWithLengthTwoWorksFine(){
        CounterQueryGeneratorService service = new CounterQueryGeneratorService(new PathService());

        // The First Property
        //Property
        Model model = ModelFactory.createDefaultModel();
        Property property1 = model.createProperty("http://example.org/property1");

        //Domain
        Set<String> domainSet1 = new HashSet<String>();
        domainSet1.add("x1");
        domainSet1.add("x2");
        ITypeRestriction domain1 = new TypeBasedRestriction(domainSet1);

        //Range
        Set<String> rangeSet1 = new HashSet<String>();
        rangeSet1.add("range1");
        ITypeRestriction range1 = new TypeBasedRestriction(rangeSet1);

        Predicate predicate1 = new Predicate(property1,domain1,range1);

        // The second Property
        //Property
        Property property2 = model.createProperty("http://example.org/property2");

        //Domain
        Set<String> domainSet2 = new HashSet<String>();
        domainSet2.add("x1");
        domainSet2.add("x2");
        ITypeRestriction domain2 = new TypeBasedRestriction(domainSet2);

        //Range
        Set<String> rangeSet2 = new HashSet<String>();
        rangeSet2.add("range2");
        ITypeRestriction range2 = new TypeBasedRestriction(rangeSet2);

        Predicate predicate2 = new Predicate(property2,domain2,range2);

        // The third Property
        //Property
        Property property3 = model.createProperty("http://example.org/property3");

        //Domain
        Set<String> domainSet3 = new HashSet<String>();
        domainSet3.add("range1");
        ITypeRestriction domain3 = new TypeBasedRestriction(domainSet3);

        //Range
        Set<String> rangeSet3 = new HashSet<String>();
        rangeSet3.add("range2");
        ITypeRestriction range3 = new TypeBasedRestriction(rangeSet3);

        Predicate predicate3 = new Predicate(property3,domain3,range3);


        List<Path> paths = new ArrayList<>();
        Path path1 = new Path();
        path1.addPart(predicate2,false);
        path1.addPart(predicate3,true);
        paths.add(path1);

        CountQueries queries = service.generateCountQueries(predicate1,paths);

        Assert.assertEquals(queries.getCoOccurrenceCountQueries().size(),1);
    }

    @Test
    public void ServiceShouldForPredicateAndPathWithLengthTwoAndDiffrentDomainRangeWorksFine(){
        CounterQueryGeneratorService service = new CounterQueryGeneratorService(new PathService());

        // The First Property
        //Property
        Model model = ModelFactory.createDefaultModel();
        Property property1 = model.createProperty("http://example.org/property1");

        //Domain
        ITypeRestriction domain1 = utilities.makeITypeRestriction("domain1");

        //Range
        ITypeRestriction range1 = utilities.makeITypeRestriction("range1");

        Predicate predicate1 = new Predicate(property1,domain1,range1);

        // The second Property
        //Property
        Property property2 = model.createProperty("http://example.org/property2");

        //Domain
        ITypeRestriction domain2 = utilities.makeITypeRestriction("x1");

        //Range
        ITypeRestriction range2 = utilities.makeITypeRestriction("range1");

        Predicate predicate2 = new Predicate(property2,domain2,range2);

        // The third Property
        //Property
        Property property3 = model.createProperty("http://example.org/property3");

        //Domain
        ITypeRestriction domain3 = utilities.makeITypeRestriction("range1");

        //Range
        ITypeRestriction range3 = utilities.makeITypeRestriction("range2");
        Predicate predicate3 = new Predicate(property3,domain3,range3);


        List<Path> paths = new ArrayList<>();
        Path path1 = new Path();
        path1.addPart(predicate2,false);
        path1.addPart(predicate3,false);
        paths.add(path1);

        CountQueries queries =  service.generateCountQueries(predicate1,paths);

        Assert.assertEquals(0, queries.getCoOccurrenceCountQueries().size());
    }

    @Test
    public void ServiceShouldForPredicateAndPathWithLengthTwoAndsameDomainRangeWorksFine(){
        CounterQueryGeneratorService service = new CounterQueryGeneratorService(new PathService());

        // The First Property
        //Property
        Model model = ModelFactory.createDefaultModel();
        Property property1 = model.createProperty("http://example.org/property1");

        //Domain
        ITypeRestriction domain1 = utilities.makeITypeRestriction("x1");

        //Range
        ITypeRestriction range1 = utilities.makeITypeRestriction("range2");

        Predicate predicate1 = new Predicate(property1,domain1,range1);

        // The second Property
        //Property
        Property property2 = model.createProperty("http://example.org/property2");

        //Domain
        ITypeRestriction domain2 = utilities.makeITypeRestriction("x1");

        //Range
        ITypeRestriction range2 = utilities.makeITypeRestriction("range1");

        Predicate predicate2 = new Predicate(property2,domain2,range2);

        // The third Property
        //Property
        Property property3 = model.createProperty("http://example.org/property3");

        //Domain
        ITypeRestriction domain3 = utilities.makeITypeRestriction("range1");

        //Range
        ITypeRestriction range3 = utilities.makeITypeRestriction("range2");
        Predicate predicate3 = new Predicate(property3,domain3,range3);


        List<Path> paths = new ArrayList<>();
        Path path1 = new Path();
        path1.addPart(predicate2,false);
        path1.addPart(predicate3,false);
        paths.add(path1);

        CountQueries queries = service.generateCountQueries(predicate1,paths);

        Assert.assertEquals(1, queries.getCoOccurrenceCountQueries().size());
    }

    @Test //uncomment if need to run
    public void howManyQueriesDoWeHave() throws CloneNotSupportedException {
    /*    howManyQueriesDoWeHave("http://dbpedia.org/ontology/nationality","Nationality","http://dbpedia.org/ontology/Person","http://dbpedia.org/ontology/Country",3);

        howManyQueriesDoWeHave("http://dbpedia.org/ontology/birthPlace","BirthPlace","http://dbpedia.org/ontology/Animal","http://dbpedia.org/ontology/Place",3);

        howManyQueriesDoWeHave("http://dbpedia.org/ontology/deathPlace","DeathPlace","http://dbpedia.org/ontology/Animal","http://dbpedia.org/ontology/Place",3);

        howManyQueriesDoWeHave("http://dbpedia.org/ontology/foundationPlace","FoundationPlace","http://dbpedia.org/ontology/Organisation","http://dbpedia.org/ontology/City",3);

        howManyQueriesDoWeHave("http://dbpedia.org/ontology/spouse","Spouse","http://dbpedia.org/ontology/Person","http://dbpedia.org/ontology/Person",3);

        howManyQueriesDoWeHave("http://dbpedia.org/ontology/starring","Starring","http://dbpedia.org/ontology/Work","http://dbpedia.org/ontology/Actor",3);

        howManyQueriesDoWeHave("http://dbpedia.org/ontology/subsidiary","Subsidiary","http://dbpedia.org/ontology/Company","http://dbpedia.org/ontology/Company",3);*/
    }

    public void howManyQueriesDoWeHave(String propertyURI, String propertyName, String domain, String range, int len ) throws CloneNotSupportedException {
        CounterQueryGeneratorService service = new CounterQueryGeneratorService(new PathService());

        // The First Property
        //Property
        Model model = ModelFactory.createDefaultModel();

        Property property1 = model.createProperty(propertyURI);

        //Domain
        ITypeRestriction domain1 = utilities.makeITypeRestriction(domain);


        //Range
        ITypeRestriction range1 = utilities.makeITypeRestriction(range);


        Predicate predicate1 = new Predicate(property1, domain1, range1);

        PredicateService predicateService = new PredicateService(null);

        Collection<Predicate> predicates = predicateService.allPredicates("collected_predicates.json");

        PathService pathService = new PathService();
        Collection<Path> paths = pathService.generateAllPaths(predicates , len,null,false);

        SaveAllPathInAFileAsText(paths);

        /*CountQueries queries = service.generateCountQueries(predicate1, paths);

        try {
            save(queries.getCoOccurrenceCountQueries(), "queriesCoOccurrence"+propertyName+len+".txt");
            save(queries.getMaxCountQueries(), "queriesMaxCount"+propertyName+len+".txt");
            save(queries.getPathInstancesCountQueries(), "queriesPathInstances"+propertyName+len+".txt");
            save(queries.getPredicateInstancesCountQueries(),"queriesPredicateInstances"+propertyName+len+".txt");
            save(queries.getTypeInstancesCountQueries(),"queriesTypeInstances"+propertyName+len+".txt");
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }*/
    }

    private void SaveAllPathInAFileAsText(Collection<Path> paths) {
        try{
            FileWriter myWriter = new FileWriter("AllPossiblePredicateCombinations_ThisFileInaTestGenerated.txt");

            for (Iterator<Path> iterator = paths.iterator(); iterator.hasNext();) {
                Path p = iterator.next();
                myWriter.write(p.toString());
            }
            myWriter.close();
        }catch (Exception ex){

        }
    }

    public static void save(Set<String> obj, String path) throws Exception {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(
                    new OutputStreamWriter(new FileOutputStream("queries/"+path), "UTF-8"));
            for (String s : obj) {
                pw.println(s);
            }
            pw.flush();
        } finally {
            pw.close();
        }
    }

    public static void save(Collection<Predicate> obj, String path) throws Exception {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(
                    new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
            for (Predicate s : obj) {
                pw.print(s.getProperty().toString());
                pw.print(",");
                pw.print(s.getDomain().toString());
                pw.print(",");
                pw.print(s.getRange().toString());
                pw.println();
            }
            pw.flush();
        } finally {
            pw.close();
        }
    }
}
