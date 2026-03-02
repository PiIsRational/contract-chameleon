package org.contract_lib.adapters.translation.types;

import java.util.List;

import org.contract_lib.adapters.translation.TypeTranslation;
import org.contract_lib.adapters.translation.TypeTranslator;
import org.contract_lib.lang.contract_lib.ast.Sort;

import org.contract_lib.lang.verifast.ast.VeriFastExpression;
import org.contract_lib.lang.verifast.ast.VeriFastType;

import com.google.auto.service.AutoService;

@AutoService(TypeTranslation.class)
public final class BoolTranslation implements TypeTranslation {

  public Sort getClibSort() {
    return new Sort.Type("Bool");
  }

  public VeriFastType getVerifastType(
      TypeTranslator translator,
      Sort sort) {
    return new VeriFastType.VeriFastBoolean();
  }

  public List<VeriFastExpression> getHelper() {
    return List.of();
  }

  public String getDefaultValue() {
    return "false";
  }
}
