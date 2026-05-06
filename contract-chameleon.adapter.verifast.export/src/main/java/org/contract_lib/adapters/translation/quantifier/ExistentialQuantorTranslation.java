package org.contract_lib.adapters.translation.quantifier;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.contract_lib.lang.contract_lib.ast.Term;
import org.contract_lib.lang.contract_lib.ast.SortedVar;
import org.contract_lib.lang.contract_lib.ast.Quantor;
import org.contract_lib.adapters.translation.HelperTranslator;
import org.contract_lib.adapters.translation.PredicateTranslator;
import org.contract_lib.adapters.translation.QuantorTranslation;
import org.contract_lib.adapters.translation.TermTranslator;
import org.contract_lib.adapters.translation.TypeTranslator;
import org.contract_lib.lang.contract_lib.ast.Argument;
import org.contract_lib.lang.contract_lib.ast.Sort;

import org.contract_lib.lang.verifast.ast.VeriFastExpression;
import org.contract_lib.lang.verifast.ast.VeriFastPredicate;
import org.contract_lib.lang.verifast.ast.VeriFastType;
import org.contract_lib.lang.verifast.ast.VeriFastArgument;

public final record ExistentialQuantorTranslation() implements QuantorTranslation {

  private static final String EXISTS_QUANTOR_PREDICATE_NAME = "exists";
  private static final String RES_SUFFIX = "_res";
  private static final VeriFastType BOOLEAN_TYPE = new VeriFastType.VeriFastBoolean();

  @Override
  public Optional<VeriFastExpression> translate(
      List<VeriFastExpression> predicateList,
      Term.QuantorBinding quantorTerm,
      TermTranslator termTranslator,
      TypeTranslator typeTranslator,
      PredicateTranslator predTranslator,
      HelperTranslator helperTranslator) {
    if (!quantorTerm.quantor().equals(Quantor.EXISTS)) {
      return Optional.empty();
    }

    String name = helperTranslator.getNewPredicateName();
    List<VeriFastArgument> arguments = predTranslator.getAvailableArguments();

    List<VeriFastExpression> res = new ArrayList<>();

    List<VeriFastExpression> formalsDef = mapFormals(
        quantorTerm.formals(),
        termTranslator,
        typeTranslator,
        predTranslator);
    res.addAll(formalsDef);

    VeriFastExpression body = termTranslator.translateTerm(res, quantorTerm.term());

    res.add(new VeriFastExpression.BinaryOperation("==", new VeriFastExpression.Variable("true"), body));

    //Remove formals

    VeriFastExpression predicateExpression = new VeriFastExpression.Chain(res);

    List<VeriFastExpression> applArguments = new ArrayList<>(
        arguments.stream()
            .map(VeriFastArgument::name)
            .map(VeriFastExpression.Variable::new)
            .collect(Collectors.toList()));

    arguments.add(new VeriFastArgument(BOOLEAN_TYPE, name + RES_SUFFIX));
    applArguments.add(new VeriFastExpression.VariableAssignment(name + RES_SUFFIX));

    VeriFastPredicate pred = new VeriFastPredicate(
        name,
        arguments,
        Optional.of(predicateExpression), Optional.empty());

    helperTranslator.addHelper(pred);
    predicateList.add(new VeriFastExpression.Predicate(
        Optional.empty(),
        name,
        applArguments));

    //TODO:
    //- create induciton from constant argument list
    //- create custom predicate

    return Optional.of(
        new VeriFastExpression.Variable(name + RES_SUFFIX));
    //Optional.of(new VeriFastExpression.Predicate(
    //);
  }

  private List<VeriFastExpression> mapFormals(
      List<SortedVar> formals,
      TermTranslator termTranslator,
      TypeTranslator typeTranslator,
      PredicateTranslator predTranslator) {
    return formals.stream()
        .flatMap(f -> this.mapFormal(f, termTranslator, typeTranslator, predTranslator))
        .collect(Collectors.toList());
  }

  private Stream<VeriFastExpression> mapFormal(
      SortedVar formal,
      TermTranslator termTranslator,
      TypeTranslator typeTranslator,
      PredicateTranslator predTranslator) {
    Sort sort = formal.sort();
    String name = formal.symbol().identifier();
    VeriFastType type = typeTranslator.translate(sort);

    Optional<VeriFastExpression> predOpen = predTranslator.createPredicate(
        new Argument(sort, name),
        termTranslator,
        true,
        false);

    //TODO: 
    // - translate sort
    // - create constants depending on sort

    return Stream.of(new VeriFastExpression.Predicate(
        Optional.empty(),
        EXISTS_QUANTOR_PREDICATE_NAME,
        List.of(
            new VeriFastExpression.VariableAssignment(name))));
  }
}
