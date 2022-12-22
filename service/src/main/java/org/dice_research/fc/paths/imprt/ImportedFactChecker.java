package org.dice_research.fc.paths.imprt;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.dice_research.fc.data.FactCheckingResult;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.FactPreprocessor;
import org.dice_research.fc.paths.IPathScorer;
import org.dice_research.fc.paths.IPathSearcher;
import org.dice_research.fc.paths.PathBasedFactChecker;
import org.dice_research.fc.sum.ScoreSummarist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Similar to {@link PathBasedFactChecker}, but loads the search paths instead and might forego the
 * scoring of individual paths (if these are present in file).
 * 
 * @author Alexandra Silva
 *
 */
@Component
public class ImportedFactChecker extends PathBasedFactChecker {

    private boolean printTheExampleOfEachFoundedPath;
  private static final Logger LOGGER = LoggerFactory.getLogger(ImportedFactChecker.class);

  /**
   * The imported facts processor
   */
  protected MetaPathsProcessor metaPreprocessor;
    protected QueryExecutionFactory qef;

  @Autowired
  public ImportedFactChecker(FactPreprocessor factPreprocessor, IPathSearcher pathSearcher,
                             IPathScorer pathScorer, ScoreSummarist summarist, MetaPathsProcessor metaPreprocessor, QueryExecutionFactory qef, boolean printTheExampleOfEachFoundedPath, double pathFilterThreshold, String[] propertyFilter) {
    super(factPreprocessor, pathSearcher, pathScorer, summarist,pathFilterThreshold, propertyFilter);
    this.metaPreprocessor = metaPreprocessor;
    this.qef = qef;
    this.printTheExampleOfEachFoundedPath = printTheExampleOfEachFoundedPath;
  }

  @Override
  public FactCheckingResult check(Resource subject, Property predicate, Resource object) {
      LOGGER.info(" -------------  START OF FACT CHECKING @ ImportedFactChecker-------------");
      LOGGER.info(" -------------  Start to preprocess the data  -------------");
      LOGGER.info("subject is " + subject);
      LOGGER.info("predicate is " +  predicate);
      LOGGER.info("object is "+object);
      Statement fact = ResourceFactory.createStatement(subject, predicate, object);
      LOGGER.info("fact is"+fact);
      Predicate preparedPredicate = factPreprocessor.generatePredicate(fact);
      LOGGER.info("preparedPredicate"+preparedPredicate);
      LOGGER.info(" -------------  Preprocess the data Done   -------------");

      // pre-process paths in file
      Collection<QRestrictedPath> paths = metaPreprocessor.processMetaPaths(fact);

      // search for paths if preprocessed paths can't be found
      if (paths == null) {
        LOGGER.info("Couldn't find the files for paths of {}. Switching to path search.",
                predicate.getURI());
        LOGGER.info(" -------------  Start to get a list of potential paths  -------------");
        paths = pathSearcher.search(subject, preparedPredicate, object);
        LOGGER.info(" -------------  Get a list of potential paths Done  -------------");
      }

      // return default score if no paths are found
      if (paths.isEmpty()) {
        LOGGER.warn("paths for {} in a file is empty.",
                predicate.getURI());
        return new FactCheckingResult(pathsNotFoundResult, paths, fact);
      }

      // calculate scores and verbalize if needed only
      LOGGER.info(" -------------  Start to filter and Score  -------------");
      LOGGER.info("number of paths before filtering is {}",paths.size());


          for (QRestrictedPath p : paths) {
              LOGGER.info(p.toStringWithTag());
              if(printTheExampleOfEachFoundedPath) {
                  String qfge = p.queryForGetAnExample();
                  try (QueryExecution qe = qef.createQueryExecution(qfge)) {
                      ResultSet rs = qe.execSelect();
                      if (rs != null) {
                          if (rs.hasNext()) {
                              LOGGER.info(rs.next().toString());
                          }
                      } else {
                          LOGGER.error("rs is null for this query :" + qfge);
                      }
                  } catch (Exception ex) {
                      LOGGER.error(ex.getMessage());
                      ex.printStackTrace();
                  }
              }
              LOGGER.info("-_-_-_-_-_-_-");
          }

          LOGGER.info("pathfilter is "+pathFilter.getClass().getName());
      paths = paths.parallelStream().filter(pathFilter).map(p -> {
        if (Double.isNaN(p.getScore())) {
          LOGGER.warn("Couldn't find scores for paths of predicate {}. Executing path scoring.",
                  predicate.getURI());
          return pathScorer.score(subject, preparedPredicate, object, p);
        } else {
          return p;
        }
      }).filter(p -> scoreFilter.test(p.getScore())).collect(Collectors.toList());
      LOGGER.info("number of paths after filtering is {}",paths.size());
      LOGGER.info(" -------------  Filter and Score Done  -------------");

      // Get the scores
      LOGGER.trace(" -------------  Start to get the scores  -------------");
      double[] scores = paths.stream().mapToDouble(p -> p.getScore()).toArray();
      if(LOGGER.isTraceEnabled()){
        LOGGER.trace("list of paths with their score");
        for(QRestrictedPath p :paths){
          LOGGER.trace("{} : {}",p.getScore(),p.toString());
        }
      }
      LOGGER.trace(" -------------  Get the scores Done  -------------");

      // Summarize the scores
      LOGGER.trace(" summarist is : {}",summarist.getClass().getName());
      LOGGER.trace(" scores are : {}", Arrays.toString(scores));
      double veracity = summarist.summarize(scores);
      LOGGER.debug(" calculated veracity is : {}",veracity);
      LOGGER.trace(" ------------- END OF FACT CHECKING -------------");

      return new FactCheckingResult(veracity, paths, fact);
  }

}
