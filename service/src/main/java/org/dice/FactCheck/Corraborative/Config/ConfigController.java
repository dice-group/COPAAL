package org.dice.FactCheck.Corraborative.Config;

import java.io.FileNotFoundException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.dice.FactCheck.Corraborative.FactChecking;
import org.dice.FactCheck.Corraborative.PathGenerator.IPathGeneratorFactory.PathGeneratorType;
import org.dice.FactCheck.Corraborative.Query.SparqlQueryGenerator;
import org.dice.FactCheck.Corraborative.UIResult.CorroborativeGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", allowCredentials = "true")
@RequestMapping("/api/v1/")
public class ConfigController {

  private final FactChecking factChecking;
  private final SparqlQueryGenerator sparqlQueryGenerator;

  @Autowired
  public ConfigController(FactChecking factChecking, SparqlQueryGenerator sparqlQueryGenerator) {
    this.factChecking = factChecking;
    this.sparqlQueryGenerator = sparqlQueryGenerator;
  }

  // To verify status of server
  @RequestMapping("/test")
  public String defaultpage() {
    return "OK!";
  }

  @GetMapping("/validate")
  public CorroborativeGraph validate(
      @RequestParam(value = "verbalize", required = false, defaultValue = "false") String verbalize,
      @RequestParam(value = "pathlength", required = false, defaultValue = "2") String pathLength,
      @RequestParam(value = "subject", required = true) String subject,
      @RequestParam(value = "object", required = true) String object,
      @RequestParam(value = "property", required = true) String property,
      @RequestParam(value = "pathgeneratortype", defaultValue = "default") String pathgeneratortype,
      @RequestParam(value = "virtualType", defaultValue = "false") boolean virtualType)
      throws InterruptedException, FileNotFoundException, ParseException {
    final Model model = ModelFactory.createDefaultModel();
    Resource subjectURI = ResourceFactory.createResource(subject);
    Resource objectURI = ResourceFactory.createResource(object);
    Property propertyURI = ResourceFactory.createProperty(property);
    Statement statement = ResourceFactory.createStatement(subjectURI, propertyURI, objectURI);
    model.add(statement);

    // the verbalize option is not supported, anymore! Instead, the fact checking
    // instance relies on a path factory that can be added to the following method
    // call

    // TODO: in future if we add more KB this section should change
    if (pathgeneratortype.toLowerCase().equals("wikidata")
        || property.toLowerCase().contains("wikidata")) {
      return factChecking.checkFacts(
          model,
          Integer.parseInt(pathLength),
          virtualType,
          PathGeneratorType.wikidataPathGenerator);
    }

    return factChecking.checkFacts(
        model, Integer.parseInt(pathLength), virtualType, PathGeneratorType.defaultPathGenerator);
  }
}
