package org.dice.fact_check.corraborative.path_generator;

import java.util.concurrent.Callable;
import org.dice.fact_check.corraborative.PathQuery;
import org.springframework.stereotype.Component;

@Component
public interface IPathGenerator extends Callable<PathQuery> {

    public PathQuery returnQuery();
}
