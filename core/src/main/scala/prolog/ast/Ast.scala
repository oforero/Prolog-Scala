package prolog.ast

import org.parboiled2.ParserInput
import prolog.parser.PrologRecognizer

import scala.util.{Failure, Success}

private case class Validator(input: ParserInput) extends PrologRecognizer


sealed trait PrologType

sealed trait SimpleType extends PrologType {
  def text: String
}

sealed trait Term extends PrologType
sealed trait Constant extends SimpleType with Term
sealed trait Predicate extends PrologType with Term
sealed trait Clause extends PrologType

case class Atom(text: String) extends Constant with Predicate {
  require {
    Validator(text).atom.run() match {
      case Success(_) => true
      case Failure(_) => false
    }
  }
}

case class Numeral(text: String) extends Constant {
  require {
    Validator(text).numeral.run() match {
      case Success(_) => true
      case Failure(_) => false
    }
  }
}

case class Variable(text: String) extends SimpleType with Term {
  require {
    Validator(text).variable.run() match {
      case Success(_) => true
      case Failure(_) => false
    }
  }
}

case class Structure(functor: Atom, terms: Seq[Term]) extends PrologType with Predicate

case class SimpleClause(predicate: Predicate) extends Clause
case class ComplexClause(predicate: Predicate, lst: Seq[Predicate]) extends Clause

case class Query(predicates: Seq[Predicate]) extends PrologType

case class Program(clauses: Seq[Clause], query: Query) extends PrologType