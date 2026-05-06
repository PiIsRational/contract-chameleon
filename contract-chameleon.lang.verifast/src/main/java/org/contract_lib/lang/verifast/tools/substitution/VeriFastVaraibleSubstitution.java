
package org.contract_lib.lang.verifast.tools.substitution;

import java.util.List;
import java.util.Optional;

import org.contract_lib.contract_chameleon.contexts.MessageContext;
import org.contract_lib.lang.verifast.ast.VeriFastContract;
import org.contract_lib.lang.verifast.ast.VeriFastExpression;

// Note: There might be more general classes and implementation, but as they are far mor complicated,
// this simple implementation is enough for the moment.

public class VeriFastVaraibleSubstitution {

  public VeriFastVaraibleSubstitution(
      CanSubstituteInterface<VeriFastExpression.Variable> canSubstitute,
      SubstitutionInterface<VeriFastExpression.Variable> substitution,
      MessageContext messageContext) {

    this.canSubstitute = canSubstitute;
    this.varSubsitution = substitution;
    this.messageContext = messageContext;
  }

  SubstitutionInterface<VeriFastExpression.Variable> varSubsitution;
  CanSubstituteInterface<VeriFastExpression.Variable> canSubstitute;
  MessageContext messageContext;

  public VeriFastContract subVeriFastContract(VeriFastContract spec) {
    VeriFastExpression requires = subVeriFastExpression(spec.requires());
    VeriFastExpression ensures = subVeriFastExpression(spec.ensures());
    return new VeriFastContract(requires, ensures);
  }

  public VeriFastExpression subVeriFastExpression(VeriFastExpression expr) {
    return expr.perform(
        this::subVeriFastChain,
        this::subVeriFastBooleanValue,
        this::subVeriFastIntegerValue,
        this::subVeriFastPredicate,
        this::subVeriFastVariable,
        this::subVeriFastVariableAssig,
        this::subVeriFastBinaryOperation,
        this::subVeriFastFixpoint);
  }

  public VeriFastExpression subVeriFastChain(VeriFastExpression.Chain expr) {
    List<VeriFastExpression> expressions = expr.expressions().stream().map(this::subVeriFastExpression).toList();
    return new VeriFastExpression.Chain(expressions);
  }

  public VeriFastExpression subVeriFastVariable(VeriFastExpression.Variable var) {
    if (canSubstitute.canSubstitute(var)) {
      messageContext
          .logError(String.format("%s is not allowed to be substituted!", var.variable()));
    }
    return varSubsitution.substitue(var);
  }

  public VeriFastExpression subVeriFastVariableAssig(VeriFastExpression.VariableAssignment varAssignment) {
    return varAssignment;
  }

  public VeriFastExpression subVeriFastBooleanValue(VeriFastExpression.BooleanValue boolV) {
    return boolV;
  }

  public VeriFastExpression subVeriFastIntegerValue(VeriFastExpression.IntegerValue intV) {
    return intV;
  }

  public VeriFastExpression subVeriFastBinaryOperation(VeriFastExpression.BinaryOperation op) {
    VeriFastExpression left = subVeriFastExpression(op.left());
    VeriFastExpression right = subVeriFastExpression(op.right());

    return new VeriFastExpression.BinaryOperation(op.operation(), left, right);
  }

  public VeriFastExpression subVeriFastFixpoint(VeriFastExpression.Fixpoint fix) {
    List<VeriFastExpression> parameters = fix.parameters().stream().map(this::subVeriFastExpression).toList();
    return new VeriFastExpression.Fixpoint(fix.operation(), parameters);
  }

  public VeriFastExpression subVeriFastPredicate(VeriFastExpression.Predicate predicate) {
    Optional<VeriFastExpression.Variable> owner = predicate
        .owner()
        .map(this.varSubsitution::substitue);

    return new VeriFastExpression.Predicate(
        owner,
        predicate.predicateName(),
        predicate.arguments());
  }
}
