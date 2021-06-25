package org.dice_research.fc.paths.ext;

import java.util.ArrayList;
import java.util.List;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.dice_research.fc.data.StringTriple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This {@link TripleProvider} implementation uses a SPARQL endpoint to retrieve triples with the
 * given property.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class SPARQLBasedTripleProvider implements TripleProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(SPARQLBasedTripleProvider.class);

  /**
   * Query execution factory used to execute SPARQL queries.
   */
  protected QueryExecutionFactory qef;

  /**
   * Constructor.
   * 
   * @param qef Query execution factory used to execute SPARQL queries.
   */
  public SPARQLBasedTripleProvider(QueryExecutionFactory qef) {
    super();
    this.qef = qef;
  }

  @Override
  public List<StringTriple> provideTriples(String propertyIri, int numberOfTriples) {
    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append("SELECT DISTINCT ?s ?o WHERE { ?s <");
    queryBuilder.append("> ?o }");
    if (numberOfTriples >= 0) {
      queryBuilder.append(" LIMIT ");
      queryBuilder.append(numberOfTriples);
    }
    List<StringTriple> triples = new ArrayList<>();


    try (QueryExecution qe = qef.createQueryExecution(queryBuilder.toString());) {
      ResultSet rs = qe.execSelect();
      QuerySolution solution;
      RDFNode s;
      RDFNode o;
      while (rs.hasNext()) {
        solution = rs.next();
        s = solution.get("s");
        o = solution.get("o");
        if (o.isURIResource()) {
          triples
              .add(new StringTriple(s.asResource().getURI(), propertyIri, o.asResource().getURI()));
        } else {
          LOGGER.info(
              "Got a triple with an object for {} that is not a URI resource: {}. It will be ignored.",
              propertyIri, o.toString());
        }
      }
    } catch (Exception e) {
      LOGGER.error("Couldn't retrieve triples for {}. Returning null.");
      return null;
    }
    return triples;
  }


}
