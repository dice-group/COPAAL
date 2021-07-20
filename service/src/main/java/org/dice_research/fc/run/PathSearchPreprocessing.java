package org.dice_research.fc.run;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.delay.core.QueryExecutionFactoryDelay;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.aksw.jena_sparql_api.timeout.QueryExecutionFactoryTimeout;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.PredicateFactory;
import org.dice_research.fc.paths.export.DefaultExporter;
import org.dice_research.fc.paths.export.IPathExporter;
import org.dice_research.fc.paths.ext.IPathExtractor;
import org.dice_research.fc.paths.ext.SamplingPathExtractor;
import org.dice_research.fc.paths.ext.SamplingSPARQLBasedTripleProvider;
import org.dice_research.fc.paths.ext.TripleProvider;
import org.dice_research.fc.paths.imprt.PredicateRetriever;
import org.dice_research.fc.paths.scorer.NPMIBasedScorer;
import org.dice_research.fc.paths.scorer.count.ApproximatingCountRetriever;
import org.dice_research.fc.paths.scorer.count.decorate.CachingCountRetrieverDecorator;
import org.dice_research.fc.paths.scorer.count.max.DefaultMaxCounter;
import org.dice_research.fc.paths.search.SPARQLBasedSOPathSearcher;
import org.dice_research.fc.sparql.filter.EqualsFilter;
import org.dice_research.fc.sparql.filter.NamespaceFilter;
import com.fasterxml.jackson.core.JsonProcessingException;

public class PathSearchPreprocessing {

  protected static final String[] FILTERED_PROPERTIES =
      new String[] {"http://dbpedia.org/ontology/wikiPageExternalLink",
          "http://dbpedia.org/ontology/wikiPageWikiLink"};
  protected static final String FILTERED_NAMESPACE = "http://dbpedia.org/ontology/";

  public static void main(String[] args) throws JsonProcessingException {
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

    // gets the most frequent predicate in the graph
    // TODO: temporary, just to check functionality
    PredicateRetriever predRetr = new PredicateRetriever(qef);
    String property = "http://dbpedia.org/property/birthPlace";
    // String property =  predRetr.getMostFrequentPredicates(1).stream().findAny().get();

    // store paths 
    List<QRestrictedPath> paths = extractor.extract(property);
    Entry<Property, List<QRestrictedPath>> pair = new AbstractMap.SimpleEntry<Property, List<QRestrictedPath>>(ResourceFactory.createProperty(property), paths);
    IPathExporter exporter = new DefaultExporter("paths/");
    String savedIn = exporter.exportPaths(pair);
    System.out.println(savedIn);
  }

}
