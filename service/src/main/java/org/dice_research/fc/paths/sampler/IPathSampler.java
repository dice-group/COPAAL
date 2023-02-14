package org.dice_research.fc.paths.sampler;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.dice_research.fc.data.FactCheckingResult;
import org.dice_research.fc.data.IPieceOfEvidence;

public interface IPathSampler {
    default void gatherSample(FactCheckingResult result) {
        Statement fact = result.getFact();
        for (IPieceOfEvidence curPath : result.getPiecesOfEvidence()) {
            getSample(fact.getSubject(), fact.getObject().asResource(), curPath);
        }
    };

    String getSample(Resource subject, Resource object, IPieceOfEvidence path);
}
