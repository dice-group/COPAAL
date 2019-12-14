package org.dice.FactCheck.Corraborative.Query;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;



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
