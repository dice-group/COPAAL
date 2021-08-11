package org.dice.FactCheck.Corraborative.PathGenerator;

import org.apache.jena.rdf.model.Statement;
import org.dice.FactCheck.Corraborative.Query.QueryExecutioner;
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
