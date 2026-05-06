package org.contract_lib.lang.verifast.ast;

import java.util.function.Function;

public interface VeriFastComment {

  public <R> R perform(
      Function<NoEscaping, R> noEscaping,
      Function<SingleLine, R> singleLine,
      Function<Inline, R> inline,
      Function<Multiline, R> multiline,
      Function<EndLine, R> endLine);

  /// This class delgates creating the comment escape to the content of the comment.
  public record NoEscaping(String commentBody) implements VeriFastComment {

    @Override
    public <R> R perform(
        Function<NoEscaping, R> noEscaping,
        Function<SingleLine, R> singleLine,
        Function<Inline, R> inline,
        Function<Multiline, R> multiline,
        Function<EndLine, R> endLine) {
      return noEscaping.apply(this);
    }
  }

  public record SingleLine(String commentBody) implements VeriFastComment {
    @Override
    public <R> R perform(
        Function<NoEscaping, R> noEscaping,
        Function<SingleLine, R> singleLine,
        Function<Inline, R> inline,
        Function<Multiline, R> multiline,
        Function<EndLine, R> endLine) {
      return singleLine.apply(this);
    }
  }

  /// Comment for {@code void /* Comment here */ myMethod() { … }}
  public record Inline(String commentBody) implements VeriFastComment {

    @Override
    public <R> R perform(
        Function<NoEscaping, R> noEscaping,
        Function<SingleLine, R> singleLine,
        Function<Inline, R> inline,
        Function<Multiline, R> multiline,
        Function<EndLine, R> endLine) {
      return inline.apply(this);
    }
  }

  /// Comment for {@code /* \\n  * Comment here \\n */  \\n void  myMethod() { … }}
  public record Multiline(String commentBody) implements VeriFastComment {
    @Override
    public <R> R perform(
        Function<NoEscaping, R> noEscaping,
        Function<SingleLine, R> singleLine,
        Function<Inline, R> inline,
        Function<Multiline, R> multiline,
        Function<EndLine, R> endLine) {
      return multiline.apply(this);
    }
  }

  /// Comment for {@code \\n void  myMethod() { // Comment here \\n     … \\n}}.
  public record EndLine(String commentBody) implements VeriFastComment {
    @Override
    public <R> R perform(
        Function<NoEscaping, R> noEscaping,
        Function<SingleLine, R> singleLine,
        Function<Inline, R> inline,
        Function<Multiline, R> multiline,
        Function<EndLine, R> endLine) {
      return endLine.apply(this);
    }
  }
}
