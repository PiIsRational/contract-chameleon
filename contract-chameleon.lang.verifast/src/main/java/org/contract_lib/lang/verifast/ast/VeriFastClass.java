package org.contract_lib.lang.verifast.ast;

import java.util.List;
import java.util.Optional;

public record VeriFastClass(
    String name,
    List<VeriFastPredicate> predicates,
    List<VeriFastConstructor> constructors,
    List<VeriFastMethod> methods,
    boolean isAbstract,
    Optional<String> implFrom,
    List<VeriFastComment> classBodyComments) {
}
