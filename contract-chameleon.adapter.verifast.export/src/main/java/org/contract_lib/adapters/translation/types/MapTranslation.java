package org.contract_lib.adapters.translation.types;

import java.util.List;

import org.contract_lib.adapters.translation.TypeTranslation;
import org.contract_lib.adapters.translation.TypeTranslator;
import org.contract_lib.lang.contract_lib.ast.Sort;

import org.contract_lib.lang.verifast.ast.VeriFastExpression;
import org.contract_lib.lang.verifast.ast.VeriFastType;

import com.google.auto.service.AutoService;

@AutoService(TypeTranslation.class)
public final class MapTranslation implements TypeTranslation {

  public Sort getClibSort() {
    return new Sort.Type("Map");
  }

  public VeriFastType getVerifastType(
      TypeTranslator translator,
      Sort sort) {

    List<Sort> inner = TypeTranslation.getInner(sort);
    if (inner.size() != 2) {
      System.err.println("two parameter for map expected");
    }
    return new VeriFastType.VeriFastInduction("list", List.of(
        new VeriFastType.VeriFastInduction("pair", List.of(
            translator.translate(inner.get(0)),
            translator.translate(inner.get(1))))));
  }

  public List<VeriFastExpression> getHelper() {
    return List.of();
  }

  public String getDefaultValue() {
    return "nil";
  }
}
