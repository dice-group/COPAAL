package org.dice_research.fc.paths.map;

import org.apache.commons.math3.util.Pair;
import org.apache.jena.rdf.model.Property;
import org.dice_research.fc.IBidirectionalMapper;
import org.dice_research.fc.IMapper;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.model.Path;
import org.dice_research.fc.paths.model.PathElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a mapper from Path to QRestrictedPath
 * for property we just consider URI if need more data it should add on the mapper @link PropertyMapper
 *
 * @author Farshad Afshari
 *
 */

@Component
public class PathMapper implements IMapper<Path, QRestrictedPath> {
    @Autowired
    IBidirectionalMapper<Property,String> mapper;

    public QRestrictedPath map(Path path) {
        double score = path.getScore();
        List<Pair<Property, Boolean>> pe = new ArrayList<>();

        for (PathElement pathElement:path.getPathElements()) {
            Pair<Property, Boolean> tmpPair = new Pair<Property, Boolean>(mapper.reverseMap(pathElement.getProperty()),pathElement.isInverted());
            pe.add(tmpPair);
        }

        QRestrictedPath retVal = new QRestrictedPath(pe,score);
        return retVal;
    }
}
