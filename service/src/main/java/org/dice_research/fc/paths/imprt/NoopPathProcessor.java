package org.dice_research.fc.paths.imprt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;
import org.dice_research.fc.data.QRestrictedPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * No-op. Does no further processing to the paths.
 * 
 * @author Alexandra Silva
 *
 */
@Component
public class NoopPathProcessor extends MetaPathsProcessor {

  @Autowired
  public NoopPathProcessor(Map<Property, List<QRestrictedPath>> metaPaths,
      QueryExecutionFactory qef) {
    super(metaPaths, qef);
  }

  @Override
  public Collection<QRestrictedPath> processMetaPaths(Statement fact) {
    return metaPaths.get(fact.getPredicate());
  }

}
