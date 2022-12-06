//package org.dice_research.fc.paths.paths.search;
//import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
//import org.apache.jena.rdf.model.Model;
//import org.apache.jena.rdf.model.ModelFactory;
//import org.apache.jena.rdf.model.Resource;
//import org.dice_research.fc.data.Predicate;
//import org.dice_research.fc.data.QRestrictedPath;
//import org.dice_research.fc.paths.search.SPARQLBasedSOPathSearcher;
//import org.dice_research.fc.run.Application;
//import org.dice_research.fc.sparql.filter.IRIFilter;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;

//import java.util.Collection;

//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = Application.class)
//public class PathBasedFactCheckerTest {
//  @Autowired
//   QueryExecutionFactory qef;

//    @Autowired
//   Collection<IRIFilter> propertyFilter;
    //this method used to extract the paths
    // at Ontotext clinical trails dataset
    // actually it is not a test that is why the assert is commented
    //@Test
//  public void findClinicalTrailsPaths(){

//       SPARQLBasedSOPathSearcher service = new SPARQLBasedSOPathSearcher(qef,4,propertyFilter);

//        Model model = ModelFactory.createDefaultModel();
//       Resource subject = model.createResource("http://linkedlifedata.com/resource/drugcentral/structure/758");
//      Predicate predicate = new Predicate(model.createProperty("http://rdf.frockg.eu/frockg/ontology/hasAdverseReaction"), null, null);
//       Resource object = model.createResource("http://rdf.frockg.eu/resource/umls/id/C4554630");
//       Collection<QRestrictedPath> actual = service.search(subject, predicate, object);
//       System.out.println(actual.size()+" paths Founds:");
                //       for (QRestrictedPath q: actual) {
//           System.out.println(q);
//       }
//       //Assert.assertTrue(actual.size()  > 0);
//   }
//}
