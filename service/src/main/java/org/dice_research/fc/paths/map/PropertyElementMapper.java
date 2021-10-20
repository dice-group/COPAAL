package org.dice_research.fc.paths.map;

import org.apache.commons.math3.util.Pair;
import org.apache.jena.rdf.model.Property;
import org.dice_research.fc.IMapper;
import org.dice_research.fc.paths.model.PathElement;
import org.springframework.stereotype.Component;

/**
 * This class is a mapper from Pair<Property, Boolean> to PathElement
 * for property we just consider URI if need more data it should add
 *
 * @author Farshad Afshari
 *
 */

@Component
public class PropertyElementMapper implements IMapper<Pair<Property, Boolean>, PathElement> {
    @Override
    public PathElement map(Pair<Property, Boolean> from) {
        PathElement pathElement = new PathElement(from.getSecond().booleanValue(),from.getFirst().getURI());
        return pathElement;
    }
}
