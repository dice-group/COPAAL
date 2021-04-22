package org.dice_research.fc.tools;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;

public class ProgramParams {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProgramParams.class);
	
	@Parameter(names = { "--length", "-l" }, description = "Maximum path length.")
	int length = 3;

	@Parameter(names = { "-vTy" }, description = "Virtual types?")
	boolean isvTy = false;
	
	@Parameter(names = { "--model", "-m" }, description = "Local file of the graph.")
	String model;
	
	@Parameter(names = { "--sparql-endpoint" }, description = "SPARQL endpoint where the graph is hosted.")
	String endpoint;
	
	@Parameter(names = { "--facts", "-f" }, description = "Facts for the application to check.")
	String facts;
	
	@Parameter(names = { "--output", "-o" }, description = "Desired output file name.")
	String outputFile;
	
	
	public void logArgs() {
		LOGGER.info("\nDate: {}", new Date());
		LOGGER.info("\nLength: {}", length);
		LOGGER.info("\nVirtual types: {}", length);
		LOGGER.info("\nModel: {}", model);
		LOGGER.info("\nSPARQL endpoint: {}", endpoint);
		LOGGER.info("\nFacts: {}", facts);
		LOGGER.info("\nSaving to: {}", facts);
	}
}
