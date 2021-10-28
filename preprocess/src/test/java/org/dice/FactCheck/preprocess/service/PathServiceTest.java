package org.dice.FactCheck.preprocess.service;

import org.apache.commons.math3.util.Pair;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.dice.FactCheck.preprocess.PreprocessApplication;
import org.dice.FactCheck.preprocess.model.ITypeRestriction;
import org.dice.FactCheck.preprocess.model.Path;
import org.dice.FactCheck.preprocess.model.Predicate;
import org.dice.FactCheck.preprocess.model.TypeBasedRestriction;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PreprocessApplication.class)
public class PathServiceTest {

    @Test
    public void forOnePredicateServiceShouldReturnNoPath() throws CloneNotSupportedException {
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

        Set<Path> actual = (Set<Path>) service.generateAllPaths(input,1);

        Assert.assertEquals(0, actual.size());
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

        service.generateAllPaths(input,2);

        Set<Path> actual = service.getAllPathWithAllLength();

        Assert.assertEquals(20 ,actual.size());

        // P1 T
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate1 , true));

        // P1 F
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate1 , false));

        // P2 T
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate2 , true));

        // P2 T
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
        Assert.assertEquals(true, theExpectedResultExists(actual , predicate1 , predicate2 , false , true));

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
    public void howMuchTimeDoesItNeed() throws CloneNotSupportedException {
        IPathService service = new PathService();

        int numberOfProperties = 60;

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

        service.generateAllPaths(input, 3);

        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
        Set<Path> actual = service.getAllPathWithAllLength();
        System.out.println("total time "+totalTime/1000000000+ " sec .");
        System.out.println("number of combination " +actual.size());



    }
}
