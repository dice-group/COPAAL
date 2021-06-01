package org.dice_research.fc.paths.imprt;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
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
 * This class pre-processes the meta-paths by ascertaining their existence between two nodes in the
 * knowledge graph and removing the non-existent.
 * <p>
 * It is written for ESTHER's use-case in mind.
 * 
 * @author Alexandra Silva
 *
 */
@Component
public class EstherPathProcessor extends MetaPathsProcessor {
  private static final Logger LOGGER = LoggerFactory.getLogger(EstherPathProcessor.class);

  @Autowired
  public EstherPathProcessor(Map<Property, Collection<QRestrictedPath>> metaPaths,
      QueryExecutionFactory qef) {
    super(metaPaths, qef);
  }

  /**
   * Removes the non-existent paths in the KG from the meta-paths {@link Collection}
   */
  @Override
  public Collection<QRestrictedPath> processMetaPaths(Statement fact) {
    Collection<QRestrictedPath> paths = metaPaths.get(fact.getPredicate());

    Collection<QRestrictedPath> copy =
        paths.stream().map(curPath -> new QRestrictedPath(curPath.getPathElements(), curPath.getScore()))
            .collect(Collectors.toList());

    copy.removeIf(curPath -> ask(fact.getSubject().toString(), curPath.getPropertyPath(),
        fact.getObject().toString()));
    return copy;
  }


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
