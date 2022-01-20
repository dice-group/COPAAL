package org.dice_research.fc.paths.ext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.dice_research.fc.data.StringTriple;
import com.carrotsearch.hppc.BitSet;

/**
 * An extension of the {@link SPARQLBasedTripleProvider} which relies on a sampling strategy to
 * select triples. If all triples are selected, it behaves exactly like the original implementation.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class SamplingSPARQLBasedTripleProvider extends SPARQLBasedTripleProvider {

  /**
   * Seed used to initialized the random number generator.
   */
  protected long seed;

  /**
   * Constructor.
   * 
   * @param qef Query execution factory used to execute SPARQL queries.
   */
  public SamplingSPARQLBasedTripleProvider(QueryExecutionFactory qef) {
    this(qef, System.currentTimeMillis());
  }

  /**
   * Constructor.
   * 
   * @param qef Query execution factory used to execute SPARQL queries.
   * @param seed Seed used to initialized the random number generator.
   */
  public SamplingSPARQLBasedTripleProvider(QueryExecutionFactory qef, long seed) {
    super(qef);
    this.seed = seed;
  }

  @Override
  public List<StringTriple> provideTriples(String propertyIri, int numberOfTriples) {
    List<StringTriple> triples = super.provideTriples(propertyIri, -1);
    if (triples.size() <= numberOfTriples) {
      return triples;
    }
    List<StringTriple> selectedTriples = new ArrayList<StringTriple>(numberOfTriples);
    BitSet selectedIds = new BitSet();
    Random random = new Random(seed);
    int id;
    while (selectedTriples.size() < numberOfTriples) {
      id = random.nextInt(triples.size());
      if (!selectedIds.get(id)) {
        selectedIds.set(id);
        selectedTriples.add(triples.get(id));
      }
    }
    return selectedTriples;
  }

}
