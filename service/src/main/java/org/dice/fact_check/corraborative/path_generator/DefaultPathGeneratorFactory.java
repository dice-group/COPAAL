package org.dice.fact_check.corraborative.path_generator;

import org.apache.jena.rdf.model.Statement;
import org.dice.fact_check.corraborative.query.QueryExecutioner;
import org.springframework.stereotype.Component;

@Component
public class DefaultPathGeneratorFactory implements IPathGeneratorFactory {

  public IPathGenerator build(String queryBuilder, Statement input, int pathLength,
      QueryExecutioner queryExecutioner, PathGeneratorType pathGeneratorType,
      String ontologyURI) {
    switch (pathGeneratorType) {
      default:
        return new DefaultPathGenerator(queryBuilder, input, pathLength, queryExecutioner, ontologyURI);
      case wikidataPathGenerator:
        return new WikiDataPathGenerator(queryBuilder, input, pathLength, queryExecutioner);
    }
  }
}
