package org.dice_research.fc.run;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.delay.core.QueryExecutionFactoryDelay;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.aksw.jena_sparql_api.timeout.QueryExecutionFactoryTimeout;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.log4j.PropertyConfigurator;
import org.dice.FactCheck.Corraborative.sum.CubicMeanSummarist;
import org.dice_research.fc.paths.PathBasedFactChecker;
import org.dice_research.fc.paths.PredicateFactory;
import org.dice_research.fc.paths.scorer.NPMIBasedScorer;
import org.dice_research.fc.paths.scorer.count.ApproximatingCountRetriever;
import org.dice_research.fc.paths.scorer.count.decorate.CachingCountRetrieverDecorator;
import org.dice_research.fc.paths.search.SPARQLBasedSOPathSearcher;
import org.dice_research.fc.sparql.filter.EqualsFilter;
import org.dice_research.fc.sparql.filter.NamespaceFilter;

/**
 * This class implements COPAAL as it has been defined in the paper
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class COPAAL {

  protected static final String[] FILTERED_PROPERTIES =
      new String[] {"http://dbpedia.org/ontology/wikiPageExternalLink", "http://dbpedia.org/ontology/wikiPageWikiLink"};
  protected static final String FILTERED_NAMESPACE = "http://dbpedia.org/ontology/";

  public static void main(String[] args) {
    PropertyConfigurator.configure(COPAAL.class.getClassLoader().getResource("log4j.properties"));
    
    QueryExecutionFactory qef = new QueryExecutionFactoryHttp("https://synthg-fact.dice-research.org/sparql");//"https://dbpedia.org/sparql");
    qef = new QueryExecutionFactoryDelay(qef, 2000);
    //qef = new QueryExecutionFactoryPaginated(qef, 10000);
    qef = new QueryExecutionFactoryTimeout(qef, 30, TimeUnit.SECONDS, 30, TimeUnit.SECONDS);

    PathBasedFactChecker checker = new PathBasedFactChecker(new PredicateFactory(qef),
        new SPARQLBasedSOPathSearcher(qef, 3,
            Arrays.asList(new NamespaceFilter("http://dbpedia.org/ontology", false),
                new EqualsFilter(FILTERED_PROPERTIES))),
        new NPMIBasedScorer(new CachingCountRetrieverDecorator(new ApproximatingCountRetriever(qef))), new CubicMeanSummarist());

    System.out.println(checker.check(ResourceFactory.createResource("http://dbpedia.org/resource/Tay_Zonday"), ResourceFactory.createProperty("http://dbpedia.org/ontology/birthPlace"),
        ResourceFactory.createResource("http://dbpedia.org/resource/Minneapolis")));
  }
}
