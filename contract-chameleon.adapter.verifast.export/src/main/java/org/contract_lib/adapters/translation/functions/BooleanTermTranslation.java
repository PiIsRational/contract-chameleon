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
import static org.contract_lib.adapters.translation.TermTranslation.FixpointOperatorTranslation;

@AutoService(TermTranslationProvider.class)
public final record BooleanTermTranslation() implements TermTranslationProvider {

  public final static Sort.Type CLIB_BOOLEAN = new Sort.Type("Bool");
  public final static VeriFastType VERIFAST_BOOLEAN = new VeriFastType.VeriFastBoolean();

  public List<TermTranslation> getAll() {
    return List.of(

        //These is defined through an helper
        new FixpointOperatorTranslation(
            new Term.Identifier.IdentifierValue(new Symbol("=>")),
            "implies",
            List.of(CLIB_BOOLEAN, CLIB_BOOLEAN),
            List.of(VERIFAST_BOOLEAN, VERIFAST_BOOLEAN),
            CLIB_BOOLEAN,
            VERIFAST_BOOLEAN),
        new FixpointOperatorTranslation(
            new Term.Identifier.IdentifierValue(new Symbol("xor")),
            "xor",
            List.of(CLIB_BOOLEAN, CLIB_BOOLEAN),
            List.of(VERIFAST_BOOLEAN, VERIFAST_BOOLEAN),
            CLIB_BOOLEAN,
            VERIFAST_BOOLEAN),

        new UnaryOperatorTranslation(
            new Term.Identifier.IdentifierValue(new Symbol("not")),
            "!",
            CLIB_BOOLEAN,
            VERIFAST_BOOLEAN,
            CLIB_BOOLEAN,
            VERIFAST_BOOLEAN),
        new BinaryOperatorTranslation(
            new Term.Identifier.IdentifierValue(new Symbol("and")),
            "&&",
            CLIB_BOOLEAN,
            VERIFAST_BOOLEAN,
            CLIB_BOOLEAN,
            VERIFAST_BOOLEAN),
        new BinaryOperatorTranslation(
            new Term.Identifier.IdentifierValue(new Symbol("or")),
            "||",
            CLIB_BOOLEAN,
            VERIFAST_BOOLEAN,
            CLIB_BOOLEAN,
            VERIFAST_BOOLEAN),

        new BinaryOperatorTranslation(
            new Term.Identifier.IdentifierValue(new Symbol("=")),
            "==",
            CLIB_BOOLEAN,
            VERIFAST_BOOLEAN,
            CLIB_BOOLEAN,
            VERIFAST_BOOLEAN),
        new BinaryOperatorTranslation(
            new Term.Identifier.IdentifierValue(new Symbol("distinct")),
            "!=",
            CLIB_BOOLEAN,
            VERIFAST_BOOLEAN,
            CLIB_BOOLEAN,
            VERIFAST_BOOLEAN));
  }
}
