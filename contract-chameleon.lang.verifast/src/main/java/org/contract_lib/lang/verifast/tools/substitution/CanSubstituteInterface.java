package org.contract_lib.lang.verifast.tools.substitution;

import org.contract_lib.lang.verifast.ast.VeriFastExpression;

@FunctionalInterface
public interface CanSubstituteInterface<Element extends VeriFastExpression> {
  boolean canSubstitute(Element e);
}
