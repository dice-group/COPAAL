package org.dice.FactCheck.Corraborative.UIResult.create;

import java.util.List;
import java.util.stream.Collectors;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.simba.bengal.paraphrasing.Paraphrasing;
import org.aksw.simba.bengal.verbalizer.SemWeb2NLVerbalizer;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.dice.FactCheck.Corraborative.Result;
import org.dice.FactCheck.Corraborative.UIResult.CorroborativeTriple;
import org.dice.FactCheck.Corraborative.UIResult.Path;
import org.dllearner.kb.sparql.SparqlEndpoint;

/**
 * An extension of the {@link DefaultPathFactory} that relies on a verbalizer to
 * create a natural language description of the path.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class VerbalizingPathFactory extends DefaultPathFactory {
    
    protected SemWeb2NLVerbalizer verbalizer = new SemWeb2NLVerbalizer(SparqlEndpoint.getEndpointDBpedia(), true, true);

    @Override
    public Path createPath(RDFNode subject, RDFNode object, Result result) {
        List<Statement> statements = generateVerbalizingTriples(result.getPathBuilder(), result.path,
                result.intermediateNodes, result.pathLength, subject, object);
        List<CorroborativeTriple> triples = statements.stream()
                .map(s -> new CorroborativeTriple(s.getSubject().toString(), s.getPredicate().toString(),
                        s.getObject().toString()))
                .collect(Collectors.toList());
        Document doc = verbalizer.generateDocument(statements, Paraphrasing.prop.getProperty("surfaceForms"));
        return new Path(triples, result.score, doc.getText());
    }
    
}
