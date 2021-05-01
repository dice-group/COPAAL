package org.dice_research.fc.config;

import java.io.FileNotFoundException;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.dice_research.fc.IFactChecker;
import org.dice_research.fc.data.FactCheckingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", allowCredentials = "true")
@RequestMapping("/api/v1/")
public class RESTController {

  @Autowired
  private IFactChecker factChecker;

  @GetMapping("/validate")
  public FactCheckingResult validate(
      @RequestParam(value = "subject", required = true) String subject,
      @RequestParam(value = "object", required = true) String object,
      @RequestParam(value = "property", required = true) String property)
      throws InterruptedException, FileNotFoundException, ParseException {

    Resource subjectURI = ResourceFactory.createResource(subject);
    Resource objectURI = ResourceFactory.createResource(object);
    Property propertyURI = ResourceFactory.createProperty(property);

    return factChecker.check(subjectURI, propertyURI, objectURI);
  }

}
