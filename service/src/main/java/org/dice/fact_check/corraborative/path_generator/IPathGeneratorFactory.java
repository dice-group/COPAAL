package org.dice.fact_check.corraborative.path_generator;

import org.apache.jena.rdf.model.Statement;
import org.dice.fact_check.corraborative.query.QueryExecutioner;
import org.springframework.stereotype.Component;

@Component
public interface IPathGeneratorFactory {
  public enum PathGeneratorType {
    defaultPathGenerator,
    wikidataPathGenerator
  }

  public IPathGenerator build(
      String queryBuilder,
      Statement input,
      int pathLength,
      QueryExecutioner queryExecutioner,
      PathGeneratorType pathGeneratorType,
      String ontologyURI);
}
