package org.dice_research.fc.paths.map;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.dice_research.fc.IBidirectionalMapper;
import org.springframework.stereotype.Component;


/**
 * This class is a bidirectional mapper between  Property and String
 * for property we just consider URI if need more data it should add
 *
 * @author Farshad Afshari
 *
 */
@Component
public class PropertyMapper implements IBidirectionalMapper<Property,String> {
    @Override
    public Property reverseMap(String from) {
        return ResourceFactory.createProperty(from);
    }

    @Override
    public String map(Property from) {
        return from.getURI();
    }
}
