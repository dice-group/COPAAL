package org.dice_research.fc.paths.imprt;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;
import org.dice_research.fc.data.QRestrictedPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class pre-processes the saved paths by ascertaining their existence between two nodes in the
 * knowledge graph and removing the non-existent.
 * 
 * @author Alexandra Silva
 *
 */
@Component
public class EstherPathProcessor extends MetaPathsProcessor {
  private static final Logger LOGGER = LoggerFactory.getLogger(EstherPathProcessor.class);

  @Autowired
  public EstherPathProcessor(String metaPaths, QueryExecutionFactory qef) {
    super(metaPaths, qef);
  }

  /**
   * Removes the non-existent paths in the KG from the meta-paths
   */
  @Override
  public Collection<QRestrictedPath> processMetaPaths(Statement fact) {
    Entry<Property, List<QRestrictedPath>> paths =
        readMetaPaths(metaPaths + fact.getPredicate().getLocalName() + JSON_EXTENSION);
    if (paths == null) {
      return null;
    }

    // remove if non existent
    paths.getValue().removeIf(curPath -> !ask(fact.getSubject().toString(), curPath.getEvidence(),
        fact.getObject().toString()));

    return paths.getValue();
  }

  /**
   * @param subject The subject's URI.
   * @param propertyPath The property path according to SPARQL standards.
   * @param object The object's URI.
   * @return True if a path exists between a given subject and object.
   */
  private boolean ask(String subject, String propertyPath, String object) {
    StringBuilder query = new StringBuilder("ASK  { ");
    query.append("<").append(subject).append("> ").append(propertyPath).append(" <").append(object)
        .append("> ").append(". }");

    try (QueryExecution qe = qef.createQueryExecution(query.toString());) {
      return qe.execAsk();
    } catch (Exception e) {
      LOGGER.error("Defaulting ASK query to false. Got an exception while executing query "
          + query.toString(), e);
      return false;
    }
  }


}
