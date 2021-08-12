package org.dice.fact_check.corraborative.ui_result.create;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.StatementImpl;
import org.dice.fact_check.corraborative.Result;
import org.dice.fact_check.corraborative.ui_result.CorroborativeTriple;
import org.dice.fact_check.corraborative.ui_result.Path;
import org.springframework.stereotype.Component;

/**
 * A simple path factory that can be used as default without relying on external services or
 * resources.
 *
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 */
@Component
public class DefaultPathBuilder implements IPathBuilder {

  @Override
  public Path createPath(RDFNode subject, RDFNode object, Result result) {
    List<Statement> statements =
        generateVerbalizingTriples(
            result.getPathBuilder(),
            result.path,
            result.intermediateNodes,
            result.pathLength,
            subject,
            object);
    List<CorroborativeTriple> triples =
        statements
            .stream()
            .map(
                s ->
                    new CorroborativeTriple(
                        s.getSubject().toString(),
                        s.getPredicate().toString(),
                        s.getObject().toString()))
            .collect(Collectors.toList());
    return new Path(triples, result.score, "Not available");
  }

  protected List<Statement> generateVerbalizingTriples(
      String builder,
      String path,
      String intermediateNodes,
      int pathLength,
      RDFNode subject,
      RDFNode object) {
    List<Statement> statementList = new ArrayList<Statement>();
    String[] paths = path.split(";");
    int prop = 1;
    int res = 1;
    String pathFormat = builder;
    for (int i = 0; i < paths.length; i++) {

      String property = "?p" + (prop);
      pathFormat = pathFormat.replace(property, paths[i]);
      prop++;
    }
    if (pathLength > 1) {
      String[] intermediateResources = intermediateNodes.split(";");
      for (int i = 0; i < intermediateResources.length; i++) {

        String resource = "?x" + (res);
        pathFormat = pathFormat.replace(resource, intermediateResources[i]);
        res++;
      }
    }

    pathFormat = pathFormat.replace("?s", subject.toString());
    pathFormat = pathFormat.replace("?o", object.toString());

    String[] triples = pathFormat.split(";");
    for (int i = 0; i < triples.length; i++) {
      Resource resourceSubject = ResourceFactory.createResource(triples[i].split(" ")[0].trim());
      Property property = ResourceFactory.createProperty(triples[i].split(" ")[1].trim());
      Resource resourceObject = ResourceFactory.createResource(triples[i].split(" ")[2].trim());
      statementList.add(new StatementImpl(resourceSubject, property, resourceObject));
    }
    return statementList;
  }
}