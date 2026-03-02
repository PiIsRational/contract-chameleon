package org.contract_lib.adapters.translation.functions;

import java.util.List;

import org.contract_lib.adapters.translation.TermTranslation;
import org.contract_lib.adapters.translation.TermTranslationProvider;
import org.contract_lib.lang.contract_lib.ast.Sort;
import org.contract_lib.lang.contract_lib.ast.Term;
import org.contract_lib.lang.contract_lib.ast.Symbol;

import org.contract_lib.lang.verifast.ast.VeriFastType;

import com.google.auto.service.AutoService;

import static org.contract_lib.adapters.translation.TermTranslation.UnaryOperatorTranslation;
import static org.contract_lib.adapters.translation.TermTranslation.BinaryOperatorTranslation;

@AutoService(TermTranslationProvider.class)
public final record IntTermTranslation() implements TermTranslationProvider {

  public final static Sort.Type CLIB_INT = new Sort.Type("Int");
  public final static VeriFastType VERIFAST_INT = new VeriFastType.VeriFastInteger();

  public List<TermTranslation> getAll() {
    return List.of(
        /*
        new UnaryOperatorTranslation(
          new Term.Identifier.IdentifierValue(new Symbol("-")),
          "-",
          CLIB_INT,
          VERIFAST_INT,
          CLIB_INT,
          VERIFAST_INT
        ),*/

        new BinaryOperatorTranslation(
            new Term.Identifier.IdentifierValue(new Symbol("+")),
            "+",
            CLIB_INT,
            VERIFAST_INT,
            CLIB_INT,
            VERIFAST_INT),
        new BinaryOperatorTranslation(
            new Term.Identifier.IdentifierValue(new Symbol("-")),
            "-",
            CLIB_INT,
            VERIFAST_INT,
            CLIB_INT,
            VERIFAST_INT),
        new BinaryOperatorTranslation(
            new Term.Identifier.IdentifierValue(new Symbol("*")),
            "*",
            CLIB_INT,
            VERIFAST_INT,
            CLIB_INT,
            VERIFAST_INT),
        //TODO: Check trans
        new BinaryOperatorTranslation(
            new Term.Identifier.IdentifierValue(new Symbol("div")),
            "/",
            CLIB_INT,
            VERIFAST_INT,
            CLIB_INT,
            VERIFAST_INT),
        //TODO: Check trans
        new BinaryOperatorTranslation(
            new Term.Identifier.IdentifierValue(new Symbol("mod")),
            "%",
            CLIB_INT,
            VERIFAST_INT,
            CLIB_INT,
            VERIFAST_INT),

        new BinaryOperatorTranslation(
            new Term.Identifier.IdentifierValue(new Symbol("<")),
            "<",
            CLIB_INT,
            VERIFAST_INT,
            CLIB_INT,
            VERIFAST_INT),
        new BinaryOperatorTranslation(
            new Term.Identifier.IdentifierValue(new Symbol("<=")),
            "<=",
            CLIB_INT,
            VERIFAST_INT,
            CLIB_INT,
            VERIFAST_INT),
        new BinaryOperatorTranslation(
            new Term.Identifier.IdentifierValue(new Symbol(">")),
            ">",
            CLIB_INT,
            VERIFAST_INT,
            CLIB_INT,
            VERIFAST_INT),
        new BinaryOperatorTranslation(
            new Term.Identifier.IdentifierValue(new Symbol(">=")),
            ">=",
            CLIB_INT,
            VERIFAST_INT,
            CLIB_INT,
            VERIFAST_INT),

        new BinaryOperatorTranslation(
            new Term.Identifier.IdentifierValue(new Symbol("=")),
            "==",
            CLIB_INT,
            VERIFAST_INT,
            CLIB_INT,
            VERIFAST_INT),
        new BinaryOperatorTranslation(
            new Term.Identifier.IdentifierValue(new Symbol("distinct")),
            "!=",
            CLIB_INT,
            VERIFAST_INT,
            CLIB_INT,
            VERIFAST_INT));
  }
}
