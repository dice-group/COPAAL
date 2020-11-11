package org.dice.FactCheck.Corraborative.Config;

import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.dice.FactCheck.Corraborative.PathGenerator.IPathGeneratorFactory.PathGeneratorType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 * @author Farshad Afshari farshad.afshari@uni-paderborn.de
 *     <p>SEP2020 Farshad remove redundant methods and add serviceURLResolve
 */
@Component
public class Config {

  @Value("${info.service.url.default}")
  private String serviceURLDefault;

  @Value("${info.service.url.wikidata}")
  private String serviceURLWikiData;

  public String serviceURLResolve(PathGeneratorType pathGeneratorType) throws ParseException {
    switch (pathGeneratorType) {
      case defaultPathGenerator:
        return serviceURLDefault;
      case wikidataPathGenerator:
        return serviceURLWikiData;
    }
    throw new ParseException("Can not resolve the SPARQL server");
  }
}
