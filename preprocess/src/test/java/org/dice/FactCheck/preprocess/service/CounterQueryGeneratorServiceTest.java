package org.dice.FactCheck.preprocess.service;

import ch.qos.logback.core.net.ObjectWriter;
import javassist.bytecode.stackmap.BasicBlock;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
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
        CounterQueryGeneratorService service = new CounterQueryGeneratorService();

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
        rangeSet1.add("range2");
        ITypeRestriction range2 = new TypeBasedRestriction(rangeSet2);

        Predicate predicate2 = new Predicate(property2,domain2,range2);

        // The third Property
        //Property
        Property property3 = model.createProperty("http://example.org/property3");

        //Domain
        Set<String> domainSet3 = new HashSet<String>();
        domainSet2.add("range1");
        ITypeRestriction domain3 = new TypeBasedRestriction(domainSet3);

        //Range
        Set<String> rangeSet3 = new HashSet<String>();
        rangeSet1.add("range2");
        ITypeRestriction range3 = new TypeBasedRestriction(rangeSet3);

        Predicate predicate3 = new Predicate(property3,domain3,range3);


        List<Path> paths = new ArrayList<>();
        Path path1 = new Path();
        path1.addPart(predicate2,false);
        path1.addPart(predicate3,true);
        paths.add(path1);

        HashSet<String> queries = (HashSet<String>) service.cooccurenceCount(predicate1,paths);

        Assert.assertEquals(queries.size(),1);
    }

    @Test
    public void ServiceShouldForPredicateAndPathWithLengthTwoAndDiffrentDomainRangeWorksFine(){
        CounterQueryGeneratorService service = new CounterQueryGeneratorService();

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

        HashSet<String> queries = (HashSet<String>) service.cooccurenceCount(predicate1,paths);

        Assert.assertEquals(0, queries.size());
    }

    @Test
    public void ServiceShouldForPredicateAndPathWithLengthTwoAndsameDomainRangeWorksFine(){
        CounterQueryGeneratorService service = new CounterQueryGeneratorService();

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

        HashSet<String> queries = (HashSet<String>) service.cooccurenceCount(predicate1,paths);

        Assert.assertEquals(1, queries.size());
    }

    @Test
    public void howManyQueriesDoweHave() throws CloneNotSupportedException {
        CounterQueryGeneratorService service = new CounterQueryGeneratorService();

        // The First Property
        //Property
        Model model = ModelFactory.createDefaultModel();

        //Property property1 = model.createProperty("http://dbpedia.org/ontology/nationality");
        Property property1 = model.createProperty("http://dbpedia.org/ontology/birthPlace");

        //Domain
        //ITypeRestriction domain1 = utilities.makeITypeRestriction("http://dbpedia.org/ontology/Person");
        ITypeRestriction domain1 = utilities.makeITypeRestriction("http://dbpedia.org/ontology/Animal");

        //Range
        //ITypeRestriction range1 = utilities.makeITypeRestriction("http://dbpedia.org/ontology/Country");
        ITypeRestriction range1 = utilities.makeITypeRestriction("http://dbpedia.org/ontology/Place");

        Predicate predicate1 = new Predicate(property1, domain1, range1);

        PredicateService predicateService = new PredicateService(null);

        Collection<Predicate> predicates = predicateService.allPredicates("collected_predicates.json");

        PathService pathService = new PathService();
        Collection<Path> paths = pathService.generateAllPaths(predicates , 1);

/*        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("paths3.ser"));
            out.writeObject(paths);
            out.close();
        }catch(Exception ex){
            System.out.println(ex);
        }*/


/*       try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("paths3.ser"));
           Collection<Path> paths = (Collection<Path>) in.readObject();
            in.close();
           HashSet<String> queries = (HashSet<String>) service.cooccurenceCount(predicate1, paths);

           System.out.println(queries.size());
        }catch(Exception ex){
            System.out.println(ex);
        }*/

        HashSet<String> queries = (HashSet<String>) service.cooccurenceCount(predicate1, paths);

        System.out.println(queries.size());
        try {
            save(queries, "queriesBirthPlace1.txt");
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    public static void save(Set<String> obj, String path) throws Exception {
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
