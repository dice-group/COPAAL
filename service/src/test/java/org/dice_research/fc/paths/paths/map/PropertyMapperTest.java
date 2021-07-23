package org.dice_research.fc.paths.paths.map;

import org.apache.commons.math3.util.Pair;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.dice_research.fc.IBidirectionalMapper;
import org.dice_research.fc.paths.map.PropertyMapper;
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

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = org.dice_research.fc.config.Config.class)

public class PropertyMapperTest {

    @Autowired
    IBidirectionalMapper<Property,String> mapper;

    Mockito mocker = new Mockito();

    @Test
    public void mapper_should_map_valid_input_from_Property_to_Json() {

        List<Property> tempList = new ArrayList<>();
        Property pr = ResourceFactory.createProperty("http://dbpedia.org/resource/Tay_Zonday");
        tempList.add(pr);

/*        pr = ResourceFactory.createProperty("http://dbpedia.org/ontology/birthPlace");
        tempList.add(pr);

        pr = ResourceFactory.createProperty("http://dbpedia.org/resource/Minneapolis");
        tempList.add(pr);*/

        String str = mapper.map(pr);
        System.out.println(str);
    }

    @Test
    public void mapper_should_map_valid_input_from_Json_to_Property() {
    }
}
