package org.contract_lib.lang.verifast.ast;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record VeriFastPredicate(
    String name,
    List<VeriFastArgument> arguments,
    Optional<VeriFastExpression> predicateDefinition,
    Optional<VeriFastComment.Inline> comment) implements VeriFastHelper {
  public <R> R perform(
      Function<VeriFastInduction, R> induction,
      Function<VeriFastPredicate, R> predicate,
      Function<VeriFastFixpoint, R> fixpoint) {
    return predicate.apply(this);
  }
}
