package org.dice_research.fc.paths.paths.map;


import org.apache.commons.math3.util.Pair;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.dice_research.fc.IMapper;
import org.dice_research.fc.paths.model.PathElement;
import org.dice_research.fc.run.Application;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class PropertyElementMapperTest {
    @Autowired
    IMapper<Pair<Property, Boolean>, PathElement> mapper;

    @Test
    public void mapperShouldMapValidInputFromPairToPathElement(){
        String excpectedURI = "http://dbpedia.org/resource/Tay_Zonday";

        Property property = ResourceFactory.createProperty(excpectedURI);

        Pair<Property, Boolean> pair = new Pair<>(property,false);

        PathElement actual = mapper.map(pair);

        Assert.assertEquals(excpectedURI,actual.getProperty());
        Assert.assertEquals(false,actual.isInverted());

        // for True inverted
        Pair<Property, Boolean> pair2 = new Pair<>(property,true);

        PathElement actual2 = mapper.map(pair2);

        Assert.assertEquals(excpectedURI,actual2.getProperty());
        Assert.assertEquals(true,actual2.isInverted());

    }
}
