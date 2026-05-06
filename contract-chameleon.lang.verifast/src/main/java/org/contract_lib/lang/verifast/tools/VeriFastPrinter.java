package org.contract_lib.lang.verifast.tools;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.contract_lib.lang.verifast.ast.VeriFastSpec;
import org.contract_lib.lang.verifast.ast.VeriFastPredicate;
import org.contract_lib.lang.verifast.ast.VeriFastType;
import org.contract_lib.lang.verifast.ast.VeriFastArgument;
import org.contract_lib.lang.verifast.ast.VeriFastClass;
import org.contract_lib.lang.verifast.ast.VeriFastComment;
import org.contract_lib.lang.verifast.ast.VeriFastContract;
import org.contract_lib.lang.verifast.ast.VeriFastExpression;
import org.contract_lib.lang.verifast.ast.VeriFastMethod;
import org.contract_lib.lang.verifast.ast.VeriFastConstructor;

import org.contract_lib.lang.verifast.ast.VeriFastHelperSpecification;
import org.contract_lib.lang.verifast.ast.VeriFastHelper;
import org.contract_lib.lang.verifast.ast.VeriFastInduction;
import org.contract_lib.lang.verifast.ast.VeriFastInductionConstructor;
import org.contract_lib.lang.verifast.ast.VeriFastJavaExpression;
import org.contract_lib.lang.verifast.ast.VeriFastJavaStatement;
import org.contract_lib.lang.verifast.ast.VeriFastFixpoint;

public class VeriFastPrinter {

  private static final int INDENTATION_STEPS = 4;

  private static final String INLINE_COMMENT_OPEN = "//";
  private static final String COMMENT_OPEN_BLOCK = "/*";
  private static final String COMMENT_CLOSE_BLOCK = "*/";

  private static final String VERIFAST_OPEN = "//@";
  private static final String VERIFAST_OPEN_BLOCK = "/*@";
  private static final String VERIFAST_CLOSE_BLOCK = "@*/";
  private static final String EXPRESSION_CHAIN_JOIN = "&*&";
  private static final String ASSIGNMENT_OPERATOR = "?";
  private static final String DEFINITION_OPERATOR = "=";
  private static final String CONSTRUCTOR_OPERATOR = "|";

  private static final String REQUIRES = "requires";
  private static final String ENSURES = "ensures";

  private static final String PREDICATE = "predicate";
  private static final String INDUCTION = "induction";
  private static final String FIXPOINT = "fixpoint";

  private static final String CLASS = "class";
  private static final String PACKAGE = "package";

  private static final String ABSTRACT = "abstract";

  private static final String STATIC = "static";
  private static final String PUBLIC = "public";

  private static final String EXTENDS = "extends";

  private static final String VOID = "void";
  private static final String RETURN = "return";
  private static final String NEW = "new";

  private static final String SEMICOLON = ";";
  private static final String COLON = ",";
  private static final String DOT = ".";

  private static final String SPACE = " ";
  private static final String BRACKET_OPEN = "(";
  private static final String BRACKET_CLOSE = ")";
  private static final String CURLY_BRACKET_OPEN = "{";
  private static final String CURLY_BRACKET_CLOSE = "}";

  /// The name of the class, which content is printed at the moment.
  private String className;

  public VeriFastPrinter(Writer writer) {
    this.writer = writer;
    this.indentation = 0;
  }

  private final Writer writer;
  private int indentation;

  public void printVeriFastSpec(VeriFastSpec spec) {
    print(PACKAGE);
    print(SPACE);
    print(spec.packageName());
    print(SEMICOLON);
    printNewLine();
    //TODO: Add import statements
    printNewLine();
    printVeriFastClass(spec.classEntity());

    printOptional(spec.comment(), (comment) -> {
      printNewLine();
      this.printVeriFastComment(comment);
    });
  }

  public void printVeriFastClass(VeriFastClass vfClass) {
    print(PUBLIC);
    print(SPACE);
    if (vfClass.isAbstract()) {
      print(ABSTRACT);
      print(SPACE);
    }
    print(CLASS);
    print(SPACE);
    print(vfClass.name());
    vfClass.implFrom().ifPresent((p) -> {
      print(SPACE);
      print(EXTENDS);
      print(SPACE);
      print(p);
    });
    print(SPACE);
    printBlock(() -> {
      this.className = vfClass.name();
      printNewLine();
      printList(vfClass.classBodyComments(), () -> printNewLine(), this::printVeriFastComment);
      printNewLine();
      printList(vfClass.predicates(), () -> printNewLine(), this::printVeriFastPredicate);
      printNewLine();
      printList(vfClass.constructors(), () -> printNewLine(), this::printVeriFastConstructor);
      printNewLine();
      printList(vfClass.methods(), () -> printNewLine(), this::printVeriFastMethod);
    });
  }

  public void printVeriFastPredicate(VeriFastPredicate pred) {
    printIndentation();
    print(VERIFAST_OPEN);
    print(SPACE);
    print(PREDICATE);
    print(SPACE);
    print(pred.name());
    print(BRACKET_OPEN);
    printColonList(pred.arguments(), this::printVeriFastArgument);
    print(BRACKET_CLOSE);
    printOptional(pred.comment(), this::printVeriFastCommentInline);
    print(SEMICOLON);
    printNewLine();
  }

  public void printVeriFastConstructor(VeriFastConstructor cons) {
    printIndentation();
    print(this.className);
    print(BRACKET_OPEN);
    printColonList(cons.arguments(), this::printVeriFastArgument);
    print(BRACKET_CLOSE);
    printNewLine();
    printVeriFastContract(cons.contract());
    printIndentation();
    printBlock(() -> {
      for (VeriFastJavaExpression vfExpr : cons.body()) {
        printIndentation();
        printJavaExpr(vfExpr);
        printNewLine();
      }
    });
  }

  public void printVeriFastType(VeriFastType type) {
    print(type.getName());
  }

  public void printVeriFastMethod(VeriFastMethod method) {
    printIndentation();
    print(PUBLIC);
    print(SPACE);
    if (!method.body().isPresent()) {
      print(ABSTRACT);
      print(SPACE);
    }
    if (method.isStatic()) {
      print(STATIC);
      print(SPACE);
    }
    method.resultType().ifPresent(this::printVeriFastType);
    if (!method.resultType().isPresent()) {
      print(VOID);
    }
    print(SPACE);
    print(method.name());
    print(BRACKET_OPEN);
    printColonList(method.arguments(), this::printVeriFastArgument);
    print(BRACKET_CLOSE);
    if (method.body().isPresent()) {
      printNewLine();
      printVeriFastContract(method.contract());
      printIndentation();
      printBlock(() -> {
        for (VeriFastJavaExpression expr : method.body().get()) {
          printIndentation();
          printJavaExpr(expr);
          printNewLine();
        }
      });
    } else {
      print(SEMICOLON);
      printNewLine();
      printVeriFastContract(method.contract());
    }
  }

  public void printVeriFastContract(VeriFastContract spec) {
    printIndentation();
    print(VERIFAST_OPEN);
    print(SPACE);
    print(REQUIRES);
    print(SPACE);
    printVeriFastExpression(spec.requires());
    print(SEMICOLON);
    printNewLine();
    printIndentation();
    print(VERIFAST_OPEN);
    print(SPACE);
    print(ENSURES);
    print(SPACE);
    printVeriFastExpression(spec.ensures());
    print(SEMICOLON);
    printNewLine();
  }

  // MARK: - Java Expressions

  public void printJavaExpr(VeriFastJavaExpression expression) {
    expression.<Void>perform(
        this::printJavaExpressionStandard,
        this::printJavaExpressionReturn,
        this::printJavaExpressionSimpleStatement);
  }

  public Void printJavaExpressionStandard(VeriFastJavaExpression.Standard simpleStatement) {
    printJavaStatement(simpleStatement.statement());
    print(SEMICOLON);
    return null;
  }

  public Void printJavaExpressionReturn(VeriFastJavaExpression.Return returnExpression) {
    print(RETURN);
    print(SPACE);
    printJavaStatement(returnExpression.statement());
    print(SEMICOLON);
    return null;
  }

  public Void printJavaExpressionSimpleStatement(VeriFastJavaExpression.SimpleStatement simpleStatement) {
    print(simpleStatement.statement());
    return null;
  }

  // MARK: - Java Statements

  public void printJavaStatement(VeriFastJavaStatement statement) {
    statement.<Void>perform(
        this::printConstructorCall,
        this::printDefaultStatment);
  }

  public Void printConstructorCall(VeriFastJavaStatement.ConstructorCall consCall) {
    print(NEW);
    print(SPACE);
    printVeriFastType(consCall.classType());
    print(BRACKET_OPEN);
    printColonList(consCall.arguments(), this::printVeriFastArgumentCall);
    print(BRACKET_CLOSE);
    return null;
  }

  public Void printDefaultStatment(VeriFastJavaStatement.DefaultValue defaultValue) {
    print("defaultValue.type");
    System.err.println("ERROR: Default value for type printer not supported yet.");
    return null;
  }

  // MARK: - Helper

  public void printVeriFastArgument(VeriFastArgument argument) {
    printVeriFastType(argument.type());
    print(SPACE);
    print(argument.name());
  }

  public void printVeriFastArgumentCall(VeriFastArgument argument) {
    print(argument.name());
  }

  public void printVeriFastExpression(VeriFastExpression expr) {
    expr.<Void>perform(
        this::printVeriFastExpressionChain,
        this::printVeriFastExpressionBoolValue,
        this::printVeriFastExpressionIntegerValue,
        this::printVeriFastExpressionPredicate,
        this::printVeriFastExpressionVariable,
        this::printVeriFastExpressionVariableAssignment,
        this::printVeriFastExpressionBinaryOperation,
        this::printVeriFastFixpoint);
  }

  public Void printVeriFastExpressionChain(VeriFastExpression.Chain expr) {
    printList(
        expr.expressions(),
        () -> {
          print(SPACE);
          print(EXPRESSION_CHAIN_JOIN);
          print(SPACE);
        },
        this::printVeriFastExpression);
    return null;
  }

  public Void printVeriFastExpressionIntegerValue(VeriFastExpression.IntegerValue expr) {
    print(String.valueOf(expr.value()));
    return null;
  }

  public Void printVeriFastExpressionBoolValue(VeriFastExpression.BooleanValue expr) {
    print(String.valueOf(expr.value()));
    return null;
  }

  public Void printVeriFastExpressionPredicate(VeriFastExpression.Predicate expr) {
    expr.owner().ifPresent((o) -> {
      printVeriFastExpressionVariable(o);
      print(DOT);
    });
    print(expr.predicateName());
    print(BRACKET_OPEN);
    printColonList(expr.arguments(), this::printVeriFastExpression);
    print(BRACKET_CLOSE);
    return null;
  }

  public Void printVeriFastExpressionVariable(VeriFastExpression.Variable expr) {
    print(expr.variable());
    return null;
  }

  public Void printVeriFastExpressionVariableAssignment(VeriFastExpression.VariableAssignment expr) {
    print(ASSIGNMENT_OPERATOR);
    print(expr.variable());
    return null;
  }

  public Void printVeriFastExpressionBinaryOperation(VeriFastExpression.BinaryOperation expr) {
    print(BRACKET_OPEN);
    printVeriFastExpression(expr.left());
    print(BRACKET_CLOSE);
    print(SPACE);
    print(expr.operation());
    print(SPACE);
    print(BRACKET_OPEN);
    printVeriFastExpression(expr.right());
    print(BRACKET_CLOSE);
    return null;
  }

  public Void printVeriFastFixpoint(VeriFastExpression.Fixpoint expr) {
    print(expr.operation());
    print(BRACKET_OPEN);
    printColonList(expr.parameters(), this::printVeriFastExpression);
    print(BRACKET_CLOSE);
    return null;
  }

  // - Helper Definition

  public void printVeriFastHelperSpecification(VeriFastHelperSpecification spec) {
    print(VERIFAST_OPEN_BLOCK);
    printNewLine();
    printNewLine();

    printList(
        spec.definitions(),
        () -> printNewLine(),
        this::printVeriFastHelperDefinition);

    printNewLine();
    print(VERIFAST_CLOSE_BLOCK);
    printNewLine();

  }

  public Void printVeriFastHelperDefinition(VeriFastHelper definition) {
    return definition.perform(
        this::printVeriFastInduction,
        this::printVeriFastPredicateDefinition,
        this::printVeriFastFixpoint);
  }

  public Void printVeriFastPredicateDefinition(VeriFastPredicate definition) {
    print(PREDICATE);
    print(SPACE);
    print(definition.name());
    print(BRACKET_OPEN);
    printNewLine();
    increaseIndentation();
    printIndentation();
    printListNewLine(definition.arguments(), this::printVeriFastArgument);
    decreaseIndentation();
    printNewLine();
    print(BRACKET_CLOSE);
    definition.predicateDefinition().ifPresent((def) -> {
      print(SPACE);
      print(DEFINITION_OPERATOR);
      print(SPACE);
      printVeriFastExpression(def);
      print(SPACE);
    });
    print(SEMICOLON);
    printNewLine();
    return null;
  }

  public Void printVeriFastFixpoint(VeriFastFixpoint definition) {
    print(FIXPOINT);
    print(SPACE);
    printVeriFastType(definition.returnType());
    print(SPACE);
    print(definition.name());
    print(BRACKET_OPEN);
    printColonList(definition.arguments(), this::printVeriFastArgument);
    print(BRACKET_CLOSE);
    print(SPACE);
    printBlock(() -> {
      printIndentation();
      print(RETURN);
      print(SPACE);
      printVeriFastExpression(definition.definition());
      print(SEMICOLON);
      printNewLine();
    });
    printNewLine();
    return null;
  }

  public Void printVeriFastInduction(VeriFastInduction definition) {
    print(INDUCTION);
    print(SPACE);
    print(definition.name());
    print(SPACE);
    print(DEFINITION_OPERATOR);
    print(SPACE);
    printList(
        definition.constructors(),
        () -> {
          print(SPACE);
          print(CONSTRUCTOR_OPERATOR);
          print(SPACE);
        },
        this::printVeriFastInductionConstructor);
    print(SEMICOLON);
    return null;
  }

  public Void printVeriFastInductionConstructor(VeriFastInductionConstructor definition) {
    print(definition.name());
    print(BRACKET_OPEN);
    printColonList(definition.parameters(), this::printVeriFastType);
    print(BRACKET_CLOSE);
    return null;
  }

  public Void printVeriFastComment(VeriFastComment comment) {
    return comment.perform(
        this::printVeriFastCommentNoEscaping,
        this::printVeriFastCommentSingleLine,
        this::printVeriFastCommentInline,
        this::printVeriFastCommentMultiline,
        this::printVeriFastCommentEndline);
  }

  public Void printVeriFastCommentNoEscaping(VeriFastComment.NoEscaping comment) {
    printIndentation();
    print(comment.commentBody());
    printNewLine();
    return null;
  }

  public Void printVeriFastCommentSingleLine(VeriFastComment.SingleLine comment) {
    printIndentation();
    print(INLINE_COMMENT_OPEN);
    print(SPACE);
    print(comment.commentBody());
    printNewLine();
    return null;
  }

  public Void printVeriFastCommentInline(VeriFastComment.Inline comment) {
    print(SPACE);
    print(COMMENT_OPEN_BLOCK);
    print(SPACE);
    print(comment.commentBody());
    print(SPACE);
    print(COMMENT_CLOSE_BLOCK);
    return null;
  }

  public Void printVeriFastCommentMultiline(VeriFastComment.Multiline comment) {
    printIndentation();
    System.err.println("EndLine Comments not fully supported yet");
    print(COMMENT_OPEN_BLOCK);
    print(SPACE);
    print(comment.commentBody());
    print(COMMENT_CLOSE_BLOCK);
    return null;
  }

  public Void printVeriFastCommentEndline(VeriFastComment.EndLine comment) {
    System.err.println("EndLine Comments not fully supported yet");
    print(INLINE_COMMENT_OPEN);
    print(SPACE);
    print(comment.commentBody());
    printNewLine();
    return null;
  }

  // - Private Methods

  @FunctionalInterface
  private interface Block {
    void content();
  }

  @FunctionalInterface
  private interface Separator {
    void print();
  }

  private void printBlock(Block b) {
    print(CURLY_BRACKET_OPEN);
    printNewLine();
    increaseIndentation();
    b.content();
    decreaseIndentation();
    printIndentation();
    print(CURLY_BRACKET_CLOSE);
    printNewLine();
  }

  private void colonListSepearator() {
    print(COLON);
    print(SPACE);
  }

  private void newLineSeparator() {
    print(COLON);
    printNewLine();
    printIndentation();
  }

  private <T> void printColonList(List<T> print, Consumer<T> consumer) {
    printList(print, this::colonListSepearator, consumer);
  }

  private <T> void printListNewLine(List<T> print, Consumer<T> consumer) {
    printList(print, this::newLineSeparator, consumer);
  }

  private <T> void printOptional(Optional<T> print, Consumer<T> consumer) {
    print.ifPresent(consumer::accept);
  }

  private <T> void printList(List<T> print, Separator separator, Consumer<T> consumer) {
    Iterator<T> i = print.iterator();
    if (!i.hasNext()) {
      return;
    }
    T t = i.next();
    consumer.accept(t);

    while (i.hasNext()) {
      t = i.next();
      separator.print();
      consumer.accept(t);
    }
  }

  private void print(String text) {
    try {
      writer.write(text);
    } catch (IOException exc) {
      //TODO: to implement
    }
  }

  private void printNewLine() {
    print(System.lineSeparator());
  }

  private void printIndentation() {
    print(SPACE.repeat(indentation));
  }

  private void decreaseIndentation() {
    indentation -= INDENTATION_STEPS;
  }

  private void increaseIndentation() {
    indentation += INDENTATION_STEPS;
  }
}
