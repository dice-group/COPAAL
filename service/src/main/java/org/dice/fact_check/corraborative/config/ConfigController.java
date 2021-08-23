package org.dice.fact_check.corraborative.config;

import java.io.FileNotFoundException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.dice.fact_check.corraborative.FactChecking;
import org.dice.fact_check.corraborative.path_generator.IPathGeneratorFactory.PathGeneratorType;
import org.dice.fact_check.corraborative.ui_result.CorroborativeGraph;
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

  @Autowired
  public ConfigController(FactChecking factChecking) {
    this.factChecking = factChecking;
  }

  // To verify status of server
  @RequestMapping("/test")
  public String defaultpage() {
    return "OK!";
  }

  @GetMapping("/validate")
  public CorroborativeGraph validate(
      @RequestParam(value = "verbalize", required = false,
          defaultValue = "false") boolean verbalize,
      @RequestParam(value = "pathlength", required = false, defaultValue = "2") String pathLength,
      @RequestParam(value = "subject", required = true) String subject,
      @RequestParam(value = "object", required = true) String object,
      @RequestParam(value = "property", required = true) String property,
      @RequestParam(value = "pathgeneratortype", defaultValue = "default") String pathgeneratortype,
      @RequestParam(value = "virtualType", defaultValue = "false") boolean virtualType)
      throws InterruptedException, FileNotFoundException, ParseException {
    final Model model = ModelFactory.createDefaultModel();
    Resource subjectURI = ResourceFactory.createResource(subject.replace("https", "http"));
    Resource objectURI = ResourceFactory.createResource(object.replace("https", "http"));
    Property propertyURI = ResourceFactory.createProperty(property.replace("https", "http"));
    Statement statement = ResourceFactory.createStatement(subjectURI, propertyURI, objectURI);
    model.add(statement);

    // the verbalize option is not supported, anymore! Instead, the fact checking
    // instance relies on a path factory that can be added to the following method
    // call

    // TODO: in future if we add more KB this section should change
    if (pathgeneratortype.equalsIgnoreCase("wikidata")
        || property.toLowerCase().contains("wikidata")) {
      return factChecking.checkFacts(model, Integer.parseInt(pathLength), virtualType,
          PathGeneratorType.wikidataPathGenerator, verbalize);
    }

    return factChecking.checkFacts(model, Integer.parseInt(pathLength), virtualType,
        PathGeneratorType.defaultPathGenerator, verbalize);
  }
}
