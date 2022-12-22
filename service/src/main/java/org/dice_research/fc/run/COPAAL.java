package org.dice_research.fc.run;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.delay.core.QueryExecutionFactoryDelay;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.aksw.jena_sparql_api.timeout.QueryExecutionFactoryTimeout;
import org.apache.jena.rdf.model.ResourceFactory;
import org.dice_research.fc.data.FactCheckingResult;
import org.dice_research.fc.paths.PathBasedFactChecker;
import org.dice_research.fc.paths.VirtualTypePredicateFactory;
import org.dice_research.fc.paths.scorer.NPMIBasedScorer;
import org.dice_research.fc.paths.scorer.count.PairCountRetriever;
import org.dice_research.fc.paths.scorer.count.decorate.CachingCountRetrieverDecorator;
import org.dice_research.fc.paths.scorer.count.max.DefaultMaxCounter;
import org.dice_research.fc.paths.search.SPARQLBasedSOPathSearcher;
import org.dice_research.fc.sparql.filter.NamespaceFilter;
import org.dice_research.fc.sparql.query.ListBaseQueryValidator;
import org.dice_research.fc.sum.FixedSummarist;

/**
 * This class implements COPAAL as it has been defined in the paper
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class COPAAL {

  //protected static final String[] FILTERED_PROPERTIES =
  //    new String[] {"http://dbpedia.org/ontology/wikiPageExternalLink",
  //        "http://dbpedia.org/ontology/wikiPageWikiLink"};
  protected static final String FILTERED_NAMESPACES[] = new String[] {
      "http://linkedlifedata.com/resource/drugcentral/", "http://rdf.frockg.eu/frockg/ontology/"};

  public static void main(String[] args) {
    QueryExecutionFactory qef =
        new QueryExecutionFactoryHttp("https://frockg.ontotext.com/repositories/data_v3");// "https://dbpedia.org/sparql");//https://synthg-fact.dice-research.org/sparql
    qef = new QueryExecutionFactoryDelay(qef, 200);
    // qef = new QueryExecutionFactoryPaginated(qef, 10000);
    qef = new QueryExecutionFactoryTimeout(qef, 30, TimeUnit.SECONDS, 30, TimeUnit.SECONDS);

    PathBasedFactChecker checker =
        new PathBasedFactChecker(new VirtualTypePredicateFactory(),
            new SPARQLBasedSOPathSearcher(qef, 3, Arrays.asList(
                new NamespaceFilter(new String[] {"http://linkedlifedata.com/resource/drugcentral/",
                    "http://rdf.frockg.eu/frockg/ontology/"}, false)/*,
                new EqualsFilter(FILTERED_PROPERTIES)*/)),
            new NPMIBasedScorer(new CachingCountRetrieverDecorator(
                new PairCountRetriever(qef, new DefaultMaxCounter(qef), new ListBaseQueryValidator(new ArrayList<>())))),
            new FixedSummarist(), 0.5,null);

    FactCheckingResult result = checker.check(
        ResourceFactory
            .createResource("http://linkedlifedata.com/resource/drugcentral/structure/95"),
        ResourceFactory.createProperty("http://rdf.frockg.eu/frockg/ontology/hasAdverseReaction"),
        ResourceFactory.createResource("http://rdf.frockg.eu/resource/umls/id/C0000737"));
    System.out.print("Result: ");
    System.out.println(result.getVeracityValue());
  }
}
