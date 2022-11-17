package org.dice_research.fc.run;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
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
import org.dice_research.fc.paths.scorer.NPMIBasedScorer;
import org.dice_research.fc.paths.scorer.count.ApproximatingCountRetriever;
import org.dice_research.fc.paths.scorer.count.decorate.CachingCountRetrieverDecorator;
import org.dice_research.fc.paths.scorer.count.max.DefaultMaxCounter;
import org.dice_research.fc.paths.search.SPARQLBasedSOPathSearcher;
import org.dice_research.fc.sparql.filter.EqualsFilter;
import org.dice_research.fc.sparql.filter.NamespaceFilter;
import org.dice_research.fc.sparql.query.ListBaseQueryValidator;
import org.dice_research.fc.sparql.query.QueryExecutionFactoryCustomHttp;
import org.dice_research.fc.sparql.query.QueryExecutionFactoryCustomHttpTimeout;
import com.fasterxml.jackson.core.JsonProcessingException;

public class PathSearchPreprocessing {

  protected static final String[] FILTERED_PROPERTIES =
      new String[] {"http://dbpedia.org/ontology/wikiPageExternalLink",
          "http://dbpedia.org/ontology/wikiPageWikiLink"};
  protected static final String FILTERED_NAMESPACE = "http://dbpedia.org/ontology/";

  public static void main(String[] args) throws JsonProcessingException {
    // TODO Get the necessary parameters and start the preprocessing method

    QueryExecutionFactory qef =
        new QueryExecutionFactoryCustomHttp("https://synthg-fact.dice-research.org/sparql",false,"json",false,"","");// "https://dbpedia.org/sparql");
    qef = new QueryExecutionFactoryCustomHttpTimeout(qef, 30000);

    long seed = 123;
    int numberOfTriples = 500;

    TripleProvider tripleProvider = new SamplingSPARQLBasedTripleProvider(qef, seed);
    NamespaceFilter filter = new NamespaceFilter("http://dbpedia.org/ontology", false);

    IPathExtractor extractor =
        new SamplingPathExtractor(tripleProvider, numberOfTriples, new PredicateFactory(qef),
            new SPARQLBasedSOPathSearcher(qef, 3,
                Arrays.asList(filter,
                    new EqualsFilter(FILTERED_PROPERTIES))),
            new NPMIBasedScorer(
                new CachingCountRetrieverDecorator(new ApproximatingCountRetriever(qef, new DefaultMaxCounter(qef), new ListBaseQueryValidator(new ArrayList<>())))));

    // gets one of the most frequent predicates in the graph
    String property = "http://dbpedia.org/ontology/birthPlace";
    //PredicateRetriever predRetr = new PredicateRetriever(qef, filter);
    //String property =  predRetr.getMostFrequentPredicates(50).stream().findAny().get();
    
    // store paths 
    List<QRestrictedPath> paths = extractor.extract(property);
    Entry<Property, List<QRestrictedPath>> pair = new AbstractMap.SimpleEntry<Property, List<QRestrictedPath>>(ResourceFactory.createProperty(property), paths);
    IPathExporter exporter = new DefaultExporter("paths/");
    String savedIn = exporter.exportPaths(pair);
    System.out.println(savedIn);
  }

}
