
package org.contract_lib.lang.verifast.tools.substitution;

import java.util.List;
import java.util.Optional;

import org.contract_lib.contract_chameleon.contexts.MessageContext;
import org.contract_lib.lang.verifast.ast.VeriFastContract;
import org.contract_lib.lang.verifast.ast.VeriFastExpression;

// Note: There might be more general classes and implementation, but as they are far mor complicated,
// this simple implementation is enough for the moment.

public class VeriFastPredicateSubstitution {

  public VeriFastPredicateSubstitution(
      CanSubstituteInterface<VeriFastExpression.Predicate> canSubstitute,
      SubstitutionInterface<VeriFastExpression.Predicate> substitution,
      MessageContext messageContext) {

    this.canSubstitute = canSubstitute;
    this.subsitution = substitution;
    this.messageContext = messageContext;
  }

  SubstitutionInterface<VeriFastExpression.Predicate> subsitution;
  CanSubstituteInterface<VeriFastExpression.Predicate> canSubstitute;
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
    return var;
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
    if (!this.canSubstitute.canSubstitute(predicate)) {
      messageContext.logError(String.format("%s is not allowed to be substituted!", predicate));
    }
    return this.subsitution.substitue(predicate);
  }
}
