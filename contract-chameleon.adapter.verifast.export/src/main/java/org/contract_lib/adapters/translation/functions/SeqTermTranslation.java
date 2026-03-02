
package org.contract_lib.adapters.translation.functions;

import java.beans.Expression;
import java.util.List;

import org.contract_lib.adapters.translation.TermTranslation;
import org.contract_lib.adapters.translation.TermTranslationProvider;
import org.contract_lib.lang.contract_lib.ast.Sort;
import org.contract_lib.lang.contract_lib.ast.Term;
import org.contract_lib.lang.contract_lib.ast.Symbol;
import org.contract_lib.lang.verifast.ast.VeriFastExpression;
import org.contract_lib.lang.verifast.ast.VeriFastType;

import com.google.auto.service.AutoService;

import static org.contract_lib.adapters.translation.TermTranslation.UnaryOperatorTranslation;
import static org.contract_lib.adapters.translation.TermTranslation.BinaryOperatorTranslation;
import static org.contract_lib.adapters.translation.TermTranslation.FixpointOperatorTranslation;
import static org.contract_lib.adapters.translation.TermTranslation.ConstantTranslation;
import static org.contract_lib.adapters.translation.TermTranslation.VeriFastExpressionTranslation;

@AutoService(TermTranslationProvider.class)
public final record SeqTermTranslation() implements TermTranslationProvider {

  public final static Sort.Type CLIB_BOOLEAN = new Sort.Type("Bool");
  public final static Sort.Type CLIB_INT = new Sort.Type("Int");
  public final static Sort.Type CLIB_SEQ = new Sort.Type("Seq");
  public final static Sort.Type CLIB_GEN = new Sort.Type("A");
  public final static VeriFastType VERIFAST_BOOLEAN = new VeriFastType.VeriFastBoolean();
  public final static VeriFastType VERIFAST_INT = new VeriFastType.VeriFastInteger();
  public final static VeriFastType VERIFAST_SEQ = new VeriFastType.VeriFastInduction("list",
      List.of(new VeriFastType.VeriFastClass("A", List.of())));
  public final static VeriFastType VERIFAST_GEN = new VeriFastType.VeriFastClass("A", List.of());

  public List<TermTranslation> getAll() {
    return List.of(
        new ConstantTranslation(
            new Term.Identifier.IdentifierValue(new Symbol("seq.empty")),
            "nil",
            CLIB_SEQ,
            VERIFAST_SEQ),
        new VeriFastExpressionTranslation(
            new Term.Identifier.IdentifierValue(new Symbol("seq.unit")),
            (args) -> {
              if (args.size() != 1) {
                System.err.println("expected arguments (list, index) for seq unit");
                return new VeriFastExpression.BooleanValue(false);
              } else {
                return new VeriFastExpression.Fixpoint("cons",
                    List.of(args.get(0), new VeriFastExpression.Variable("nil")));
              }
            },
            List.of(CLIB_GEN),
            List.of(VERIFAST_GEN),
            CLIB_SEQ,
            VERIFAST_SEQ),
        new VeriFastExpressionTranslation(
            new Term.Identifier.IdentifierValue(new Symbol("seq.at")),
            (args) -> {
              if (args.size() != 2) {
                System.err.println("expected arguments (list, index) for seq at");
                return new VeriFastExpression.BooleanValue(false);
              } else {
                return new VeriFastExpression.Fixpoint("cons",
                    List.of(new VeriFastExpression.Fixpoint("nth", List.of(args.get(1), args.get(0))),
                        new VeriFastExpression.Variable("nil")));
              }
            },
            List.of(CLIB_SEQ, CLIB_INT),
            List.of(VERIFAST_SEQ, VERIFAST_INT),
            CLIB_SEQ,
            VERIFAST_SEQ),
        new VeriFastExpressionTranslation(
            new Term.Identifier.IdentifierValue(new Symbol("seq.extract")),
            (args) -> {
              if (args.size() != 3) {
                System.err.println("expected arguments (list, index, index) for seq extract");
                return new VeriFastExpression.BooleanValue(false);
              } else {
                return new VeriFastExpression.Fixpoint("take", List.of( //2. take of length j
                    args.get(2),
                    new VeriFastExpression.Fixpoint("drop", List.of(//1. drop the first i elements
                        args.get(1), args.get(0)))));
              }
            },
            List.of(CLIB_SEQ, CLIB_INT, CLIB_INT),
            List.of(VERIFAST_SEQ, VERIFAST_INT, VERIFAST_INT),
            CLIB_SEQ,
            VERIFAST_SEQ),
        new FixpointOperatorTranslation(
            new Term.Identifier.IdentifierValue(new Symbol("seq.len")),
            "length",
            List.of(CLIB_SEQ),
            List.of(VERIFAST_SEQ),
            CLIB_INT,
            VERIFAST_INT),
        new VeriFastExpressionTranslation(
            new Term.Identifier.IdentifierValue(new Symbol("seq.nth")),
            (args) -> {
              if (args.size() != 2) {
                System.err.println("expected arguments (list, index) for seq nth");
                return new VeriFastExpression.BooleanValue(false);
              } else {
                return new VeriFastExpression.Fixpoint("nth", List.of(args.get(1), args.get(0)));
              }
            },
            List.of(CLIB_BOOLEAN, CLIB_BOOLEAN),
            List.of(VERIFAST_BOOLEAN, VERIFAST_BOOLEAN),
            CLIB_BOOLEAN,
            VERIFAST_BOOLEAN),
        new VeriFastExpressionTranslation(
            new Term.Identifier.IdentifierValue(new Symbol("seq.indexof")),
            (args) -> {
              if (args.size() != 2) {
                System.err.println("expected arguments (list, t) for seq indesxof");
                return new VeriFastExpression.BooleanValue(false);
              } else {
                return new VeriFastExpression.Fixpoint("index_of", List.of(args.get(1), args.get(0)));
              }
            },
            List.of(CLIB_SEQ, CLIB_GEN),
            List.of(VERIFAST_SEQ, VERIFAST_GEN),
            CLIB_INT,
            VERIFAST_INT),
        new VeriFastExpressionTranslation(
            new Term.Identifier.IdentifierValue(new Symbol("seq.update")),
            (args) -> {
              if (args.size() != 3) {
                System.err.println("expected arguments (list, index, t) for seq update");
                return new VeriFastExpression.BooleanValue(false);
              } else {
                return new VeriFastExpression.Fixpoint("update_range",
                    List.of(args.get(2), args.get(1), args.get(0)));
              }
            },
            List.of(CLIB_SEQ, CLIB_INT, CLIB_GEN),
            List.of(VERIFAST_SEQ, VERIFAST_INT, VERIFAST_GEN),
            CLIB_SEQ,
            VERIFAST_SEQ),
        new FixpointOperatorTranslation(
            new Term.Identifier.IdentifierValue(new Symbol("seq.contains")),
            "contains",
            List.of(CLIB_SEQ, CLIB_BOOLEAN),
            List.of(VERIFAST_BOOLEAN, VERIFAST_BOOLEAN),
            CLIB_BOOLEAN,
            VERIFAST_BOOLEAN),
        new FixpointOperatorTranslation(
            new Term.Identifier.IdentifierValue(new Symbol("seq.++")),
            "append",
            List.of(CLIB_SEQ, CLIB_SEQ),
            List.of(VERIFAST_SEQ, VERIFAST_SEQ),
            CLIB_SEQ,
            VERIFAST_SEQ));
  }
}
