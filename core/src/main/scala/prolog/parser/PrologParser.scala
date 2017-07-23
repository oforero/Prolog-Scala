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

  private def Digits = rule { oneOrMore(CharPredicate.Digit) }
  private def Chars = rule { zeroOrMore(characters) }
  private def smallAtom = rule { CharPredicate.LowerAlpha ~ zeroOrMore(characters) }

  def SmallAtom: Rule[HNil, shapeless.::[Atom, HNil]] = rule { capture(smallAtom) ~> (astr => ast.Atom(astr)) }
  def Numeral: Rule[HNil, shapeless.::[ast.Numeral, HNil]] = rule { capture(Digits) ~> (nstr => ast.Numeral(nstr)) }
}
