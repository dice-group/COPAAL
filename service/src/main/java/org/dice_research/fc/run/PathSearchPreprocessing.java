package org.dice_research.fc.run;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.delay.core.QueryExecutionFactoryDelay;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.aksw.jena_sparql_api.timeout.QueryExecutionFactoryTimeout;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.PredicateFactory;
import org.dice_research.fc.paths.ext.IPathExtractor;
import org.dice_research.fc.paths.ext.SamplingPathExtractor;
import org.dice_research.fc.paths.ext.SamplingSPARQLBasedTripleProvider;
import org.dice_research.fc.paths.ext.TripleProvider;
import org.dice_research.fc.paths.scorer.NPMIBasedScorer;
import org.dice_research.fc.paths.scorer.count.ApproximatingCountRetriever;
import org.dice_research.fc.paths.scorer.count.decorate.CachingCountRetrieverDecorator;
import org.dice_research.fc.paths.scorer.count.max.DefaultMaxCounter;
import org.dice_research.fc.paths.search.SPARQLBasedSOPathSearcher;
import org.dice_research.fc.sparql.filter.EqualsFilter;
import org.dice_research.fc.sparql.filter.NamespaceFilter;

public class PathSearchPreprocessing {

  protected static final String[] FILTERED_PROPERTIES =
      new String[] {"http://dbpedia.org/ontology/wikiPageExternalLink",
          "http://dbpedia.org/ontology/wikiPageWikiLink"};
  protected static final String FILTERED_NAMESPACE = "http://dbpedia.org/ontology/";

  public static void main(String[] args) {
    // TODO Get the necessary parameters and start the preprocessing method

    QueryExecutionFactory qef =
        new QueryExecutionFactoryHttp("https://synthg-fact.dice-research.org/sparql");// "https://dbpedia.org/sparql");
    qef = new QueryExecutionFactoryDelay(qef, 200);
    // qef = new QueryExecutionFactoryPaginated(qef, 10000);
    qef = new QueryExecutionFactoryTimeout(qef, 30, TimeUnit.SECONDS, 30, TimeUnit.SECONDS);

    long seed = 123;
    int numberOfTriples = 500;

    TripleProvider tripleProvider = new SamplingSPARQLBasedTripleProvider(qef, seed);

    IPathExtractor extractor =
        new SamplingPathExtractor(tripleProvider, numberOfTriples, new PredicateFactory(qef),
            new SPARQLBasedSOPathSearcher(qef, 3,
                Arrays.asList(new NamespaceFilter("http://dbpedia.org/ontology", false),
                    new EqualsFilter(FILTERED_PROPERTIES))),
            new NPMIBasedScorer(
                new CachingCountRetrieverDecorator(new ApproximatingCountRetriever(qef, new DefaultMaxCounter(qef)))));

    // TODO get a list of property URIs from somewhere
    String propertyURI = null;

    List<QRestrictedPath> paths = extractor.extract(propertyURI);

    // TODO store the paths somewhere
  }

}
