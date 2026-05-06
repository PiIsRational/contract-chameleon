package org.contract_lib.lang.contract_lib.tools;

import org.contract_lib.contract_chameleon.error.ChameleonMessageManager;

import org.contract_lib.lang.contract_lib.ast.Abstraction;

public final class ClassNameExtractor {

  private static final String IMPL_SUFFIX = "Impl";
  private static final String DEFAULT_PACKAGE = "example";
  private static final String DOT = ".";
  private static final String TODO_MESSAGE = "//TODO: Implement '%s'.";

  public ClassNameExtractor(
      Abstraction abstraction,
      ChameleonMessageManager messageManager) {
    String abstractionName = abstraction.identifier().name().identifier();
    int lastDot = abstractionName.lastIndexOf(DOT);

    if (lastDot == -1) {
      this.packageName = DEFAULT_PACKAGE;
      this.className = abstractionName;
      //TODO: print warning
    } else {
      this.packageName = abstractionName.substring(0, lastDot);
      this.className = abstractionName.substring(lastDot + 1);
      if (this.packageName.equals(DEFAULT_PACKAGE)) {
        //TODO: print warning
      }
    }
  }

  private final String className;
  private final String packageName;

  public String getClassBodyPlaceholder() {
    return String.format(TODO_MESSAGE, getClassIdentifier());
  }

  public String getImplClassName() {
    return className + IMPL_SUFFIX;
  }

  public String getClassIdentifier() {
    return packageName + DOT + className;
  }

  public String getClassName() {
    return className;
  }

  public String getPackageName() {
    return packageName;
  }

  public String getDirectoryName() {
    return packageName.replaceAll("\\.", "/");
  }
}
