package org.dice.FactCheck.Corraborative.Query;

import java.util.logging.Logger;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component
public class QueryExecutioner {

	private String serviceRequestURL;

	public QueryExecutioner() {
	}


	public void setServiceRequestURL(String serviceRequestURL) {
		this.serviceRequestURL = serviceRequestURL;
	}

	public QueryExecution getQueryExecution(Query query){
		return QueryExecutionFactory.createServiceRequest(serviceRequestURL, query);
	}
}
