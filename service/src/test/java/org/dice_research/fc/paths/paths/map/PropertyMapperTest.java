package org.dice_research.fc.paths.paths.map;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.dice_research.fc.IBidirectionalMapper;
import org.dice_research.fc.run.Application;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;




@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)

public class PropertyMapperTest {

    @Autowired
    IBidirectionalMapper<Property,String> mapper;

    @Test
    public void mapper_should_map_valid_input_from_Property_to_String() {
        String excpected = "http://dbpedia.org/resource/Tay_Zonday";

        Property pr = ResourceFactory.createProperty(excpected);

        String actual = mapper.map(pr);
        Assert.assertEquals(excpected,actual);
    }

    @Test
    public void mapper_should_map_valid_input_from_String_to_Property() {
        String uri = "http://dbpedia.org/resource/Tay_Zonday";

        Property expected = ResourceFactory.createProperty(uri);

        Property actual = mapper.reverseMap(uri);
        Assert.assertEquals(expected,actual);
    }
}
