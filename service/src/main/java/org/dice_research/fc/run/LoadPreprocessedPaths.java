package org.dice_research.fc.run;

import java.util.ArrayList;
import java.util.Arrays;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.dice_research.fc.data.FactCheckingResult;
import org.dice_research.fc.paths.PredicateFactory;
import org.dice_research.fc.paths.imprt.EstherPathProcessor;
import org.dice_research.fc.paths.imprt.ImportedFactChecker;
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
import org.dice_research.fc.sum.FixedSummarist;

/**
 * 
 * Loads the pre-processed paths from file and proceeds to score a fact.
 * If the pre-processed paths can't be found in file, it switches to path search.
 *
 */
public class LoadPreprocessedPaths {
  protected static final String[] FILTERED_PROPERTIES =
      new String[] {"http://dbpedia.org/ontology/wikiPageExternalLink",
          "http://dbpedia.org/ontology/wikiPageWikiLink"};
  protected static final String FILTERED_NAMESPACE = "http://dbpedia.org/ontology/";

  public static void main(String[] args) {
    QueryExecutionFactory qef =
        new QueryExecutionFactoryCustomHttp("https://synthg-fact.dice-research.org/sparql",false,"json",false,"","");// "https://dbpedia.org/sparql");
    qef = new QueryExecutionFactoryCustomHttpTimeout(qef, 30000);

    ImportedFactChecker checker = new ImportedFactChecker(new PredicateFactory(qef),
        new SPARQLBasedSOPathSearcher(qef, 3,
            Arrays.asList(new NamespaceFilter("http://dbpedia.org/ontology", false),
                new EqualsFilter(FILTERED_PROPERTIES))),
        new NPMIBasedScorer(new CachingCountRetrieverDecorator(
            new ApproximatingCountRetriever(qef, new DefaultMaxCounter(qef), new ListBaseQueryValidator(new ArrayList<>())))),
        new FixedSummarist(), new EstherPathProcessor("./paths/", qef), qef, false,0.0, null);

    FactCheckingResult result =
        checker.check(ResourceFactory.createResource("http://dbpedia.org/resource/Tay_Zonday"),
            ResourceFactory.createProperty("http://dbpedia.org/ontology/birthPlace"),
            ResourceFactory.createResource("http://dbpedia.org/resource/Minneapolis"));
    System.out.print("Result: ");
    System.out.println(result.getVeracityValue());
  }
}
