package org.dice.FactCheck.Corraborative.UIResult.create;

import org.springframework.stereotype.Component;

@Component
public class DefaultPathFactory implements IPathFactory {

  @Override
  public IPathBuilder ReturnPath(boolean verbalize) {
    if (verbalize) {
      return new VerbalizingPathBuilder();
    } else {
      return new DefaultPathBuilder();
    }
  }
}
