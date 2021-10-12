package org.dice_research.fc.paths.data;

import org.apache.commons.math3.util.Pair;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.rdf.model.impl.StatementImpl;
import org.dice_research.fc.data.FactCheckingResult;
import org.dice_research.fc.data.IPieceOfEvidence;
import org.dice_research.fc.data.QRestrictedPath;
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
public class FactCheckingResultTest {
    @Test
    public void rdfStarVersionShouldWorkFine(){

        List<IPieceOfEvidence> piecesOfEvidences = new ArrayList<>();

        List<Pair< Property, Boolean>> ll1 = new ArrayList<>();
        ll1.add(new Pair< Property, Boolean>(new PropertyImpl("test"),false));
        ll1.add(new Pair< Property, Boolean>(new PropertyImpl("test"),true));
        QRestrictedPath qrp1 = new QRestrictedPath(ll1);
        qrp1.setScore(0.6);
        qrp1.setVerbalizedOutput("verbalize1");

        List<Pair< Property, Boolean>> ll2 = new ArrayList<>();
        ll2.add(new Pair< Property, Boolean>(new PropertyImpl("testR"),false));
        ll2.add(new Pair< Property, Boolean>(new PropertyImpl("testR"),true));
        QRestrictedPath qrp2 = new QRestrictedPath(ll1);
        qrp2.setScore(0.1);
        qrp2.setVerbalizedOutput("verbalizeR");

        piecesOfEvidences.add(qrp1);
        piecesOfEvidences.add(qrp2);


        Resource resourceSubject = ResourceFactory.createResource("http://frockg.upb.de/resource/test1");
        Property property = ResourceFactory.createProperty("http://frockg.upb.de/ontology/isTest");
        Resource resourceObject = ResourceFactory.createResource("http://frockg.upb.de/resource/test2");

        Statement fact = new StatementImpl(resourceSubject,property,resourceObject);

        FactCheckingResult result = new FactCheckingResult(0.5,piecesOfEvidences,fact);

        String expected = "<< http://frockg.upb.de/resource/test1 http://frockg.upb.de/ontology/isTest http://frockg.upb.de/resource/test2 >> \n" +
                ":veracityValue 0.5;\n" +
                "\n" +
                ":copaal:score 0.6\", \"0.1;\n" +
                "\n" +
                ":evidence \"^<test>/<test>\", \"^<test>/<test>\";\n" +
                ":explanation \"verbalize1\", \"verbalizeR\".";

        Assert.assertEquals(expected, result.getRdfStarVersion());
    }

    @Test
    public void rdfStarVersionWhenTheExplanationsAreUniqueShouldWorkFine(){

        List<IPieceOfEvidence> piecesOfEvidences = new ArrayList<>();

        List<Pair< Property, Boolean>> ll1 = new ArrayList<>();
        ll1.add(new Pair< Property, Boolean>(new PropertyImpl("test"),false));
        ll1.add(new Pair< Property, Boolean>(new PropertyImpl("test"),true));
        QRestrictedPath qrp1 = new QRestrictedPath(ll1);
        qrp1.setScore(0.6);
        qrp1.setVerbalizedOutput("11");

        List<Pair< Property, Boolean>> ll2 = new ArrayList<>();
        ll2.add(new Pair< Property, Boolean>(new PropertyImpl("testR"),false));
        ll2.add(new Pair< Property, Boolean>(new PropertyImpl("testR"),true));
        QRestrictedPath qrp2 = new QRestrictedPath(ll1);
        qrp2.setScore(0.1);
        qrp2.setVerbalizedOutput("11");

        piecesOfEvidences.add(qrp1);
        piecesOfEvidences.add(qrp2);


        Resource resourceSubject = ResourceFactory.createResource("http://frockg.upb.de/resource/test1");
        Property property = ResourceFactory.createProperty("http://frockg.upb.de/ontology/isTest");
        Resource resourceObject = ResourceFactory.createResource("http://frockg.upb.de/resource/test2");

        Statement fact = new StatementImpl(resourceSubject,property,resourceObject);

        FactCheckingResult result = new FactCheckingResult(0.5,piecesOfEvidences,fact);

        String expected = "<< http://frockg.upb.de/resource/test1 http://frockg.upb.de/ontology/isTest http://frockg.upb.de/resource/test2 >> \n" +
                ":veracityValue 0.5;\n" +
                "\n" +
                ":copaal:score 0.6\", \"0.1;\n" +
                "\n" +
                ":evidence \"^<test>/<test>\", \"^<test>/<test>\";\n" +
                ":explanation \"11\".";

        Assert.assertEquals(expected, result.getRdfStarVersion());
    }

    @Test
    public void rdfStarARVersionShouldWorkFine(){

        List<IPieceOfEvidence> piecesOfEvidences = new ArrayList<>();

        List<Pair< Property, Boolean>> ll1 = new ArrayList<>();
        ll1.add(new Pair< Property, Boolean>(new PropertyImpl("test"),false));
        ll1.add(new Pair< Property, Boolean>(new PropertyImpl("test"),true));
        QRestrictedPath qrp1 = new QRestrictedPath(ll1);
        qrp1.setScore(0.6);
        qrp1.setVerbalizedOutput("verbalize1");

        List<Pair< Property, Boolean>> ll2 = new ArrayList<>();
        ll2.add(new Pair< Property, Boolean>(new PropertyImpl("testR"),false));
        ll2.add(new Pair< Property, Boolean>(new PropertyImpl("testR"),true));
        QRestrictedPath qrp2 = new QRestrictedPath(ll1);
        qrp2.setScore(0.1);
        qrp2.setVerbalizedOutput("verbalizeR");

        piecesOfEvidences.add(qrp1);
        piecesOfEvidences.add(qrp2);


        Resource resourceSubject = ResourceFactory.createResource("http://frockg.upb.de/resource/test1");
        Property property = ResourceFactory.createProperty("http://frockg.upb.de/ontology/isTest");
        Resource resourceObject = ResourceFactory.createResource("http://frockg.upb.de/resource/test2");

        Statement fact = new StatementImpl(resourceSubject,property,resourceObject);

        FactCheckingResult result = new FactCheckingResult(0.5,piecesOfEvidences,fact);

        String expected = "<< http://frockg.upb.de/resource/test1 http://frockg.upb.de/ontology/isTest http://frockg.upb.de/resource/test2 >> \n" +
                ":hasEvidence _:1,. _:2.\n" +
                " _:1 :copaal:score 0.6;\n" +
                " :evidence \"^<test>/<test>\";\n" +
                " :explanation \"verbalize1\".\n" +
                " _:2 :copaal:score 0.1;\n" +
                " :evidence \"^<test>/<test>\";\n" +
                " :explanation \"verbalizeR\".\n";

        Assert.assertEquals(expected, result.getRdfStarVersionAR());
    }
}
