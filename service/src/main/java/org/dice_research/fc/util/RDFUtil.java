package org.dice_research.fc.util;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;

/**
 * Util class for RDF graphs.
 * 
 * @author Alexandra Silva
 *
 */
public class RDFUtil {

  /**
   * Formats the node into a representation ready to be used in SPARQL queries.
   * 
   * @param node
   * @return
   */
  public static String format(RDFNode node) {
    String open = " ";
    String close = " ";
    if (node.isURIResource()) {
      open = " <";
      close = "> ";
    }
    if (node.isLiteral()) {
      open = " \"";
      close = "\" ";
    }

    return new StringBuilder(open).append(node.toString()).append(close).toString();
  }

  /**
   * Returns a {@link Statement} object from a string with 3 URIs.
   * 
   * @param stmtString
   * @return
   */
  public static Statement getStatementFromString(String stmtString) {
    // remove <>, ", trim and split by white space
    String trimmedStr = stmtString.replaceAll("<*>*\"*", "").replaceAll("\\.$", "").trim();
    String[] resources = trimmedStr.split("\\s+");

    if (resources.length != 3) {
      throw new IllegalArgumentException(
          "The Statement string doesn't have the expected 3 elements of a triple.");
    }

    // create resources and statement
    Resource subject = ResourceFactory.createResource(resources[0]);
    Property property = ResourceFactory.createProperty(resources[1]);
    Resource object = ResourceFactory.createResource(resources[2]);

    return ResourceFactory.createStatement(subject, property, object);

  }


  public static Triple getTripleFromString(String stmtString) {
    // remove <>, ", trim and split by white space
    String trimmedStr = stmtString.replaceAll("<*>*\"*", "").replaceAll("\\.$", "").trim();
    String[] resources = trimmedStr.split("\\s+");

    if (resources.length != 3) {
      throw new IllegalArgumentException(
          "The Statement string doesn't have the expected 3 elements of a triple.");
    }

    Node subject = getResourceOrVar(resources[0]);
    Node property = getResourceOrVar(resources[1]);
    Node object = getResourceOrVar(resources[2]);

    return Triple.create(subject, property, object);
  }
  
  public static Node getResourceOrVar(String str) {
    Node node;
    if (str.contains("?")) {
      node = NodeFactory.createVariable(str.replace("?", ""));
    } else {
      node = NodeFactory.createURI(str);
    }
    return node;
  }


}
