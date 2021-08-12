package org.dice.fact_check.corraborative.config;

import org.dice.fact_check.corraborative.FactChecking;
import org.dice.fact_check.corraborative.path_generator.DefaultPathGeneratorFactory;
import org.dice.fact_check.corraborative.path_generator.IPathGeneratorFactory;
import org.dice.fact_check.corraborative.path_generator.IPathGeneratorFactory.PathGeneratorType;
import org.dice.fact_check.corraborative.query.LocalQueryExecutioner;
import org.dice.fact_check.corraborative.query.QueryExecutioner;
import org.dice.fact_check.corraborative.query.SparqlQueryGenerator;
import org.dice.fact_check.corraborative.ui_result.CorroborativeGraph;
import org.dice.fact_check.corraborative.ui_result.create.DefaultPathFactory;
import org.dice.fact_check.corraborative.ui_result.create.IPathFactory;
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
}
