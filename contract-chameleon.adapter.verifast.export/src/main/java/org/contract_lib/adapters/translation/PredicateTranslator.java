package org.contract_lib.adapters.translation;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Optional;
import java.util.Collection;

import java.util.stream.Collectors;

import org.contract_lib.contract_chameleon.error.ChameleonMessageManager;

import org.contract_lib.lang.contract_lib.ast.Sort;
import org.contract_lib.lang.contract_lib.ast.Term;
import org.contract_lib.lang.contract_lib.ast.Constructor;
import org.contract_lib.lang.contract_lib.ast.SelectorDec;
import org.contract_lib.lang.contract_lib.ast.Argument;
import org.contract_lib.lang.contract_lib.ast.Symbol;

import org.contract_lib.lang.verifast.ast.VeriFastExpression;
import org.contract_lib.lang.verifast.ast.VeriFastArgument;
import org.contract_lib.lang.verifast.ast.VeriFastComment;
import org.contract_lib.lang.verifast.ast.VeriFastType;
import org.contract_lib.lang.verifast.ast.VeriFastExpression.Predicate;
import org.contract_lib.lang.verifast.ast.VeriFastPredicate;

public final class PredicateTranslator {

  private static final String PREDICATE_NAME = "pred";

  private final ChameleonMessageManager messageHandler;
  private final Set<PredicateTranslation> allowedPredicates;
  private final Map<String, List<VeriFastArgument>> predicates;
  private final TypeTranslator typeTranslator;
  private final List<VeriFastArgument> availableArguments;

  // This flag removes the this from this.pred(arg1, arg2),
  // so verifast can load it properly. Otherwise vf behaves quite bugy.
  private final boolean removeOwnerThis = true;

  public PredicateTranslator(
      ChameleonMessageManager messageHandler,
      TypeTranslator typeTranslator) {
    this.messageHandler = messageHandler;
    this.typeTranslator = typeTranslator;
    this.allowedPredicates = new HashSet<>();
    this.predicates = new HashMap<>();
    this.availableArguments = new ArrayList<>();
  }

  public List<VeriFastArgument> getAvailableArguments() {
    return new ArrayList<>(this.availableArguments);
  }

  public void clearAvailableArguments() {
    this.availableArguments.clear();
  }

  public VeriFastExpression.VariableAssignment generateVariableAssignment(PredicateTranslation translation) {
    this.allowedPredicates.add(translation);
    String name = translation.createName();
    return new VeriFastExpression.VariableAssignment(name);
  }

  public Optional<PredicateTranslation> translatePredicateOld(Term.Old old) {
    PredicateTranslation pt = translateTermOld(old.argument());
    String fieldName = pt.field();
    String owner = pt.owner();
    PredicateTranslation result = new PredicateTranslation(fieldName, owner, true);
    if (this.allowedPredicates.contains(result)) {
      return Optional.of(result);
    }
    {
      return Optional.empty();
    }
  }

  public Optional<PredicateTranslation> translatePredicate(Term.MethodApplication term, boolean accessOld) {
    PredicateTranslation result = translateTermMethodApplication(term);
    if (accessOld) {
      result = new PredicateTranslation(result.field(), result.owner(), true);
    }
    if (this.allowedPredicates.contains(result)) {
      return Optional.of(result);
    }
    {
      return Optional.empty();
    }
  }

  public List<VeriFastPredicate> translatePredicateDef(String owner, Constructor constructor,
      Optional<VeriFastComment.Inline> comment) {
    List<VeriFastArgument> selectors = constructor.selectors()
        .stream()
        .map(this::translateSelector)
        .collect(Collectors.toList());

    List<VeriFastArgument> args = this.predicates.getOrDefault(owner, new ArrayList<>());
    this.predicates.put(owner, args);
    args.addAll(selectors);

    //TODO: move to PredicateHandler
    return List.of(new VeriFastPredicate(
        PREDICATE_NAME,
        selectors,
        Optional.empty(),
        comment));
  }

  public List<VeriFastExpression> createPredicates(
      List<Argument> args,
      TermTranslator termTranslator,
      boolean assignment,
      boolean old) {
    return args.stream()
        .map((a) -> this.createPredicate(a, termTranslator, assignment, old))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
  }

  private PredicateTranslation createTranslation(
      String owner,
      VeriFastArgument arg,
      boolean old) {
    PredicateTranslation t = new PredicateTranslation(arg.name(), owner, old);
    this.availableArguments.add(new VeriFastArgument(arg.type(), t.createName()));
    return t;
  }

  public Optional<VeriFastExpression> createPredicate(
      Argument args,
      TermTranslator termTranslator,
      boolean assignment,
      boolean old) {

    String owner = args.identifier();
    Sort sort = args.sort();
    VeriFastType vfType = this.typeTranslator.translate(sort);
    String type = sort.getName();

    if (!this.predicates.containsKey(type)) {
      this.createNewConstantArgument(termTranslator, owner, sort, vfType);
      return Optional.empty();
    }

    if (assignment) {
      return Optional.of(
          this.removeThisOwner(
              new VeriFastExpression.Predicate(
                  Optional.of(new VeriFastExpression.Variable(owner)),
                  PREDICATE_NAME,
                  this.predicates.get(type)
                      .stream()
                      .map((field) -> this.createTranslation(owner, field, old))
                      .map(this::generateVariableAssignment)
                      .collect(Collectors.toList()))));
    } else {
      return Optional.of(
          this.removeThisOwner(
              new VeriFastExpression.Predicate(
                  Optional.of(new VeriFastExpression.Variable(owner)),
                  PREDICATE_NAME,
                  this.predicates.get(type)
                      .stream()
                      .map((field) -> this.createTranslation(owner, field, old))

                      .map(PredicateTranslation::generateVariable)
                      .collect(Collectors.toList()))));
    }
  }

  private void createNewConstantArgument(
      TermTranslator termTranslator,
      String name,
      Sort sort,
      VeriFastType vfType) {
    //Add constant for value types
    termTranslator.addTranslation(
        new TermTranslation.ConstantTranslation(
            new Term.Identifier.IdentifierValue(new Symbol(name)),
            name,
            sort,
            vfType));
    this.availableArguments.add(new VeriFastArgument(vfType, name));
  }

  public VeriFastArgument translateSelector(SelectorDec selector) {
    //TODO: handle error if type does not exist
    VeriFastType type = this.typeTranslator.translate(selector.sort());
    String name = selector.symbol().identifier();

    return new VeriFastArgument(
        type,
        name);
  }

  private PredicateTranslation translateTermOld(Term term) {
    return term.perform(
        this::translateError,
        this::translateError,
        this::translateError,
        this::translateTermMethodApplication,
        this::translateError,
        this::translateError,
        this::translateError,
        this::translateError,
        this::translateError,
        this::translateError,
        this::translateError);
  }

  private PredicateTranslation translateTermMethodApplication(Term.MethodApplication term) {
    String fieldName = term.identifier().getValue().identifier().identifier();
    if (term.parameters().size() != 1) {
      //TODO: hanlde error
      System.err.println("Predicates can only have one owner (parameter), term: " + term);
    }
    String owner = translateTerm(term.parameters().get(0));
    return new PredicateTranslation(fieldName, owner, false);
  }

  private String translateTerm(Term term) {
    return term.perform(
        this::translateError,
        this::translateTermIdentifierAs,
        this::translateTermIdentifierValue,
        this::translateError,
        this::translateError,
        this::translateError,
        this::translateError,
        this::translateError,
        this::translateError,
        this::translateError,
        this::translateError);
  }

  private String translateTermIdentifierValue(Term.Identifier.IdentifierValue term) {
    return term.getValue().identifier().identifier();
  }

  private String translateTermIdentifierAs(Term.Identifier.IdentifierAs term) {
    return translateTermIdentifierValue(term.identifier());
  }

  private <T extends Term, R> R translateError(T term) {
    System.err.println("The term cannot be translated, as it is defined as a predicate: " + term);
    //TODO: Create error message
    return null;
  }

  private Predicate removeThisOwner(Predicate predicate) {
    if (removeOwnerThis && checkHasThisOwner(predicate)) {
      return new Predicate(Optional.empty(), predicate.predicateName(), predicate.arguments());
    }
    return predicate;
  }

  private boolean checkHasThisOwner(Predicate predicate) {
    return predicate.owner()
        .map((o) -> o.variable().equals("this"))
        .orElse(false);
  }
}
