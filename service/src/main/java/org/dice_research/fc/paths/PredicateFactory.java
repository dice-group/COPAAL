package org.dice_research.fc.paths;

import java.util.HashSet;
import java.util.Set;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.sparql.restrict.ITypeRestriction;
import org.dice_research.fc.sparql.restrict.TypeBasedRestriction;

public class PredicateFactory implements FactPreprocessor {

  private QueryExecutionFactory executioner;
  private boolean vTy;

  public PredicateFactory(QueryExecutionFactory qef, boolean vTy) {
    this.executioner = qef;
    this.vTy = vTy;
  }

  @Override
  public Predicate generatePredicate(Property predicate) {
    Set<String> dTypes = getDomain(predicate);
    Set<String> rTypes = getRange(predicate);

    ITypeRestriction domain = new TypeBasedRestriction(dTypes);
    ITypeRestriction range = new TypeBasedRestriction(rTypes);
    return new Predicate(predicate, domain, range);
  }

  /**
   * Retrieves the domain of a given predicate
   * 
   * @param predicate
   * @return the domain as a set of URIs
   */
  public Set<String> getDomain(Property predicate) {
    if (vTy) {
      return new HashSet<String>();
    }
    return getObjects(predicate, RDFS.domain);
  }

  /**
   * Retrieves the range of a given predicate
   * 
   * @param predicate
   * @return the range as a set of URIs
   */
  public Set<String> getRange(Property predicate) {
    if (vTy) {
      return new HashSet<String>();
    }
    return getObjects(predicate, RDFS.range);
  }

  /**
   * Retrieves the objects present in the graph with a given subject and predicate
   * 
   * @param subject the subject we want to check the objects for
   * @param predicate the predicate we want to check the objects for
   * @return Returns a set of the objects' URIs
   */
  public Set<String> getObjects(Resource subject, Property predicate) {
    Set<String> types = new HashSet<String>();
    SelectBuilder selectBuilder = new SelectBuilder();
    selectBuilder.addWhere(subject, predicate, NodeFactory.createVariable("x"));
    
    Query query = selectBuilder.build();
    try (QueryExecution queryExecution = executioner.createQueryExecution(query)) {
      ResultSet resultSet = queryExecution.execSelect();
      while (resultSet.hasNext()) {
        types.add(resultSet.next().get("x").asResource().getURI());
      }
    }
    return types;
  }

}
