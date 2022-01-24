package org.dice.FactCheck.preprocess.service;

import org.apache.commons.math3.util.Pair;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.dice.FactCheck.preprocess.PreprocessApplication;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;
import org.dice.FactCheck.preprocess.model.Path;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.sparql.restrict.TypeBasedRestriction;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;

//@RunWith(SpringRunner.class)
@SpringBootTest(classes = PreprocessApplication.class)
public class PathServiceTest {

    @Test
    public void forOnePredicateServiceShouldReturnTwoPath() throws CloneNotSupportedException {
        IPathService service = new PathService();

        //Property
        Model model = ModelFactory.createDefaultModel();
        Property property = model.createProperty("http://example.org/property1");

        //Domain
        Set<String> domainSet = new HashSet<String>();
        domainSet.add("domain");
        ITypeRestriction domain = new TypeBasedRestriction(domainSet);

        //Range
        Set<String> rangeSet = new HashSet<String>();
        rangeSet.add("range");
        ITypeRestriction range = new TypeBasedRestriction(rangeSet);

        List<Predicate> input = new ArrayList<>();
        input.add(new Predicate(property,domain,range));

        List<Path> actual = (List<Path>) service.generateAllPaths(input,1, null, false);

        Assert.assertEquals(2, actual.size());
    }

    /* Correct answer is
    * P1 T P1 T
    * P1 T P1 F
    * P1 F P1 T
    * P1 F P1 F
    *
    * P1 T P2 T
    * P1 T P2 F
    * P1 F P2 T
    * P1 F P2 F
    *
    * P2 T P1 T
    * P2 T P1 F
    * P2 F P1 T
    * P2 F P1 F
    *
    * P2 T P2 T
    * P2 T P2 F
    * P2 F P2 T
    * P2 F P2 F
    *
    * */

    @Test
    public void forTwoPredicateWithSameDomainAndRangeServiceShouldReturnCorrectAnswer() throws CloneNotSupportedException {
        IPathService service = new PathService();

        // The First Property
        //Property
        Model model = ModelFactory.createDefaultModel();
        Property property1 = model.createProperty("http://example.org/property1");

        //Domain
        Set<String> domainSet1 = new HashSet<String>();
        domainSet1.add("x");
        ITypeRestriction domain1 = new TypeBasedRestriction(domainSet1);

        //Range
        Set<String> rangeSet1 = new HashSet<String>();
        rangeSet1.add("x");
        ITypeRestriction range1 = new TypeBasedRestriction(rangeSet1);

        // The second Property
        //Property
        Property property2 = model.createProperty("http://example.org/property2");

        //Domain
        Set<String> domainSet2 = new HashSet<String>();
        domainSet2.add("x");
        ITypeRestriction domain2 = new TypeBasedRestriction(domainSet2);

        //Range
        Set<String> rangeSet2 = new HashSet<String>();
        rangeSet2.add("x");
        ITypeRestriction range2 = new TypeBasedRestriction(rangeSet2);

        List<Predicate> input = new ArrayList<>();
        Predicate predicate1 = new Predicate(property1,domain1,range1);
        input.add(predicate1);
        Predicate predicate2 = new Predicate(property2,domain2,range2);
        input.add(predicate2);

        // The third Property
        //Property
        Property property3 = model.createProperty("http://example.org/property3");

        Predicate predicate3 = new Predicate(property3,domain2,range2);
        input.add(predicate2);

        service.generateAllPaths(input,2, null, false);

        Set<Path> actual = service.getAllPathWithAllLength();

        Assert.assertEquals(20 ,actual.size());

        // P1 T
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate1 , true));

        // P1 F
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate1 , false));

        // P2 T
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate2 , true));

        // P2 F
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate2 , false));

        // P1 T P1 T
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate1 , predicate1 , true ,true));

        // P1 T P1 F
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate1 , predicate1 , true , false));

        // P1 F P1 T
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate1 , predicate1 , false , true));

        // P1 F P1 F
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate1 , predicate1 , false , false));

        // P1 T P2 T
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate1 , predicate2 , true , true));

        // P1 T P2 F
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate1 , predicate2 , true , false));

        // P1 F P2 T
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate1 , predicate2 , false , true));

        // P1 F P2 F
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate1 , predicate2 , false , false));

        // P2 T P1 T
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate2 , predicate1 , true , true));

        // P2 T P1 F
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate2 , predicate1 , true , false));

        // P2 F P1 T
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate2 , predicate1 , false , true));

        // P2 F P1 F
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate2 , predicate1 , false , false));

        // P2 T P2 T
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate2 , predicate2 , true , true));

        // P2 T P2 F
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate2 , predicate2 , true , false));

        // P2 F P2 T
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate2 , predicate2 , false , true));

        // P2 F P2 F
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate2 , predicate2 , false , false));

        // should not exist

        // P3 T
        Assert.assertEquals(false, theExpectedResultExists(actual , predicate3 , true));
    }

    @Test
    public void forTwoPredicateWithSameDomainDifferentRangeServiceShouldReturnCorrectAnswer() throws CloneNotSupportedException {
        IPathService service = new PathService();

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

        // The second Property
        //Property
        Property property2 = model.createProperty("http://example.org/property2");

        //Domain
        Set<String> domainSet2 = new HashSet<String>();
        domainSet2.add("x2");
        domainSet2.add("x1");
        ITypeRestriction domain2 = new TypeBasedRestriction(domainSet2);

        //Range
        Set<String> rangeSet2 = new HashSet<String>();
        rangeSet2.add("range2");
        ITypeRestriction range2 = new TypeBasedRestriction(rangeSet2);

        List<Predicate> input = new ArrayList<>();
        Predicate predicate1 = new Predicate(property1,domain1,range1);
        input.add(predicate1);
        Predicate predicate2 = new Predicate(property2,domain2,range2);
        input.add(predicate2);

        service.generateAllPaths(input,2, null, false);

        Set<Path> actual = service.getAllPathWithAllLength();

        Assert.assertEquals(10 ,actual.size());

        // P1 T
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate1 , true));

        // P1 F
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate1 , false));

        // P2 T
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate2 , true));

        // P2 F
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate2 , false));

        // P1 T P1 T
        Assert.assertEquals(false, theExpectedResultExists(actual , predicate1 , predicate1 , true ,true));

        // P1 T P1 F
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate1 , predicate1 , true , false));

        // P1 F P1 T
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate1 , predicate1 , false , true));

        // P1 F P1 F
        Assert.assertEquals(false, theExpectedResultExists(actual , predicate1 , predicate1 , false , false));

        // P1 T P2 T
        Assert.assertEquals(false, theExpectedResultExists(actual , predicate1 , predicate2 , true , true));

        // P1 T P2 F
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate1 , predicate2 , true , false));

        // P1 F P2 T
        Assert.assertEquals(false, theExpectedResultExists(actual , predicate1 , predicate2 , false , true));

        // P1 F P2 F
        Assert.assertEquals(false, theExpectedResultExists(actual , predicate1 , predicate2 , false , false));

        // P2 T P1 T
        Assert.assertEquals(false, theExpectedResultExists(actual , predicate2 , predicate1 , true , true));

        // P2 T P1 F
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate2 , predicate1 , true , false));

        // P2 F P1 T
        Assert.assertEquals(false, theExpectedResultExists(actual , predicate2 , predicate1 , false , true));

        // P2 F P1 F
        Assert.assertEquals(false, theExpectedResultExists(actual , predicate2 , predicate1 , false , false));

        // P2 T P2 T
        Assert.assertEquals(false, theExpectedResultExists(actual , predicate2 , predicate2 , true , true));

        // P2 T P2 F
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate2 , predicate2 , true , false));

        // P2 F P2 T
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate2 , predicate2 , false , true));

        // P2 F P2 F
        Assert.assertEquals(false, theExpectedResultExists(actual , predicate2 , predicate2 , false , false));

        // should not exist

        // The third Property
        //Property
        Property property3 = model.createProperty("http://example.org/property3");

        Predicate predicate3 = new Predicate(property3,domain2,range2);

        // P3 T
        Assert.assertEquals(false, theExpectedResultExists(actual , predicate3 , true));
    }

    @Test
    public void forTwoPredicateWithDifferentDomainAndSameRangeServiceShouldReturnCorrectAnswer() throws CloneNotSupportedException {
        IPathService service = new PathService();

        // The First Property
        //Property
        Model model = ModelFactory.createDefaultModel();
        Property property1 = model.createProperty("http://example.org/property1");

        //Domain
        Set<String> domainSet1 = new HashSet<String>();
        domainSet1.add("domain");
        ITypeRestriction domain1 = new TypeBasedRestriction(domainSet1);

        //Range
        Set<String> rangeSet1 = new HashSet<String>();
        rangeSet1.add("x");
        ITypeRestriction range1 = new TypeBasedRestriction(rangeSet1);

        // The second Property
        //Property
        Property property2 = model.createProperty("http://example.org/property2");

        //Domain
        Set<String> domainSet2 = new HashSet<String>();
        domainSet2.add("x");
        domainSet2.add("domain");

        ITypeRestriction domain2 = new TypeBasedRestriction(domainSet2);

        //Range
        Set<String> rangeSet2 = new HashSet<String>();
        rangeSet2.add("x");
        ITypeRestriction range2 = new TypeBasedRestriction(rangeSet2);

        List<Predicate> input = new ArrayList<>();
        Predicate predicate1 = new Predicate(property1,domain1,range1);
        input.add(predicate1);
        Predicate predicate2 = new Predicate(property2,domain2,range2);
        input.add(predicate2);

        service.generateAllPaths(input,2, null, false);

        Set<Path> actual = service.getAllPathWithAllLength();

        Assert.assertEquals(10 ,actual.size());

        // P1 T
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate1 , true));

        // P1 F
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate1 , false));

        // P2 T
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate2 , true));

        // P2 F
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate2 , false));

        // P1 T P1 T
        Assert.assertEquals(false, theExpectedResultExists(actual , predicate1 , predicate1 , true ,true));

        // P1 T P1 F
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate1 , predicate1 , true , false));

        // P1 F P1 T
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate1 , predicate1 , false , true));

        // P1 F P1 F
        Assert.assertEquals(false, theExpectedResultExists(actual , predicate1 , predicate1 , false , false));

        // P1 T P2 T
        Assert.assertEquals(false, theExpectedResultExists(actual , predicate1 , predicate2 , true , true));

        // P1 T P2 F
        Assert.assertEquals(false, theExpectedResultExists(actual , predicate1 , predicate2 , true , false));

        // P1 F P2 T
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate1 , predicate2 , false , true));

        // P1 F P2 F
        Assert.assertEquals(false, theExpectedResultExists(actual , predicate1 , predicate2 , false , false));

        // P2 T P1 T
        Assert.assertEquals(false, theExpectedResultExists(actual , predicate2 , predicate1 , true , true));

        // P2 T P1 F
        Assert.assertEquals(false, theExpectedResultExists(actual , predicate2 , predicate1 , true , false));

        // P2 F P1 T
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate2 , predicate1 , false , true));

        // P2 F P1 F
        Assert.assertEquals(false, theExpectedResultExists(actual , predicate2 , predicate1 , false , false));

        // P2 T P2 T
        Assert.assertEquals(false, theExpectedResultExists(actual , predicate2 , predicate2 , true , true));

        // P2 T P2 F
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate2 , predicate2 , true , false));

        // P2 F P2 T
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate2 , predicate2 , false , true));

        // P2 F P2 F
        Assert.assertEquals(false, theExpectedResultExists(actual , predicate2 , predicate2 , false , false));
    }

    @Test
    public void forTwoPredicateWithNoDomainAndRangeServiceShouldReturnCorrectAnswer() throws CloneNotSupportedException {
        IPathService service = new PathService();

        // The First Property
        //Property
        Model model = ModelFactory.createDefaultModel();
        Property property1 = model.createProperty("http://example.org/property1");

        //Domain
        Set<String> domainSet1 = new HashSet<String>();
        domainSet1.add("domain1");
        ITypeRestriction domain1 = new TypeBasedRestriction(domainSet1);

        //Range
        Set<String> rangeSet1 = new HashSet<String>();
        rangeSet1.add("range1");
        ITypeRestriction range1 = new TypeBasedRestriction(rangeSet1);

        // The second Property
        //Property
        Property property2 = model.createProperty("http://example.org/property2");

        //Domain
        Set<String> domainSet2 = new HashSet<String>();
        domainSet2.add("domain2");
        ITypeRestriction domain2 = new TypeBasedRestriction(domainSet2);

        //Range
        Set<String> rangeSet2 = new HashSet<String>();
        rangeSet2.add("range2");
        ITypeRestriction range2 = new TypeBasedRestriction(rangeSet2);

        List<Predicate> input = new ArrayList<>();
        Predicate predicate1 = new Predicate(property1,domain1,range1);
        input.add(predicate1);
        Predicate predicate2 = new Predicate(property2,domain2,range2);
        input.add(predicate2);

        service.generateAllPaths(input,3, null, false);

        Set<Path> actual = service.getAllPathWithAllLength();

        Assert.assertEquals(12 ,actual.size());

        // P1 T
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate1 , true));

        // P1 F
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate1 , false));

        // P2 T
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate2 , true));

        // P2 F
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate2 , false));

    }

    @Test
    public void howMuchTimeDoesItNeed() throws CloneNotSupportedException {


        // Get current size of heap in bytes
        long heapSize = Runtime.getRuntime().totalMemory();
        System.out.println("heapSize: "+heapSize);


        // Get maximum size of heap in bytes. The heap cannot grow beyond this size.// Any attempt will result in an OutOfMemoryException.
        long heapMaxSize = Runtime.getRuntime().maxMemory();
        System.out.println("heapMaxSize: "+heapMaxSize);

        // Get amount of free memory within the heap in bytes. This size will increase // after garbage collection and decrease as new objects are created.
        long heapFreeSize = Runtime.getRuntime().freeMemory();
        System.out.println("heapFreeSize: "+heapFreeSize);

        IPathService service = new PathService();

        int numberOfProperties = 750;
        //int numberOfProperties = 6;

        List<Predicate> input = new ArrayList<>();

        long startTime = System.nanoTime();

        for(int i = 0 ; i < numberOfProperties ; i++){
            //Property
            Model model = ModelFactory.createDefaultModel();
            Property property = model.createProperty("http://example.org/property"+i);

            //Domain
            Set<String> domainSet = new HashSet<String>();
            domainSet.add("x");
            ITypeRestriction domain = new TypeBasedRestriction(domainSet);

            //Range
            Set<String> rangeSet = new HashSet<String>();
            rangeSet.add("x");
            ITypeRestriction range = new TypeBasedRestriction(rangeSet);

            Predicate predicate = new Predicate(property,domain,range);
            input.add(predicate);
        }

        service.generateAllPaths(input, 3, null, false);

        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
        Set<Path> actual = service.getAllPathWithAllLength();
        System.out.println("total time "+totalTime/1000000000+ " sec .");
        System.out.println("number of combination " +actual.size());



    }

    @Test
    public void doesHaveOverlapShouldWorkWhenOneTheSubclassIsJustOWLThing(){
        PathService service = new PathService();

        Set<String> firstRestrictionSet = new HashSet<String>();
        firstRestrictionSet.add("http://dbpedia.org/ontology/Work");
        ITypeRestriction firstRestriction = new TypeBasedRestriction(firstRestrictionSet);

        Set<String> secondRestrictionSet = new HashSet<>();
        secondRestrictionSet.add("http://dbpedia.org/ontology/Newspaper");
        ITypeRestriction secondRestriction = new TypeBasedRestriction(secondRestrictionSet);

        boolean actual  = service.doesHaveOverlap(firstRestriction, secondRestriction);

        Assert.assertEquals(false, actual);
    }

    @Test
    public void predicateCompatibleWithExistingPathShouldWorkWellRealUri(){

        theFirstPartOfPath();
        theSecondPartOfPath();
        theThirdPartOfPath();
        theFourthPartOfPath();
    }

    private void theFirstPartOfPath() {
        PathService service = new PathService();

        Path mockPath = new Path();
        mockPath.addPart(makePredicate("http://dbpedia.org/ontology/starring","http://dbpedia.org/ontology/Work","http://dbpedia.org/ontology/Actor"),true);

        Predicate predicateToAddInPath = makePredicate("http://dbpedia.org/ontology/sisterNewspaper","http://dbpedia.org/ontology/Newspaper","http://dbpedia.org/ontology/Newspaper");

        boolean actual = service.isNewPredicateCompatibleWithExistingPath(mockPath,predicateToAddInPath,false);
        Assert.assertEquals(true, actual);
    }

    private void theSecondPartOfPath() {
        PathService service = new PathService();

        Path mockPath = new Path();
        mockPath.addPart(makePredicate("http://dbpedia.org/ontology/starring","http://dbpedia.org/ontology/Work","http://dbpedia.org/ontology/Actor"),true);
        mockPath.addPart(makePredicate("http://dbpedia.org/ontology/sisterNewspaper","http://dbpedia.org/ontology/Newspaper","http://dbpedia.org/ontology/Newspaper"),false);

        Predicate predicateToAddInPath = makePredicate("http://dbpedia.org/ontology/magazine","http://dbpedia.org/ontology/WrittenWork","http://dbpedia.org/ontology/Magazine");

        boolean actual = service.isNewPredicateCompatibleWithExistingPath(mockPath,predicateToAddInPath,false);
        Assert.assertEquals(true, actual);
    }

    private void theThirdPartOfPath() {
        PathService service = new PathService();

        Path mockPath = new Path();
        mockPath.addPart(makePredicate("http://dbpedia.org/ontology/starring","http://dbpedia.org/ontology/Work","http://dbpedia.org/ontology/Actor"),true);
        mockPath.addPart(makePredicate("http://dbpedia.org/ontology/sisterNewspaper","http://dbpedia.org/ontology/Newspaper","http://dbpedia.org/ontology/Newspaper"),true);
        mockPath.addPart(makePredicate("http://dbpedia.org/ontology/magazine","http://dbpedia.org/ontology/WrittenWork","http://dbpedia.org/ontology/Magazine"),false);

        Predicate predicateToAddInPath = makePredicate("http://dbpedia.org/ontology/composer","http://dbpedia.org/ontology/Work","http://dbpedia.org/ontology/Person");

        boolean actual = service.isNewPredicateCompatibleWithExistingPath(mockPath,predicateToAddInPath,false);
        Assert.assertEquals(true, actual);

        //if the added predicate in inverted it should return false
        actual = service.isNewPredicateCompatibleWithExistingPath(mockPath,predicateToAddInPath,true);
        Assert.assertEquals(false, actual);
    }

    private void theFourthPartOfPath() {
        PathService service = new PathService();

        Path mockPath = new Path();
        mockPath.addPart(makePredicate("http://dbpedia.org/ontology/starring","http://dbpedia.org/ontology/Work","http://dbpedia.org/ontology/Actor"),true);
        mockPath.addPart(makePredicate("http://dbpedia.org/ontology/sisterNewspaper","http://dbpedia.org/ontology/Newspaper","http://dbpedia.org/ontology/Newspaper"),true);
        mockPath.addPart(makePredicate("http://dbpedia.org/ontology/magazine","http://dbpedia.org/ontology/WrittenWork","http://dbpedia.org/ontology/Magazine"),false);
        mockPath.addPart(makePredicate("http://dbpedia.org/ontology/composer","http://dbpedia.org/ontology/Work","http://dbpedia.org/ontology/Person"),false);

        Predicate predicateToAddInPath = makePredicate("http://dbpedia.org/ontology/starring","http://dbpedia.org/ontology/Work","http://dbpedia.org/ontology/Actor");

        boolean actual = service.isNewPredicateCompatibleWithExistingPath(mockPath,predicateToAddInPath,false);
        Assert.assertEquals(true, actual);

        //if the added predicate in inverted it should return false
        actual = service.isNewPredicateCompatibleWithExistingPath(mockPath,predicateToAddInPath,true);
        Assert.assertEquals(false, actual);
    }

    @Test
    public void predicateCompatibleWithExistingPathShouldWorkandConsiderCaseSensivity() {
        //http://dbpedia.org/ontology/work:[]
        //http://dbpedia.org/ontology/Work:[http://www.w3.org/2002/07/owl#Thing]
        PathService service = new PathService();

        Path mockPath = new Path();
        mockPath.addPart(makePredicate("p1","http://dbpedia.org/ontology/work","http://dbpedia.org/ontology/work"), false);

        Predicate predicateToAddInPath = makePredicate("p2","http://dbpedia.org/ontology/Work","http://dbpedia.org/ontology/Work");

        boolean actual = service.isNewPredicateCompatibleWithExistingPath(mockPath,predicateToAddInPath,false);
        Assert.assertEquals(false, actual);
    }

    @Test
    public void predicateCompatibleWithExistingPathShouldWorkWhenDomainAndRangeParentsAreNull() {
        //http://dbpedia.org/ontology/work:[]
        //http://dbpedia.org/ontology/Architect:[http://dbpedia.org/ontology/Person,http://dbpedia.org/ontology/Animal,http://dbpedia.org/ontology/Eukaryote,http://dbpedia.org/ontology/Species,http://www.w3.org/2002/07/owl#Thing]
        //http://dbpedia.org/ontology/Monastry:[]
        PathService service = new PathService();

        Path mockPath = new Path();
        mockPath.addPart(makePredicate("p1","http://dbpedia.org/ontology/work","http://dbpedia.org/ontology/work"), false);

        Predicate predicateToAddInPath = makePredicate("p2","http://dbpedia.org/ontology/Architect","http://dbpedia.org/ontology/Architect");

        boolean actual = service.isNewPredicateCompatibleWithExistingPath(mockPath,predicateToAddInPath,false);
        Assert.assertEquals(false, actual);


        predicateToAddInPath = makePredicate("p2","http://dbpedia.org/ontology/Monastry","http://dbpedia.org/ontology/Monastry");
        actual = service.isNewPredicateCompatibleWithExistingPath(mockPath,predicateToAddInPath,false);
        Assert.assertEquals(false, actual);
    }

    @Test
    public void predicateCompatibleWithExistingPathShouldWorkForSameDomainAndRange() {
        //http://dbpedia.org/ontology/Architect:[http://dbpedia.org/ontology/Person,http://dbpedia.org/ontology/Animal,http://dbpedia.org/ontology/Eukaryote,http://dbpedia.org/ontology/Species,http://www.w3.org/2002/07/owl#Thing]

        PathService service = new PathService();

        Path mockPath = new Path();
        mockPath.addPart(makePredicate("p1","http://dbpedia.org/ontology/Architect","http://dbpedia.org/ontology/Architect"), false);

        Predicate predicateToAddInPath = makePredicate("p2","http://dbpedia.org/ontology/Architect","http://dbpedia.org/ontology/Architect");

        boolean actual = service.isNewPredicateCompatibleWithExistingPath(mockPath,predicateToAddInPath,false);
        Assert.assertEquals(true, actual);
    }

    @Test
    public void predicateCompatibleWithExistingPathShouldWorkWhenOneOfDomainAndRangeIsAnotherParent() {
        //http://dbpedia.org/ontology/Architect:[http://dbpedia.org/ontology/Person,http://dbpedia.org/ontology/Animal,http://dbpedia.org/ontology/Eukaryote,http://dbpedia.org/ontology/Species,http://www.w3.org/2002/07/owl#Thing]
        //http://dbpedia.org/ontology/Person:[http://dbpedia.org/ontology/Animal,http://dbpedia.org/ontology/Eukaryote,http://dbpedia.org/ontology/Species,http://www.w3.org/2002/07/owl#Thing]
        // here the Person is the parent for Architect

        PathService service = new PathService();

        Path mockPath = new Path();
        mockPath.addPart(makePredicate("p1","http://dbpedia.org/ontology/Architect","http://dbpedia.org/ontology/Architect"), false);

        Predicate predicateToAddInPath = makePredicate("p2","http://dbpedia.org/ontology/Person","http://dbpedia.org/ontology/Person");

        boolean actual = service.isNewPredicateCompatibleWithExistingPath(mockPath,predicateToAddInPath,false);
        Assert.assertEquals(true, actual);
    }

    @Test
    public void isNewPredicateCompatibleWithExistingPathShouldWorkForCommonParentInALlInvertedDirection() {

        //http://dbpedia.org/ontology/Architect:[http://dbpedia.org/ontology/Person,http://dbpedia.org/ontology/Animal,http://dbpedia.org/ontology/Eukaryote,http://dbpedia.org/ontology/Species,http://www.w3.org/2002/07/owl#Thing]
        //http://dbpedia.org/ontology/Judge:[http://dbpedia.org/ontology/Person,http://dbpedia.org/ontology/Animal,http://dbpedia.org/ontology/Eukaryote,http://dbpedia.org/ontology/Species,http://www.w3.org/2002/07/owl#Thing]
        //http://dbpedia.org/ontology/Software:[http://dbpedia.org/ontology/Work,http://www.w3.org/2002/07/owl#Thing]
        //http://dbpedia.org/ontology/Letter:[http://dbpedia.org/ontology/WrittenWork,http://dbpedia.org/ontology/Work,http://www.w3.org/2002/07/owl#Thing]
        // here two by two has common parents
        PathService service = new PathService();

        Path mockPath = new Path();
        mockPath.addPart(makePredicate("p1","http://dbpedia.org/ontology/Architect","http://dbpedia.org/ontology/Software"), false);

        Predicate predicateToAddInPath = makePredicate("p2","http://dbpedia.org/ontology/Judge","http://dbpedia.org/ontology/Letter");

        boolean actual = service.isNewPredicateCompatibleWithExistingPath(mockPath,predicateToAddInPath,false);
        Assert.assertEquals(false, actual);
        actual = service.isNewPredicateCompatibleWithExistingPath(mockPath,predicateToAddInPath,true);
        Assert.assertEquals(true, actual);

        mockPath = new Path();
        mockPath.addPart(makePredicate("p1","http://dbpedia.org/ontology/Architect","http://dbpedia.org/ontology/Software"), true);

        actual = service.isNewPredicateCompatibleWithExistingPath(mockPath,predicateToAddInPath,false);
        Assert.assertEquals(true, actual);
        actual = service.isNewPredicateCompatibleWithExistingPath(mockPath,predicateToAddInPath,true);
        Assert.assertEquals(false, actual);
    }

    @Test
    public void isNewPredicateCompatibleWithExistingPathShouldWorkWhenDomainAndRangeHaveCommonParent() {

        //http://dbpedia.org/ontology/Architect:[http://dbpedia.org/ontology/Person,http://dbpedia.org/ontology/Animal,http://dbpedia.org/ontology/Eukaryote,http://dbpedia.org/ontology/Species,http://www.w3.org/2002/07/owl#Thing]
        //http://dbpedia.org/ontology/Judge:[http://dbpedia.org/ontology/Person,http://dbpedia.org/ontology/Animal,http://dbpedia.org/ontology/Eukaryote,http://dbpedia.org/ontology/Species,http://www.w3.org/2002/07/owl#Thing]
        // Here ThePerson is common parent for Architect and Judge
        PathService service = new PathService();

        Path mockPath = new Path();
        mockPath.addPart(makePredicate("p1","http://dbpedia.org/ontology/Architect","http://dbpedia.org/ontology/Architect"), false);

        Predicate predicateToAddInPath = makePredicate("p2","http://dbpedia.org/ontology/Judge","http://dbpedia.org/ontology/Judge");

        boolean actual = service.isNewPredicateCompatibleWithExistingPath(mockPath,predicateToAddInPath,false);
        Assert.assertEquals(true, actual);
        actual = service.isNewPredicateCompatibleWithExistingPath(mockPath,predicateToAddInPath,true);
        Assert.assertEquals(true, actual);
    }

    @Test
    public void isNewPredicateCompatibleWithExistingPathShouldWorkWhenDomainAndRangeHaveNotCommonParent() {

        //http://dbpedia.org/ontology/Architect:[http://dbpedia.org/ontology/Person,http://dbpedia.org/ontology/Animal,http://dbpedia.org/ontology/Eukaryote,http://dbpedia.org/ontology/Species,http://www.w3.org/2002/07/owl#Thing]
        //http://dbpedia.org/ontology/Software:[http://dbpedia.org/ontology/Work,http://www.w3.org/2002/07/owl#Thing]
        // Here Software and Architect does not have a shared parent
        // we dont consider http://www.w3.org/2002/07/owl#Thing
        PathService service = new PathService();

        Path mockPath = new Path();
        mockPath.addPart(makePredicate("p1","http://dbpedia.org/ontology/Architect","http://dbpedia.org/ontology/Architect"), false);

        Predicate predicateToAddInPath = makePredicate("p2","http://dbpedia.org/ontology/Software","http://dbpedia.org/ontology/Software");

        boolean actual = service.isNewPredicateCompatibleWithExistingPath(mockPath,predicateToAddInPath,false);
        Assert.assertEquals(false, actual);
        actual = service.isNewPredicateCompatibleWithExistingPath(mockPath,predicateToAddInPath,true);
        Assert.assertEquals(false, actual);
    }


    private Predicate makePredicate(String uri, String domainUri, String rangeUri) {
        //Property
        Model model = ModelFactory.createDefaultModel();
        Property property = model.createProperty(uri);

        //Domain
        Set<String> domainSet = new HashSet<String>();
        domainSet.add(domainUri);
        ITypeRestriction domain = new TypeBasedRestriction(domainSet);

        //Range
        Set<String> rangeSet = new HashSet<String>();
        rangeSet.add(rangeUri);
        ITypeRestriction range = new TypeBasedRestriction(rangeSet);

        Predicate predicate = new Predicate(property,domain,range);
        return predicate;
    }

    private boolean theExpectedResultExists(Set<Path> actual, Predicate predicate1, boolean inverted1) {
        LinkedList<Pair<Predicate, Boolean>> ShouldContainsThisSubPaths = new LinkedList<>();
        ShouldContainsThisSubPaths.add(new Pair<>(predicate1,inverted1));
        Path shouldContainThisPath = new Path(ShouldContainsThisSubPaths);
        return actual.contains(shouldContainThisPath);
    }

    private boolean theExpectedResultExists(Set<Path> actual, Predicate predicate1, Predicate predicate2, boolean inverted1, boolean inverted2) {
        LinkedList<Pair<Predicate, Boolean>> ShouldContainsThisSubPaths = new LinkedList<>();
        ShouldContainsThisSubPaths.add(new Pair<>(predicate1,inverted1));
        ShouldContainsThisSubPaths.add(new Pair<>(predicate2,inverted2));
        Path shouldContainThisPath = new Path(ShouldContainsThisSubPaths);
        return actual.contains(shouldContainThisPath);
    }

    @Test
    public void runService() throws CloneNotSupportedException, IOException {
        PredicateService predicateService = new PredicateService(null);

        Collection<Predicate> predicates = predicateService.allPredicates("collected_predicates.json");

        PathService pathService = new PathService();
        pathService.generateAllPaths(predicates , 3, null, false);
        Set<Path> paths = pathService.getAllPathWithAllLength();

        FileWriter fw = new FileWriter("allPathsWithAncestorsCalculated.txt");

        try {
            Iterator<Path> iterator = paths.iterator();
            while (iterator.hasNext()) {
                fw.write(iterator.next().toString());
                fw.write("\n");
            }
            fw.close();
        }catch(Exception ex){

        }


        FileOutputStream fileOut = null;
        try {
            fileOut =
                    new FileOutputStream("allPathsWithAncestorsCalculatedItIsSetOfPaths.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(paths);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }finally {
            System.out.println("Any body here ...");
            try {
                if (fileOut != null) {
                    System.out.println("CloseConn");
                    fileOut.close();
                    System.out.println("CloseConnDone");
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
