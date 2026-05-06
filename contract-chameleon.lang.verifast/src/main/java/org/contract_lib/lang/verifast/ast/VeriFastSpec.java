package org.contract_lib.lang.verifast.ast;

import java.util.Optional;

public record VeriFastSpec(
    String packageName,
    VeriFastClass classEntity,
    Optional<VeriFastComment> comment) {
}
