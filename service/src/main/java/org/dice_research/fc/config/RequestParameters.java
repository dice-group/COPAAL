package org.dice_research.fc.config;

import org.springframework.stereotype.Component;

/**
 * All the request parameters that influence the bean configuration should be declared here along
 * with the corresponding setters and getters. 
 * 
 * @author Alexandra Silva
 *
 */
@Component
public class RequestParameters {

  /**
   * True if verbalization is enabled
   */
  private boolean verbalize;

  public boolean isVerbalize() {
    return verbalize;
  }

  public void setVerbalize(boolean verbalize) {
    this.verbalize = verbalize;
  }

  // if true then for each path get a sample full path ( not only predicates)
  private boolean pathWithSample;

  public boolean isPathWithSample() {
    return pathWithSample;
  }

  public void setPathWithSample(boolean pathWithSample) {
    this.pathWithSample = pathWithSample;
  }
}
