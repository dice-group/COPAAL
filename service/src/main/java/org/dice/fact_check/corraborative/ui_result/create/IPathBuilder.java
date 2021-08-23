package org.dice.fact_check.corraborative.ui_result.create;

import org.apache.jena.rdf.model.RDFNode;
import org.dice.fact_check.corraborative.Result;
import org.dice.fact_check.corraborative.ui_result.Path;

/**
 * Classes implementing this interface create {@link Path} objects based on the given {@link Result}
 * objects.
 *
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 */
public interface IPathBuilder {

  /**
   * Creates a {@link Path} object based on the given {@link Result} object.
   *
   * @param subject the subject of the checked fact
   * @param object the object of the checked fact
   * @param result the {@link Result} of the search for corroborative paths
   * @return The created {@link Path} instance
   */
  public Path createPath(RDFNode subject, RDFNode object, Result result);
}
