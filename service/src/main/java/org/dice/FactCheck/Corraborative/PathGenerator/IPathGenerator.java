package org.dice.FactCheck.Corraborative.PathGenerator;

import org.dice.FactCheck.Corraborative.PathQuery;

import java.util.concurrent.Callable;

public interface IPathGenerator extends Callable<PathQuery> {

    public PathQuery returnQuery();
}
