package org.dice_research.fc.tools;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;

public class ProgramParams {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProgramParams.class);

  @Parameter(names = {"--facts", "-f"}, description = "Facts for the application to check.")
  public String facts;

  @Parameter(names = {"--output", "-o"}, description = "Desired output file name.")
  public String outputFile;

  public void logArgs() {
    LOGGER.info("Date: {}", new Date());
    LOGGER.info("Facts: {}", facts);
    LOGGER.info("Saving to: {}", outputFile);
  }
}
