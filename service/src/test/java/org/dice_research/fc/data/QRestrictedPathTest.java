package org.dice_research.fc.data;

import org.apache.commons.math3.util.Pair;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.dice_research.fc.run.Application;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class QRestrictedPathTest {
    @Test
    public void equalShouldWorkFineForTwoSameInstances(){
        List<Pair<Property, Boolean>> pathElements1 = new ArrayList<>();
        pathElements1.add(new Pair<>(ResourceFactory.createProperty("a"),false));
        pathElements1.add(new Pair<>(ResourceFactory.createProperty("b"),true));
        pathElements1.add(new Pair<>(ResourceFactory.createProperty("c"),false));
        pathElements1.add(new Pair<>(ResourceFactory.createProperty("d"),true));
        double score1 = 1;
        QRestrictedPath qp1 = new QRestrictedPath(pathElements1,score1);

        List<Pair<Property, Boolean>> pathElements2 = new ArrayList<>();
        pathElements2.add(new Pair<>(ResourceFactory.createProperty("a"),false));
        pathElements2.add(new Pair<>(ResourceFactory.createProperty("b"),true));
        pathElements2.add(new Pair<>(ResourceFactory.createProperty("c"),false));
        pathElements2.add(new Pair<>(ResourceFactory.createProperty("d"),true));
        double score2 = 1;
        QRestrictedPath qp2 = new QRestrictedPath(pathElements2,score2);

        Assert.assertTrue(qp1.equals(qp2));
    }

    @Test
    public void equalShouldWorkFineForTwoDifferentPathElementsInstances(){
        List<Pair<Property, Boolean>> pathElements1 = new ArrayList<>();
        pathElements1.add(new Pair<>(ResourceFactory.createProperty("a"),false));
        pathElements1.add(new Pair<>(ResourceFactory.createProperty("b"),false));
        pathElements1.add(new Pair<>(ResourceFactory.createProperty("c"),false));
        pathElements1.add(new Pair<>(ResourceFactory.createProperty("d"),false));
        double score1 = 1;
        QRestrictedPath qp1 = new QRestrictedPath(pathElements1,score1);

        List<Pair<Property, Boolean>> pathElements2 = new ArrayList<>();
        pathElements2.add(new Pair<>(ResourceFactory.createProperty("a"),true));
        pathElements2.add(new Pair<>(ResourceFactory.createProperty("b"),true));
        pathElements2.add(new Pair<>(ResourceFactory.createProperty("c"),true));
        pathElements2.add(new Pair<>(ResourceFactory.createProperty("d"),true));
        double score2 = 1;
        QRestrictedPath qp2 = new QRestrictedPath(pathElements2,score2);

        Assert.assertFalse(qp1.equals(qp2));
    }

    @Test
    public void equalShouldWorkFineForTwoDifferentScoreInstances(){
        List<Pair<Property, Boolean>> pathElements1 = new ArrayList<>();
        pathElements1.add(new Pair<>(ResourceFactory.createProperty("a"),false));
        pathElements1.add(new Pair<>(ResourceFactory.createProperty("b"),false));
        pathElements1.add(new Pair<>(ResourceFactory.createProperty("c"),false));
        pathElements1.add(new Pair<>(ResourceFactory.createProperty("d"),false));
        double score1 = 0;
        QRestrictedPath qp1 = new QRestrictedPath(pathElements1,score1);

        List<Pair<Property, Boolean>> pathElements2 = new ArrayList<>();
        pathElements2.add(new Pair<>(ResourceFactory.createProperty("a"),false));
        pathElements2.add(new Pair<>(ResourceFactory.createProperty("b"),false));
        pathElements2.add(new Pair<>(ResourceFactory.createProperty("c"),false));
        pathElements2.add(new Pair<>(ResourceFactory.createProperty("d"),false));
        double score2 = 1;
        QRestrictedPath qp2 = new QRestrictedPath(pathElements2,score2);

        Assert.assertFalse(qp1.equals(qp2));
    }
}
