package org.dice.FactCheck.Corraborative.PathGenerator;

import org.dice.FactCheck.Corraborative.PathQuery;
import org.springframework.stereotype.Component;
import java.util.concurrent.Callable;

@Component
public interface IPathGenerator extends Callable<PathQuery> {

    public PathQuery returnQuery();
}
