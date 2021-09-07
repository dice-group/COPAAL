package org.dice_research.fc.paths.scorer.count.decorate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.paths.scorer.ICountRetriever;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;
import org.dice_research.fc.sparql.restrict.OneOfRestriction;

/**
 * A class that samples instances from the given restrictions. All queries are restricted to the
 * sampled instances instead of using the original restrictions. The maximum count is adapted
 * accordingly.
 * 
 * <p>
 * The sampling method ({@link #sampleInstances(List, int)} is implemented in a way that with the
 * same seed and the same given list of instances and the same number of instances that should be
 * sampled, the method will return the same result if it is called several times.
 * </p>
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class SamplingCountRetrieverDecorator extends AbstractCountRetrieverDecorator {
  /**
   * The seed that is used to initialize the random number generator.
   */
  protected long seed;
  /**
   * The maximum size of a list of instances.
   */
  protected int maxSampleSize;
  /**
   * The {@link QueryExecutionFactory} used to derive the list of instances.
   */
  protected QueryExecutionFactory qef;

  /**
   * Constructor.
   * 
   * @param decorated The decorated count retriever that is used to get the counts for the
   *        predicates and paths restricted to the sampled list of instances
   * @param seed The seed that is used to initialize the random number generator
   * @param maxSampleSize The maximum size of a list of instances
   * @param qef The {@link QueryExecutionFactory} used to derive the list of instances
   */
  public SamplingCountRetrieverDecorator(ICountRetriever decorated, long seed, int maxSampleSize,
      QueryExecutionFactory qef) {
    super(decorated);
    this.seed = seed;
    this.maxSampleSize = maxSampleSize;
    this.qef = qef;
  }

  /**
   * Creates a new {@link Predicate} instance with replaced restrictions. The new restrictions
   * contain list of instances to which they will be limited.
   * 
   * @param predicate The original predicate
   * @return The newly created predicate with the new restrictions
   */
  protected Predicate replacePredicate(Predicate predicate) {
    return new Predicate(predicate.getProperty(), replaceRestriction(predicate.getDomain()),
        replaceRestriction(predicate.getRange()));
  }

  /**
   * Replaces the given restriction with a restriction that is based on a list of instances that
   * have been sampled from all instance that fulfill the given restriction.
   * 
   * @param restriction The original restriction
   * @return The newly created restriction
   */
  protected OneOfRestriction replaceRestriction(ITypeRestriction restriction) {
    if (restriction == null) {
      return null;
    }
    List<String> instances = retrieveInstances(restriction);
    return new OneOfRestriction(sampleInstances(instances, maxSampleSize));
  }

  /**
   * Samples the given number of instances from the given list of instances and returns the list. If
   * the given list does not contain enough instances the original list is returned. It should be
   * noted that the list of instances might be sorted during the process.
   * 
   * @param instances The list of instances from which the single instances should be sampled
   * @param sampleSize The number of instances that should be sampled
   * @return The sampled instances
   */
  protected Collection<String> sampleInstances(List<String> instances, int sampleSize) {
    if (sampleSize >= instances.size()) {
      return instances;
    }
    // Sort the instances to ensure that we have the same order every time
    Collections.sort(instances);
    // Get the hash of the first instance to get a seed for this sampling run
    // (it should be the same seed as long as we have the same instance list)
    long localSeed = seed + instances.get(0).hashCode();
    localSeed = Long.reverse(localSeed);
    // Randomly choose instances until we have enough instances
    Random random = new Random(localSeed);
    Set<String> chosen = new HashSet<String>(2 * sampleSize);
    while (chosen.size() < sampleSize) {
      chosen.add(instances.get(random.nextInt(instances.size())));
    }
    return chosen;
  }

  protected List<String> retrieveInstances(ITypeRestriction restriction) {
    List<String> instances = new ArrayList<>();
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT ?s WHERE {");
    restriction.addRestrictionToQuery("s", builder);
    builder.append("}");
    QueryExecution qe = null;
    try {
      qe = qef.createQueryExecution(builder.toString());
      ResultSet rs = qe.execSelect();
      QuerySolution qs;
      while (rs.hasNext()) {
        qs = rs.next();
        RDFNode node = qs.get("s");
        if (node.isURIResource()) {
          instances.add(node.asResource().getURI());
        } else if (node.isAnon()) {
          instances.add(node.asResource().getId().getBlankNodeId().getLabelString());
        }
      }
    } finally {
      if (qe != null) {
        qe.close();
      }
    }
    return instances;
  }

  @Override
  public long countPathInstances(QRestrictedPath path, ITypeRestriction domainRestriction,
      ITypeRestriction rangeRestriction) {
    return decorated.countPathInstances(path, replaceRestriction(domainRestriction),
        replaceRestriction(rangeRestriction));
  }

  @Override
  public long countPredicateInstances(Predicate predicate) {
    return decorated.countPredicateInstances(replacePredicate(predicate));
  }

  @Override
  public long countCooccurrences(Predicate predicate, QRestrictedPath path) {
    return decorated.countCooccurrences(new Predicate(predicate.getProperty(),
        replaceRestriction(predicate.getDomain()), replaceRestriction(predicate.getRange())), path);
  }

  @Override
  public long deriveMaxCount(Predicate predicate) {
    OneOfRestriction domain = replaceRestriction(predicate.getDomain());
    OneOfRestriction range = replaceRestriction(predicate.getRange());
    return ((domain != null) ? domain.getNumberOfValues() : 0)
        * ((range != null) ? range.getNumberOfValues() : 0);
  }

}
