package org.dice.FactCheck.Corraborative.Config;

import org.dice.FactCheck.Corraborative.FactChecking;
import org.dice.FactCheck.Corraborative.PathGenerator.DefaultPathGeneratorFactory;
import org.dice.FactCheck.Corraborative.PathGenerator.IPathGeneratorFactory;
import org.dice.FactCheck.Corraborative.PathGenerator.IPathGeneratorFactory.PathGeneratorType;
import org.dice.FactCheck.Corraborative.Query.LocalQueryExecutioner;
import org.dice.FactCheck.Corraborative.Query.QueryExecutioner;
import org.dice.FactCheck.Corraborative.Query.SparqlQueryGenerator;
import org.dice.FactCheck.Corraborative.UIResult.CorroborativeGraph;
import org.dice.FactCheck.Corraborative.UIResult.create.DefaultPathFactory;
import org.dice.FactCheck.Corraborative.UIResult.create.IPathFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 * @author Farshad Afshari farshad.afshari@uni-paderborn.de
 *         <p>
 *         SEP2020 Farshad remove redundant methods and add serviceURLResolve
 */
@Configuration
public class Config {
  
  @Value("${info.dataset:default}")
  private String dataset;

  @Value("${info.service.url.default:}")
  private String serviceURLDefault;

  @Value("${info.service.url.wikidata:}")
  private String serviceURLWikiData;

  @Value("${info.dataset.local:}")
  private String localPath;
  
  @Value("${ontology.uri:}")
  private String ontologyURI;

  @Bean
  public QueryExecutioner getQueryExecutioner() {
    switch (dataset.toLowerCase()) {
      case "wikidata":
        return new QueryExecutioner(serviceURLWikiData);
      case "local":
        return new LocalQueryExecutioner(localPath);
      default:
        return new QueryExecutioner(serviceURLDefault);
    }
  }

  @Bean
  public PathGeneratorType getPathGeneratorType() {
    switch (dataset.toLowerCase()) {
      case "wikidata":
        return PathGeneratorType.defaultPathGenerator;
      default:
        return PathGeneratorType.wikidataPathGenerator;
    }
  }

  @Bean
  public FactChecking getFactChecking(SparqlQueryGenerator sparqlQueryGenerator,
      QueryExecutioner queryExecutioner, CorroborativeGraph corroborativeGraph,
      IPathFactory defaultPathFactory, IPathGeneratorFactory pathGeneratorFactory) {
    return new FactChecking(sparqlQueryGenerator, queryExecutioner, corroborativeGraph,
        defaultPathFactory, pathGeneratorFactory, ontologyURI);
  }

  @Bean
  public SparqlQueryGenerator getSparqlQueryGenerator() {
    return new SparqlQueryGenerator();
  }

  @Bean
  public CorroborativeGraph getCorroborativeGraph() {
    return new CorroborativeGraph();
  }

  @Bean
  public IPathFactory getPathFactory() {
    return new DefaultPathFactory();
  }

  @Bean
  public IPathGeneratorFactory getPathGeneratorFactory() {
    return new DefaultPathGeneratorFactory();
  }

  public String GetOntologyURI() {
    return ontologyURI;
  }
}
