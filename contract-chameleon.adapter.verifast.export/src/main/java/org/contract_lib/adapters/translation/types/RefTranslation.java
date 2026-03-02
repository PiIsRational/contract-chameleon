package org.contract_lib.adapters.translation.types;

import java.util.List;

import org.contract_lib.adapters.translation.TypeTranslation;
import org.contract_lib.adapters.translation.TypeTranslator;
import org.contract_lib.lang.contract_lib.ast.Sort;

import org.contract_lib.lang.verifast.ast.VeriFastExpression;
import org.contract_lib.lang.verifast.ast.VeriFastType;

import com.google.auto.service.AutoService;

@AutoService(TypeTranslation.class)
public final class RefTranslation implements TypeTranslation {

  public Sort getClibSort() {
    return new Sort.Type("Ref");
  }

  public VeriFastType getVerifastType(
      TypeTranslator translator,
      Sort sort) {
    List<Sort> inner = TypeTranslation.getInner(sort);
    if (inner.size() != 1) {
      System.err.println("only one parameter for Ref expected");
    }
    return translator.translate(inner.get(0));
  }

  public List<VeriFastExpression> getHelper() {
    return List.of();
  }

  public String getDefaultValue() {
    return "null";
  }
}
