package org.dice_research.fc.paths.paths.map;

import org.apache.commons.math3.util.Pair;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.dice_research.fc.IBidirectionalMapper;
import org.dice_research.fc.paths.map.PropertyMapper;
import org.dice_research.fc.run.Application;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;


/*@RunWith(SpringJUnit4ClassRunner.class)*/
/*@RunWith(SpringRunner.class)
@ContextConfiguration(classes= org.dice_research.fc.config.Config.class , loader= AnnotationConfigContextLoader.class)
@SpringBootTest*/

/*@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = org.dice_research.fc.config.Config.class)*/

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
