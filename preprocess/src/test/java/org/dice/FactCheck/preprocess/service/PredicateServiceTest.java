package org.dice.FactCheck.preprocess.service;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import java.util.*;

import org.dice_research.fc.data.Predicate;
import org.junit.Assert;
import org.junit.Test;

public class PredicateServiceTest {
    @Test
    public void serviceShouldBringAllThePredicatesCorrect(){
        QueryExecutionFactory qef;
        Model model = ModelFactory.createDefaultModel();
        model.read("tempPredicates.nt");
        qef = new QueryExecutionFactoryModel(model);

        PredicateService service = new PredicateService(qef);
        List<String> filters = new ArrayList<>();
        filters.add("http://www.example.org");
        Set<Predicate> actual = (Set<Predicate>) service.allPredicates(filters);

        Assert.assertEquals(5, actual.size());
    }
}
