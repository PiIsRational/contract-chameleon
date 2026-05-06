package org.contract_lib.lang.verifast.tools.substitution;

import org.contract_lib.lang.verifast.ast.VeriFastExpression;

@FunctionalInterface
public interface SubstitutionInterface<Element extends VeriFastExpression> {

  Element substitue(Element e);
}
