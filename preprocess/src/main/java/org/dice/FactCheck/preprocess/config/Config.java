package org.dice.FactCheck.preprocess.config;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.dice_research.fc.sparql.query.QueryExecutionFactoryCustomHttp;
import org.dice_research.fc.sparql.query.QueryExecutionFactoryCustomHttpTimeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:application.properties")
public class Config {

    @Value("${SPARQL.endpoint.url:}")
    private String serviceURL;

    /**
     * @return The {@link QueryExecutionFactory} object depending on whether we are running the
     *         application on a local graph or on a remote endpoint
     */
    @Bean
    public QueryExecutionFactory getQueryExecutionFactory() {
        QueryExecutionFactory qef = new QueryExecutionFactoryCustomHttp(serviceURL);
        qef = new QueryExecutionFactoryCustomHttpTimeout(qef, 30000);
        return qef;
    }
}
