package prolog.ast

import org.parboiled2.ParserInput
import prolog.parser.PrologRecognizer

import scala.util.{Failure, Success}

private case class Validator(input: ParserInput) extends PrologRecognizer


sealed trait PrologType

sealed trait SimpleType extends PrologType {
  def text: String
}

sealed trait Constant extends SimpleType
case class Atom(text: String) extends Constant {
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

case class Variable(text: String) extends SimpleType {
  require {
    Validator(text).variable.run() match {
      case Success(_) => true
      case Failure(_) => false
    }
  }
}
