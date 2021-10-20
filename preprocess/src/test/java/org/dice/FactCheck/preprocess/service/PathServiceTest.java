package org.dice.FactCheck.preprocess.service;

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
    public void forOnePredicateServiceShouldReturnNoPath(){
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

        List<Path> actual = (List<Path>) service.generateAllPaths(input);

        Assert.assertEquals(actual.size(), 0);
    }

    @Test
    public void forTwoPredicateWithSameDomainAndRangeServiceShouldReturnCorrectAnswer(){
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
        domainSet1.add("x");
        ITypeRestriction domain2 = new TypeBasedRestriction(domainSet2);

        //Range
        Set<String> rangeSet2 = new HashSet<String>();
        rangeSet1.add("range");
        ITypeRestriction range2 = new TypeBasedRestriction(rangeSet2);

        List<Predicate> input = new ArrayList<>();
        input.add(new Predicate(property1,domain1,range1));
        input.add(new Predicate(property2,domain2,range2));

        List<Path> actual = (List<Path>) service.generateAllPaths(input);

        Assert.assertEquals(actual.size(), 0);
    }
}
