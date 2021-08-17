package org.dice_research.fc.paths.paths.map;

import org.dice_research.fc.IMapper;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.model.Path;
import org.dice_research.fc.paths.model.PathElement;
import org.dice_research.fc.run.Application;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)

public class PathMapperTest {
    @Autowired
    IMapper<Path, QRestrictedPath> mapper;

    @Test
    public void mapper_should_map_valid_input_from_Path_to_QRestrictedPath(){
        Path path = new Path( "subject","predicate","object","factPreprocessor","counterRetriever","pathSearcher","pathScorer", 0.51);
        List<PathElement> pathElements = new ArrayList<>();

        pathElements.add(new PathElement(false,"http://dbpedia.org/ontology/birthPlace"));
        pathElements.add(new PathElement(true,"http://dbpedia.org/ontology/deathPlace"));

        path.setPathElements(pathElements);

        QRestrictedPath actual = mapper.map(path);

        Assert.assertEquals(2,actual.getPathElements().size());

        Assert.assertEquals("http://dbpedia.org/ontology/birthPlace",actual.getPathElements().get(0).getFirst().getURI());
        Assert.assertEquals(false,actual.getPathElements().get(0).getSecond());

        Assert.assertEquals("http://dbpedia.org/ontology/deathPlace",actual.getPathElements().get(1).getFirst().getURI());
        Assert.assertEquals(true,actual.getPathElements().get(1).getSecond());

        Assert.assertEquals(0.51,actual.getScore(),0.0001);


    }
}
