package org.dice_research.fc.paths.scorer.count;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.dice_research.fc.data.Predicate;
import org.dice_research.fc.paths.FactPreprocessor;
import org.dice_research.fc.paths.PredicateFactory;
import org.dice_research.fc.paths.scorer.ICountRetriever;
import org.dice_research.fc.query.LocalQueryExecutioner;
import org.junit.Test;

public class CountRetrieverTest {

  @Test
  public void test() {
    Model model = buildTestModel();
    QueryExecutionFactory qef = new LocalQueryExecutioner(model);
    Property property = ResourceFactory.createProperty("");
    
    FactPreprocessor predFactory = new PredicateFactory(qef, false);
    Predicate predicate = predFactory.generatePredicate(property);

    
    ICountRetriever countRetriever = new ApproximatingCountRetriever(qef);
    countRetriever.countPredicateInstances(predicate);

  }

  @Test
  public void test2() {
    Model model = buildTestModel();
    QueryExecutionFactory qef = new LocalQueryExecutioner(model);
    ICountRetriever countRetriever = new PropPathBasedPairCountRetriever(qef);

  }

  public Model buildTestModel() {
    Model model = ModelFactory.createDefaultModel();

    return model;

  }
}
