package org.dice.fact_check.corraborative.ui_result.create;

import org.springframework.stereotype.Component;

@Component
public class DefaultPathFactory implements IPathFactory {

  @Override
  public IPathBuilder returnPath(boolean verbalize) {
    if (verbalize) {
      return new VerbalizingPathBuilder();
    } else {
      return new DefaultPathBuilder();
    }
  }
}
