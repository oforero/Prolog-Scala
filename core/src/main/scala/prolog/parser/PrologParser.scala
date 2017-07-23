package prolog.parser

import org.parboiled2._
import prolog.ast
import shapeless.HNil

class PrologParser(val input: ParserInput) extends Parser {
  private def Digits = rule { oneOrMore(CharPredicate.Digit) }
  def Numeral: Rule[HNil, shapeless.::[ast.Numeral, HNil]] = rule { capture(Digits) ~> (nstr => ast.Numeral(nstr)) }
}
