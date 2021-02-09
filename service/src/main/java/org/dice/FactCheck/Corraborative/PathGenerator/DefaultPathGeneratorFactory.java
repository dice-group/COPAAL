package org.dice.FactCheck.Corraborative.PathGenerator;

import org.apache.jena.rdf.model.Statement;
import org.dice.FactCheck.Corraborative.Query.QueryExecutioner;
import org.springframework.stereotype.Component;

@Component
public class DefaultPathGeneratorFactory implements IPathGeneratorFactory {

  public IPathGenerator build(
      String queryBuilder,
      Statement input,
      int pathLength,
      QueryExecutioner queryExecutioner,
      PathGeneratorType pathGeneratorType) {
    switch (pathGeneratorType) {
      case defaultPathGenerator:
        return new DefaultPathGenerator(queryBuilder, input, pathLength, queryExecutioner);
      case wikidataPathGenerator:
        return new WikiDataPathGenerator(queryBuilder, input, pathLength, queryExecutioner);
    }
    return null;
  }
}
