package org.dice.FactCheck.Corraborative.Config;

import java.io.FileNotFoundException;
import java.util.concurrent.atomic.AtomicLong;

import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.dice.FactCheck.Corraborative.FactChecking;
import org.dice.FactCheck.Corraborative.Query.SparqlQueryGenerator;
import org.dice.FactCheck.Corraborative.UIResult.CorroborativeGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", allowCredentials = "true")
public class ConfigController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    private final FactChecking factChecking;
    private final SparqlQueryGenerator sparqlQueryGenerator;

    @Autowired
    public ConfigController(FactChecking factChecking, SparqlQueryGenerator sparqlQueryGenerator) {

        this.factChecking = factChecking;
        this.sparqlQueryGenerator = sparqlQueryGenerator;
    }

    @GetMapping("/validate")
    public CorroborativeGraph validate(
            @RequestParam(value = "verbalize", required = false, defaultValue = "false") String verbalize,
            @RequestParam(value = "pathlength", required = false, defaultValue = "2") String pathLength,
            @RequestParam(value = "subject", required = true) String subject,
            @RequestParam(value = "object", required = true) String object,
            @RequestParam(value = "property", required = true) String property)
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
        return factChecking.checkFacts(model, Integer.parseInt(pathLength));
    }
}
