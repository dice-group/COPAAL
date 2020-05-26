package org.dice.FactCheck.Corraborative.PathGenerator;

import org.apache.jena.rdf.model.Statement;
import org.dice.FactCheck.Corraborative.Query.QueryExecutioner;

public interface IPathGeneratorFactory {

    public IPathGenerator build(String queryBuilder, Statement input, int pathLength, QueryExecutioner queryExecutioner);

}
