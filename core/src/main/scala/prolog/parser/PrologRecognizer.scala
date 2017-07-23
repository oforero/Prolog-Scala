package prolog.parser

import org.parboiled2.{CharPredicate, Parser, Rule}
import shapeless.HNil

trait PrologRecognizer extends Parser {
  private def specialChars = CharPredicate('+', '-', '*', '/', '\\', '^', '~',
    ':', '.', '?', '#',  '$', '&')
  private def lowercase = CharPredicate.LowerAlpha
  private def uppercase = CharPredicate('_') ++ CharPredicate.UpperAlpha
  private def digit = CharPredicate.Digit
  private def characters = digit ++ lowercase ++ uppercase ++ specialChars
  private def quote = CharPredicate('\'')

  def numeral: Rule[HNil, HNil] = rule { oneOrMore(digit) }
  def smallAtom: Rule[HNil, HNil] = rule { lowercase ~ zeroOrMore(characters) }
  def stringAtom: Rule[HNil, HNil] = rule { quote ~ zeroOrMore(characters) ~ quote }
  def atom: Rule[HNil, HNil] = rule { smallAtom | stringAtom }
  def variable: Rule[HNil, HNil] = rule { uppercase ~ zeroOrMore(characters) }

}
