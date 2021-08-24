package org.dice_research.fc.paths.verbalizer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.simba.bengal.paraphrasing.Paraphrasing;
import org.aksw.simba.bengal.verbalizer.SemWeb2NLVerbalizer;
import org.apache.commons.math3.util.Pair;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.dice_research.fc.data.QRestrictedPath;
import org.dice_research.fc.util.RDFUtil;
import org.dllearner.kb.sparql.SparqlEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Default path verbalizer implementation.
 * <p>
 * Retrieves the intermediate nodes from a path between two nodes and retrieves the verbalized
 * output.
 * 
 * @author Alexandra Silva
 *
 */
@Component
public class DefaultPathVerbalizer implements IPathVerbalizer {

  protected static final String INTERMEDIATE_VAR = "?x";

  protected QueryExecutionFactory qef;

  protected SemWeb2NLVerbalizer verbalizer =
      new SemWeb2NLVerbalizer(SparqlEndpoint.getEndpointDBpedia(), true, true);

  @Autowired
  public DefaultPathVerbalizer(QueryExecutionFactory qef) {
    this.qef = qef;
  }

  @Override
  public String verbalizePaths(Resource subject, Resource object, QRestrictedPath path) {
    List<Statement> stmts = getStmtsFromPath(subject, object, path);
    Document doc =
        verbalizer.generateDocument(stmts, Paraphrasing.prop.getProperty("surfaceForms"));
    String output = doc.getText();
    path.setVerbalizedOutput(output);
    return output;
  }

  /**
   * Retrieves a path's equivalent list of statements with the intermediate nodes.
   * <p>
   * Note: If there are multiple occurrences of the path, this method might return a {@link List}
   * with more {@link Statement}s.
   * 
   * @param subject The given fact's subject
   * @param object The given fact's object
   * @param path A path found
   * @return The path's equivalent list of statements with the intermediate nodes
   */
  public List<Statement> getStmtsFromPath(Resource subject, Resource object, QRestrictedPath path) {
    StringBuilder builder = new StringBuilder();

    // initialize as if there was a previous path stretch
    String stretchStart = null;
    String stretchEnd = RDFUtil.format(subject);

    List<Pair<Property, Boolean>> pathElements = path.getPathElements();
    for (int i = 0; i < pathElements.size(); i++) {
      Pair<Property, Boolean> pathStretch = pathElements.get(i);

      // new start is the old end, new end is updated
      stretchStart = stretchEnd;
      if (i == pathElements.size() - 1) {
        stretchEnd = RDFUtil.format(object);
      } else {
        stretchEnd = INTERMEDIATE_VAR + i;
      }

      // build string
      if (pathStretch.getSecond()) {
        builder.append(stretchStart);
        builder.append(RDFUtil.format(pathStretch.getFirst()));
        builder.append(stretchEnd);
      } else {
        builder.append(stretchEnd);
        builder.append(RDFUtil.format(pathStretch.getFirst()));
        builder.append(stretchStart);
      }
      builder.append(" . ");
      
    }
    return returnIntermediateNodes(builder.toString());
  }

  /**
   * Builds the path as a list of statements.
   * <p>
   * Note: If there are multiple occurrences of the path, this method might return a {@link List}
   * with more {@link Statement}s.
   * 
   * @param query SPARQL query to retrieve the intermediate nodes
   * @return The path's equivalent list of statements with the intermediate nodes
   */
  public List<Statement> returnIntermediateNodes(String queryStr) {
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT * WHERE {");
    builder.append(queryStr);
    builder.append("}");

    List<Statement> stmts = new ArrayList<>();
    Query query = QueryFactory.create(builder.toString());
    StringBuilder result = new StringBuilder();
    try (QueryExecution queryExecution = qef.createQueryExecution(query)) {
      ResultSet resultSet = queryExecution.execSelect();
      String localResult = queryStr.replace("?", "");
      while (resultSet.hasNext()) {
        QuerySolution curSol = resultSet.next();
        
        String temp = localResult;
        Iterator<String> varNames = curSol.varNames();
        while(varNames.hasNext()) {
          String curVarName = varNames.next();
          temp = temp.replace(curVarName, curSol.get(curVarName).toString());
        }
        result.append(temp);
      }
    }

    // build statements
    String[] items = result.toString().trim().split("\\s+[.]\\s+");
    for (String curStmt : items) {
      if(curStmt.isEmpty()) {
        continue;
      }
      stmts.add(RDFUtil.getStatementFromString(curStmt));
    }
    return stmts;
  }

}
