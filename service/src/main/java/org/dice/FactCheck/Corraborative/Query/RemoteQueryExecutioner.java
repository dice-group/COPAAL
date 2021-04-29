package org.dice.FactCheck.Corraborative.Query;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;

public class RemoteQueryExecutioner extends QueryExecutioner {
	private String serviceRequestURL;

	public RemoteQueryExecutioner() {
	}

	public RemoteQueryExecutioner(String serviceRequestURL) {
		this.serviceRequestURL = serviceRequestURL;
	}

	@Override
	public QueryExecution getQueryExecution(Query query) {
		return QueryExecutionFactory.createServiceRequest(serviceRequestURL, query);
	}

	public void setServiceRequestURL(String serviceURLResolve) {
		this.serviceRequestURL = serviceURLResolve;
	}

}
