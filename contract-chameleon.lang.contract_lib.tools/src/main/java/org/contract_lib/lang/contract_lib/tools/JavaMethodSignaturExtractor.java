package org.contract_lib.lang.contract_lib.tools;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

import org.contract_lib.contract_chameleon.error.ChameleonMessageManager;

import org.contract_lib.lang.contract_lib.ast.Contract;
import org.contract_lib.lang.contract_lib.ast.Sort;
import org.contract_lib.lang.contract_lib.ast.Formal;
import org.contract_lib.lang.contract_lib.ast.ArgumentMode;
import org.contract_lib.lang.contract_lib.ast.Argument;

public final class JavaMethodSignaturExtractor {

  private static final String DEFAULT_RETURN_IDENTIFIER = "result";
  private static final String DEFAULT_THIS_IDENTIFIER = "this";
  private static final String DOT = ".";
  private static final String TODO_MESSAGE = "// TODO: Implement '%s'.";

  private String contractIdentifier;
  private String methodName;
  private String ownerClassIdentifier;
  private Optional<Sort> ownerSort;
  private boolean isStatic;
  private boolean readOnlyThis;
  private final List<Argument> arguments;
  private final List<Argument> inoutArguments;
  private final List<Argument> inArguments;
  private Optional<Sort> returnType;

  //Internal field to identify fields wearing the same name
  private final Set<String> fieldIdentifier;

  public JavaMethodSignaturExtractor(
      Contract contract,
      ChameleonMessageManager messageManager) {
    this.isStatic = true;
    this.fieldIdentifier = new HashSet<>();
    this.arguments = new ArrayList<>();
    this.inoutArguments = new ArrayList<>();
    this.inArguments = new ArrayList<>();
    this.returnType = Optional.empty();
    this.contractIdentifier = contract.identifier().identifier();

    Optional<String> className = extractName(contractIdentifier);
    contract.formals().stream().forEachOrdered(this::handleFormal);

    className.ifPresent((cn) -> {
      ownerClassIdentifier = cn;
    });

    if (!className.isPresent()) {
      System.err.println("ERROR: each contract needs to be associated with an abstraction.");
    }
  }

  public String getDefaultMethodBody() {
    return String.format(TODO_MESSAGE, contractIdentifier);
  }

  public String methodName() {
    return this.methodName;
  }

  public String ownerClassIdentifier() {
    return this.ownerClassIdentifier;
  }

  public boolean isStatic() {
    return this.isStatic;
  }

  public Optional<Sort> getReturnType() {
    return this.returnType;
  }

  public List<Argument> getArguments() {
    return new ArrayList<>(this.arguments);
  }

  public List<Argument> inoutArguments() {
    return new ArrayList<>(this.inoutArguments);
  }

  public List<Argument> inArguments() {
    return new ArrayList<>(this.inArguments);
  }

  public Optional<Argument> thisReadOnlyArgument() {
    if (isStatic || !this.readOnlyThis) {
      return Optional.empty();
    } else {
      return this.ownerSort
          .map((s) -> new Argument(s, DEFAULT_THIS_IDENTIFIER));
    }
  }

  public Optional<Argument> thisMutatableArgument() {
    if (isStatic || this.readOnlyThis) {
      return Optional.empty();
    } else {
      return this.ownerSort
          .map((s) -> new Argument(s, DEFAULT_THIS_IDENTIFIER));
    }
  }

  private void handleFormal(Formal formal) {
    if (formal.argumentMode().equals(ArgumentMode.OUT)) {
      handleOutParameter(formal);
    } else if (formal.argumentMode().equals(ArgumentMode.INOUT)) {
      handleInoutParameter(formal);
    } else if (formal.argumentMode().equals(ArgumentMode.IN)) {
      handleInParameter(formal);
    } else {
      //TODO: print error
      System.err.println("ERROR: Unexpected argument mode: " + formal.argumentMode());
    }
  }

  private void handleOutParameter(Formal formal) {
    if (formal.identifier().identifier().equals(DEFAULT_RETURN_IDENTIFIER)) {
      this.returnType = Optional.of(formal.sort());
    } else {
      //TODO: print error, out must always be a default parameter
      System.err.println("ERROR: There must only be one out parameter named `result`");
    }
  }

  private void addParameter(Formal f, List<Argument> toList) {
    String name = f.identifier().identifier();
    if (name.equals(DEFAULT_RETURN_IDENTIFIER)) {
      //TODO: error must not be default ret identifier
      System.err.println("ERROR: Only the out parameter can be named `result`");
    } else {
      this.arguments.add(new Argument(f.sort(), name));
      toList.add(new Argument(f.sort(), name));
    }
  }

  private void handlThisParameter(Formal formal) {
    if (this.isStatic) {
      //TODO: Add conversion from type to class identifier
      System.err.println("WARNING: Type of `this` is not checked yet.");
      this.ownerSort = Optional.of(formal.sort());
      this.isStatic = false;
      if (formal.argumentMode().equals(ArgumentMode.IN)) {
        this.readOnlyThis = true;
      } else if (formal.argumentMode().equals(ArgumentMode.INOUT)) {
        this.readOnlyThis = false;
      } else if (formal.argumentMode().equals(ArgumentMode.OUT)) {
        System.err.println("ERROR: 'this' must not be used for out parameter.");
      }
    } else {
      //TODO: print error: two label with this
      System.err.println("ERROR: Parameter with name `this` is used multiple times.");
    }
  }

  private void handleInoutParameter(Formal formal) {
    if (formal.identifier().identifier().equals(DEFAULT_THIS_IDENTIFIER)) {
      handlThisParameter(formal);
    } else {
      addParameter(formal, this.inoutArguments);
    }
  }

  private void handleInParameter(Formal formal) {
    if (formal.identifier().identifier().equals(DEFAULT_THIS_IDENTIFIER)) {
      handlThisParameter(formal);
    } else {
      addParameter(formal, this.inArguments);
    }
  }

  private Optional<String> extractName(String name) {
    int lastDot = name.lastIndexOf(DOT);

    if (lastDot == -1) {
      this.methodName = name;
      return Optional.empty();
    } else {
      this.methodName = name.substring(lastDot + 1);
      return Optional.of(name.substring(0, lastDot));
    }
  }
}
