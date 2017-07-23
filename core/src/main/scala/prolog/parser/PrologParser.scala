package prolog.parser

import org.parboiled2._
import prolog.ast
import prolog.ast.Atom
import shapeless.HNil

class PrologParser(val input: ParserInput) extends Parser {
  private def specialChars = CharPredicate('+', '-', '*', '/', '\\', '^', '~',
                                           ':', '.', '?', '#',  '$', '&')
  private def lowercase = CharPredicate.LowerAlpha
  private def uppercase = CharPredicate('_') ++ CharPredicate.UpperAlpha
  private def digit = CharPredicate.Digit
  private def characters = digit ++ lowercase ++ uppercase ++ specialChars

  def Numeral: Rule[HNil, shapeless.::[ast.Numeral, HNil]] = rule {
    capture(oneOrMore(digit)) ~> (nstr => ast.Numeral(nstr))
  }
  def SmallAtom: Rule[HNil, shapeless.::[Atom, HNil]] = rule {
    capture(CharPredicate.LowerAlpha ~ zeroOrMore(characters)) ~> (astr => ast.Atom(astr))
  }
}
