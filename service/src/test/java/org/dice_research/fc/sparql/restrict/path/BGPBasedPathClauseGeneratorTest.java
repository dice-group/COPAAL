package org.dice_research.fc.sparql.restrict.path;

import org.apache.commons.math3.util.Pair;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.run.Application;
import org.dice_research.fc.sparql.path.BGPBasedPathClauseGenerator;
import org.dice_research.fc.sparql.path.IPathClauseGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class BGPBasedPathClauseGeneratorTest {

    @Test
    public void pathLen1NoInvertedShouldReturnExpectedResult(){

        List<Pair<Property, Boolean>> pathElements = new ArrayList<>();

        Model model = ModelFactory.createDefaultModel();

        // The First Property
        //Property
        Property property1 = model.createProperty("http://example.org/property1");
        boolean property1Inverted = true;

        pathElements.add(new Pair<>(property1,property1Inverted));
        //pathElements.add(new Pair<>(property2,property2Inverted));

        double score = 0.5;

        QRestrictedPath path = new QRestrictedPath(pathElements,score);

        StringBuilder queryBuilder = new StringBuilder();
        String subjectVariable = "s";
        String objectVariable = "o";
        String intermediateName = "x";

        IPathClauseGenerator generator = new BGPBasedPathClauseGenerator();
        generator.addPath(path, queryBuilder, subjectVariable,objectVariable,intermediateName);

        System.out.println(queryBuilder.toString());
        String expected = "?s <http://example.org/property1> ?o .";
        Assert.assertEquals(expected,queryBuilder.toString());
    }

    @Test
    public void pathLen1WithInvertedShouldReturnExpectedResult(){

        List<Pair<Property, Boolean>> pathElements = new ArrayList<>();

        Model model = ModelFactory.createDefaultModel();

        // The First Property
        //Property
        Property property1 = model.createProperty("http://example.org/property1");
        boolean property1Inverted = false;

        pathElements.add(new Pair<>(property1,property1Inverted));

        double score = 0.5;

        QRestrictedPath path = new QRestrictedPath(pathElements,score);

        StringBuilder queryBuilder = new StringBuilder();
        String subjectVariable = "s";
        String objectVariable = "o";
        String intermediateName = "x";

        IPathClauseGenerator generator = new BGPBasedPathClauseGenerator();
        generator.addPath(path, queryBuilder, subjectVariable,objectVariable,intermediateName);

        System.out.println(queryBuilder.toString());
        String expected = "?o <http://example.org/property1> ?s .";
        Assert.assertEquals(expected,queryBuilder.toString());
    }

    @Test
    public void pathLen2NoInvertedShouldReturnExpectedResult(){

        List<Pair<Property, Boolean>> pathElements = new ArrayList<>();

        Model model = ModelFactory.createDefaultModel();

        // The First Property
        //Property
        Property property1 = model.createProperty("http://example.org/property1");
        boolean property1Inverted = true;

        // The second Property
        //Property
        Property property2 = model.createProperty("http://example.org/property2");
        boolean property2Inverted = true;

        pathElements.add(new Pair<>(property1,property1Inverted));
        pathElements.add(new Pair<>(property2,property2Inverted));

        double score = 0.5;

        QRestrictedPath path = new QRestrictedPath(pathElements,score);

        StringBuilder queryBuilder = new StringBuilder();
        String subjectVariable = "s";
        String objectVariable = "o";
        String intermediateName = "x";

        IPathClauseGenerator generator = new BGPBasedPathClauseGenerator();
        generator.addPath(path, queryBuilder, subjectVariable,objectVariable,intermediateName);

        System.out.println(queryBuilder.toString());
        String expected = "?s <http://example.org/property1> ?x1 . ?x1 <http://example.org/property2> ?o .";
        Assert.assertEquals(expected,queryBuilder.toString());
    }

    @Test
    public void pathLen2FirstPropertyWithInvertedShouldReturnExpectedResult(){

        List<Pair<Property, Boolean>> pathElements = new ArrayList<>();

        Model model = ModelFactory.createDefaultModel();

        // The First Property
        //Property
        Property property1 = model.createProperty("http://example.org/property1");
        boolean property1Inverted = false;

        // The second Property
        //Property
        Property property2 = model.createProperty("http://example.org/property2");
        boolean property2Inverted = true;

        pathElements.add(new Pair<>(property1,property1Inverted));
        pathElements.add(new Pair<>(property2,property2Inverted));

        double score = 0.5;

        QRestrictedPath path = new QRestrictedPath(pathElements,score);

        StringBuilder queryBuilder = new StringBuilder();
        String subjectVariable = "s";
        String objectVariable = "o";
        String intermediateName = "x";

        IPathClauseGenerator generator = new BGPBasedPathClauseGenerator();
        generator.addPath(path, queryBuilder, subjectVariable,objectVariable,intermediateName);

        System.out.println(queryBuilder.toString());
        String expected = "?x1 <http://example.org/property1> ?s . ?x1 <http://example.org/property2> ?o .";
        Assert.assertEquals(expected,queryBuilder.toString());
    }

    @Test
    public void pathLen2secondPropertyWithInvertedShouldReturnExpectedResult(){

        List<Pair<Property, Boolean>> pathElements = new ArrayList<>();

        Model model = ModelFactory.createDefaultModel();

        // The First Property
        //Property
        Property property1 = model.createProperty("http://example.org/property1");
        boolean property1Inverted = true;

        // The second Property
        //Property
        Property property2 = model.createProperty("http://example.org/property2");
        boolean property2Inverted = false;

        pathElements.add(new Pair<>(property1,property1Inverted));
        pathElements.add(new Pair<>(property2,property2Inverted));

        double score = 0.5;

        QRestrictedPath path = new QRestrictedPath(pathElements,score);

        StringBuilder queryBuilder = new StringBuilder();
        String subjectVariable = "s";
        String objectVariable = "o";
        String intermediateName = "x";

        IPathClauseGenerator generator = new BGPBasedPathClauseGenerator();
        generator.addPath(path, queryBuilder, subjectVariable,objectVariable,intermediateName);

        System.out.println(queryBuilder.toString());
        String expected = "?s <http://example.org/property1> ?x1 . ?o <http://example.org/property2> ?x1 .";
        Assert.assertEquals(expected,queryBuilder.toString());
    }

    @Test
    public void pathLen2WithInvertedShouldReturnExpectedResult(){

        List<Pair<Property, Boolean>> pathElements = new ArrayList<>();

        Model model = ModelFactory.createDefaultModel();

        // The First Property
        //Property
        Property property1 = model.createProperty("http://example.org/property1");
        boolean property1Inverted = false;

        // The second Property
        //Property
        Property property2 = model.createProperty("http://example.org/property2");
        boolean property2Inverted = false;

        pathElements.add(new Pair<>(property1,property1Inverted));
        pathElements.add(new Pair<>(property2,property2Inverted));

        double score = 0.5;

        QRestrictedPath path = new QRestrictedPath(pathElements,score);

        StringBuilder queryBuilder = new StringBuilder();
        String subjectVariable = "s";
        String objectVariable = "o";
        String intermediateName = "x";

        IPathClauseGenerator generator = new BGPBasedPathClauseGenerator();
        generator.addPath(path, queryBuilder, subjectVariable,objectVariable,intermediateName);

        System.out.println(queryBuilder.toString());
        String expected = "?x1 <http://example.org/property1> ?s . ?o <http://example.org/property2> ?x1 .";
        Assert.assertEquals(expected,queryBuilder.toString());
    }

    @Test
    public void pathLen3NoInvertedShouldReturnExpectedResult(){

        List<Pair<Property, Boolean>> pathElements = new ArrayList<>();

        Model model = ModelFactory.createDefaultModel();

        // The First Property
        //Property
        Property property1 = model.createProperty("http://example.org/property1");
        boolean property1Inverted = true;

        // The second Property
        //Property
        Property property2 = model.createProperty("http://example.org/property2");
        boolean property2Inverted = true;

        // The third Property
        //Property
        Property property3 = model.createProperty("http://example.org/property3");
        boolean property3Inverted = true;

        pathElements.add(new Pair<>(property1,property1Inverted));
        pathElements.add(new Pair<>(property2,property2Inverted));
        pathElements.add(new Pair<>(property3,property3Inverted));

        double score = 0.5;

        QRestrictedPath path = new QRestrictedPath(pathElements,score);

        StringBuilder queryBuilder = new StringBuilder();
        String subjectVariable = "s";
        String objectVariable = "o";
        String intermediateName = "x";

        IPathClauseGenerator generator = new BGPBasedPathClauseGenerator();
        generator.addPath(path, queryBuilder, subjectVariable,objectVariable,intermediateName);

        System.out.println(queryBuilder.toString());
        String expected = "?s <http://example.org/property1> ?x1 . ?x1 <http://example.org/property2> ?x2 . ?x2 <http://example.org/property3> ?o .";
        Assert.assertEquals(expected,queryBuilder.toString());
    }

    @Test
    public void pathLen3FirstPropertyWithInvertedShouldReturnExpectedResult(){

        List<Pair<Property, Boolean>> pathElements = new ArrayList<>();

        Model model = ModelFactory.createDefaultModel();

        // The First Property
        //Property
        Property property1 = model.createProperty("http://example.org/property1");
        boolean property1Inverted = false;

        // The second Property
        //Property
        Property property2 = model.createProperty("http://example.org/property2");
        boolean property2Inverted = true;

        // The third Property
        //Property
        Property property3 = model.createProperty("http://example.org/property3");
        boolean property3Inverted = true;

        pathElements.add(new Pair<>(property1,property1Inverted));
        pathElements.add(new Pair<>(property2,property2Inverted));
        pathElements.add(new Pair<>(property3,property3Inverted));

        double score = 0.5;

        QRestrictedPath path = new QRestrictedPath(pathElements,score);

        StringBuilder queryBuilder = new StringBuilder();
        String subjectVariable = "s";
        String objectVariable = "o";
        String intermediateName = "x";

        IPathClauseGenerator generator = new BGPBasedPathClauseGenerator();
        generator.addPath(path, queryBuilder, subjectVariable,objectVariable,intermediateName);

        System.out.println(queryBuilder.toString());
        String expected = "?x1 <http://example.org/property1> ?s . ?x1 <http://example.org/property2> ?x2 . ?x2 <http://example.org/property3> ?o .";
        Assert.assertEquals(expected,queryBuilder.toString());
    }

    @Test
    public void pathLen3SecondPropertyWithInvertedShouldReturnExpectedResult(){

        List<Pair<Property, Boolean>> pathElements = new ArrayList<>();

        Model model = ModelFactory.createDefaultModel();

        // The First Property
        //Property
        Property property1 = model.createProperty("http://example.org/property1");
        boolean property1Inverted = true;

        // The second Property
        //Property
        Property property2 = model.createProperty("http://example.org/property2");
        boolean property2Inverted = false;

        // The third Property
        //Property
        Property property3 = model.createProperty("http://example.org/property3");
        boolean property3Inverted = true;

        pathElements.add(new Pair<>(property1,property1Inverted));
        pathElements.add(new Pair<>(property2,property2Inverted));
        pathElements.add(new Pair<>(property3,property3Inverted));

        double score = 0.5;

        QRestrictedPath path = new QRestrictedPath(pathElements,score);

        StringBuilder queryBuilder = new StringBuilder();
        String subjectVariable = "s";
        String objectVariable = "o";
        String intermediateName = "x";

        IPathClauseGenerator generator = new BGPBasedPathClauseGenerator();
        generator.addPath(path, queryBuilder, subjectVariable,objectVariable,intermediateName);

        System.out.println(queryBuilder.toString());
        String expected = "?s <http://example.org/property1> ?x1 . ?x2 <http://example.org/property2> ?x1 . ?x2 <http://example.org/property3> ?o .";
        Assert.assertEquals(expected,queryBuilder.toString());
    }

    @Test
    public void pathLen3ThirdPropertyWithInvertedShouldReturnExpectedResult(){

        List<Pair<Property, Boolean>> pathElements = new ArrayList<>();

        Model model = ModelFactory.createDefaultModel();

        // The First Property
        //Property
        Property property1 = model.createProperty("http://example.org/property1");
        boolean property1Inverted = true;

        // The second Property
        //Property
        Property property2 = model.createProperty("http://example.org/property2");
        boolean property2Inverted = true;

        // The third Property
        //Property
        Property property3 = model.createProperty("http://example.org/property3");
        boolean property3Inverted = false;

        pathElements.add(new Pair<>(property1,property1Inverted));
        pathElements.add(new Pair<>(property2,property2Inverted));
        pathElements.add(new Pair<>(property3,property3Inverted));

        double score = 0.5;

        QRestrictedPath path = new QRestrictedPath(pathElements,score);

        StringBuilder queryBuilder = new StringBuilder();
        String subjectVariable = "s";
        String objectVariable = "o";
        String intermediateName = "x";

        IPathClauseGenerator generator = new BGPBasedPathClauseGenerator();
        generator.addPath(path, queryBuilder, subjectVariable,objectVariable,intermediateName);

        System.out.println(queryBuilder.toString());
        String expected = "?s <http://example.org/property1> ?x1 . ?x1 <http://example.org/property2> ?x2 . ?o <http://example.org/property3> ?x2 .";
        Assert.assertEquals(expected,queryBuilder.toString());
    }

    @Test
    public void pathLen3FirstAndSecondPropertiesWithInvertedShouldReturnExpectedResult(){
        List<Pair<Property, Boolean>> pathElements = new ArrayList<>();

        Model model = ModelFactory.createDefaultModel();

        // The First Property
        //Property
        Property property1 = model.createProperty("http://example.org/property1");
        boolean property1Inverted = false;

        // The second Property
        //Property
        Property property2 = model.createProperty("http://example.org/property2");
        boolean property2Inverted = false;

        // The third Property
        //Property
        Property property3 = model.createProperty("http://example.org/property3");
        boolean property3Inverted = true;

        pathElements.add(new Pair<>(property1,property1Inverted));
        pathElements.add(new Pair<>(property2,property2Inverted));
        pathElements.add(new Pair<>(property3,property3Inverted));

        double score = 0.5;

        QRestrictedPath path = new QRestrictedPath(pathElements,score);

        StringBuilder queryBuilder = new StringBuilder();
        String subjectVariable = "s";
        String objectVariable = "o";
        String intermediateName = "x";

        IPathClauseGenerator generator = new BGPBasedPathClauseGenerator();
        generator.addPath(path, queryBuilder, subjectVariable,objectVariable,intermediateName);

        System.out.println(queryBuilder.toString());
        String expected = "?x1 <http://example.org/property1> ?s . ?x2 <http://example.org/property2> ?x1 . ?x2 <http://example.org/property3> ?o .";
        Assert.assertEquals(expected,queryBuilder.toString());
    }

    @Test
    public void pathLen3FirstAndThirdPropertiesWithInvertedShouldReturnExpectedResult(){
        List<Pair<Property, Boolean>> pathElements = new ArrayList<>();

        Model model = ModelFactory.createDefaultModel();

        // The First Property
        //Property
        Property property1 = model.createProperty("http://example.org/property1");
        boolean property1Inverted = false;

        // The second Property
        //Property
        Property property2 = model.createProperty("http://example.org/property2");
        boolean property2Inverted = true;

        // The third Property
        //Property
        Property property3 = model.createProperty("http://example.org/property3");
        boolean property3Inverted = false;

        pathElements.add(new Pair<>(property1,property1Inverted));
        pathElements.add(new Pair<>(property2,property2Inverted));
        pathElements.add(new Pair<>(property3,property3Inverted));

        double score = 0.5;

        QRestrictedPath path = new QRestrictedPath(pathElements,score);

        StringBuilder queryBuilder = new StringBuilder();
        String subjectVariable = "s";
        String objectVariable = "o";
        String intermediateName = "x";

        IPathClauseGenerator generator = new BGPBasedPathClauseGenerator();
        generator.addPath(path, queryBuilder, subjectVariable,objectVariable,intermediateName);

        System.out.println(queryBuilder.toString());
        String expected = "?x1 <http://example.org/property1> ?s . ?x1 <http://example.org/property2> ?x2 . ?o <http://example.org/property3> ?x2 .";
        Assert.assertEquals(expected,queryBuilder.toString());
    }

    @Test
    public void pathLen3SecondAndThirdPropertiesWithInvertedShouldReturnExpectedResult(){
        List<Pair<Property, Boolean>> pathElements = new ArrayList<>();

        Model model = ModelFactory.createDefaultModel();

        // The First Property
        //Property
        Property property1 = model.createProperty("http://example.org/property1");
        boolean property1Inverted = true;

        // The second Property
        //Property
        Property property2 = model.createProperty("http://example.org/property2");
        boolean property2Inverted = false;

        // The third Property
        //Property
        Property property3 = model.createProperty("http://example.org/property3");
        boolean property3Inverted = false;

        pathElements.add(new Pair<>(property1,property1Inverted));
        pathElements.add(new Pair<>(property2,property2Inverted));
        pathElements.add(new Pair<>(property3,property3Inverted));

        double score = 0.5;

        QRestrictedPath path = new QRestrictedPath(pathElements,score);

        StringBuilder queryBuilder = new StringBuilder();
        String subjectVariable = "s";
        String objectVariable = "o";
        String intermediateName = "x";

        IPathClauseGenerator generator = new BGPBasedPathClauseGenerator();
        generator.addPath(path, queryBuilder, subjectVariable,objectVariable,intermediateName);

        System.out.println(queryBuilder.toString());
        String expected = "?s <http://example.org/property1> ?x1 . ?x2 <http://example.org/property2> ?x1 . ?o <http://example.org/property3> ?x2 .";
        Assert.assertEquals(expected,queryBuilder.toString());
    }

    @Test
    public void pathLen3AllWithInvertedShouldReturnExpectedResult(){
        List<Pair<Property, Boolean>> pathElements = new ArrayList<>();

        Model model = ModelFactory.createDefaultModel();

        // The First Property
        //Property
        Property property1 = model.createProperty("http://example.org/property1");
        boolean property1Inverted = false;

        // The second Property
        //Property
        Property property2 = model.createProperty("http://example.org/property2");
        boolean property2Inverted = false;

        // The third Property
        //Property
        Property property3 = model.createProperty("http://example.org/property3");
        boolean property3Inverted = false;

        pathElements.add(new Pair<>(property1,property1Inverted));
        pathElements.add(new Pair<>(property2,property2Inverted));
        pathElements.add(new Pair<>(property3,property3Inverted));

        double score = 0.5;

        QRestrictedPath path = new QRestrictedPath(pathElements,score);

        StringBuilder queryBuilder = new StringBuilder();
        String subjectVariable = "s";
        String objectVariable = "o";
        String intermediateName = "x";

        IPathClauseGenerator generator = new BGPBasedPathClauseGenerator();
        generator.addPath(path, queryBuilder, subjectVariable,objectVariable,intermediateName);

        System.out.println(queryBuilder.toString());
        String expected = "?x1 <http://example.org/property1> ?s . ?x2 <http://example.org/property2> ?x1 . ?o <http://example.org/property3> ?x2 .";
        Assert.assertEquals(expected,queryBuilder.toString());
    }

    @Test
    public void pathLen1NoInvertedNoIntermediateNodeShouldReturnExpectedResult(){

        List<Pair<Property, Boolean>> pathElements = new ArrayList<>();

        Model model = ModelFactory.createDefaultModel();

        // The First Property
        //Property
        Property property1 = model.createProperty("http://example.org/property1");
        boolean property1Inverted = true;

        pathElements.add(new Pair<>(property1,property1Inverted));
        //pathElements.add(new Pair<>(property2,property2Inverted));

        double score = 0.5;

        QRestrictedPath path = new QRestrictedPath(pathElements,score);

        StringBuilder queryBuilder = new StringBuilder();
        String subjectVariable = "s";
        String objectVariable = "o";

        IPathClauseGenerator generator = new BGPBasedPathClauseGenerator();
        generator.addPath(path, queryBuilder, subjectVariable,objectVariable);

        System.out.println(queryBuilder.toString());
        String expected = "?s <http://example.org/property1> ?o .";
        Assert.assertEquals(expected,queryBuilder.toString());
    }

    @Test
    public void pathLen2secondPropertyWithInvertedNoIntermediateNodeShouldReturnExpectedResult(){

        List<Pair<Property, Boolean>> pathElements = new ArrayList<>();

        Model model = ModelFactory.createDefaultModel();

        // The First Property
        //Property
        Property property1 = model.createProperty("http://example.org/property1");
        boolean property1Inverted = true;

        // The second Property
        //Property
        Property property2 = model.createProperty("http://example.org/property2");
        boolean property2Inverted = false;

        pathElements.add(new Pair<>(property1,property1Inverted));
        pathElements.add(new Pair<>(property2,property2Inverted));

        double score = 0.5;

        QRestrictedPath path = new QRestrictedPath(pathElements,score);

        StringBuilder queryBuilder = new StringBuilder();
        String subjectVariable = "s";
        String objectVariable = "o";

        IPathClauseGenerator generator = new BGPBasedPathClauseGenerator();
        generator.addPath(path, queryBuilder, subjectVariable,objectVariable);

        System.out.println(queryBuilder.toString());
        String expected = "?s <http://example.org/property1> ?in1 . ?o <http://example.org/property2> ?in1 .";
        Assert.assertEquals(expected,queryBuilder.toString());
    }

    @Test
    public void pathLen3FirstAndThirdPropertiesWithInvertedNoIntermediateNodeShouldReturnExpectedResult(){
        List<Pair<Property, Boolean>> pathElements = new ArrayList<>();

        Model model = ModelFactory.createDefaultModel();

        // The First Property
        //Property
        Property property1 = model.createProperty("http://example.org/property1");
        boolean property1Inverted = false;

        // The second Property
        //Property
        Property property2 = model.createProperty("http://example.org/property2");
        boolean property2Inverted = true;

        // The third Property
        //Property
        Property property3 = model.createProperty("http://example.org/property3");
        boolean property3Inverted = false;

        pathElements.add(new Pair<>(property1,property1Inverted));
        pathElements.add(new Pair<>(property2,property2Inverted));
        pathElements.add(new Pair<>(property3,property3Inverted));

        double score = 0.5;

        QRestrictedPath path = new QRestrictedPath(pathElements,score);

        StringBuilder queryBuilder = new StringBuilder();
        String subjectVariable = "s";
        String objectVariable = "o";

        IPathClauseGenerator generator = new BGPBasedPathClauseGenerator();
        generator.addPath(path, queryBuilder, subjectVariable,objectVariable);

        System.out.println(queryBuilder.toString());
        String expected = "?in1 <http://example.org/property1> ?s . ?in1 <http://example.org/property2> ?in2 . ?o <http://example.org/property3> ?in2 .";
        Assert.assertEquals(expected,queryBuilder.toString());
    }

    @Test
    public void pathLen2NoInvertedNoLoopsShouldReturnExpectedResult(){

        List<Pair<Property, Boolean>> pathElements = new ArrayList<>();

        Model model = ModelFactory.createDefaultModel();

        // The First Property
        //Property
        Property property1 = model.createProperty("http://example.org/property1");
        boolean property1Inverted = true;

        // The second Property
        //Property
        Property property2 = model.createProperty("http://example.org/property2");
        boolean property2Inverted = true;

        pathElements.add(new Pair<>(property1,property1Inverted));
        pathElements.add(new Pair<>(property2,property2Inverted));

        double score = 0.5;

        QRestrictedPath path = new QRestrictedPath(pathElements,score);

        StringBuilder queryBuilder = new StringBuilder();
        String subjectVariable = "s";
        String objectVariable = "o";
        String intermediateName = "x";

        IPathClauseGenerator generator = new BGPBasedPathClauseGenerator(true);
        generator.addPath(path, queryBuilder, subjectVariable,objectVariable,intermediateName);

        System.out.println(queryBuilder.toString());
        String expected = "?s <http://example.org/property1> ?x1 . ?x1 <http://example.org/property2> ?o . FILTER(?s != ?x1 && ?x1 != ?o)";
        Assert.assertEquals(expected,queryBuilder.toString());
    }

    @Test
    public void pathLen3FirstAndThirdPropertiesWithInvertedNoIntermediateNodeNoLoopsShouldReturnExpectedResult(){
        List<Pair<Property, Boolean>> pathElements = new ArrayList<>();

        Model model = ModelFactory.createDefaultModel();

        // The First Property
        //Property
        Property property1 = model.createProperty("http://example.org/property1");
        boolean property1Inverted = false;

        // The second Property
        //Property
        Property property2 = model.createProperty("http://example.org/property2");
        boolean property2Inverted = true;

        // The third Property
        //Property
        Property property3 = model.createProperty("http://example.org/property3");
        boolean property3Inverted = false;

        pathElements.add(new Pair<>(property1,property1Inverted));
        pathElements.add(new Pair<>(property2,property2Inverted));
        pathElements.add(new Pair<>(property3,property3Inverted));

        double score = 0.5;

        QRestrictedPath path = new QRestrictedPath(pathElements,score);

        StringBuilder queryBuilder = new StringBuilder();
        String subjectVariable = "s";
        String objectVariable = "o";

        IPathClauseGenerator generator = new BGPBasedPathClauseGenerator(true);
        generator.addPath(path, queryBuilder, subjectVariable,objectVariable);

        System.out.println(queryBuilder.toString());
        String expected = "?in1 <http://example.org/property1> ?s . ?in1 <http://example.org/property2> ?in2 . ?o <http://example.org/property3> ?in2 . FILTER(?s != ?in1 && ?s != ?in2 && ?in1 != ?in2 && ?in1 != ?o && ?in2 != ?o)";
        Assert.assertEquals(expected,queryBuilder.toString());
    }
}
