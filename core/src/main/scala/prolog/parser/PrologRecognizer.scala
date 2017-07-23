package prolog.parser

import org.parboiled2.{CharPredicate, Parser, ParserInput, Rule}
import shapeless.HNil

trait PrologRecognizer extends Parser {
  private def specialChars = CharPredicate('+', '-', '*', '/', '\\', '^', '~',
    ':', '.', '?', '#',  '$', '&')
  private def lowercase = CharPredicate.LowerAlpha
  private def uppercase = CharPredicate('_') ++ CharPredicate.UpperAlpha
  private def digit = CharPredicate.Digit
  private def characters = digit ++ lowercase ++ uppercase ++ specialChars
  private def quote = CharPredicate('\'')
  protected def openparenthesis: CharPredicate = CharPredicate('(')
  protected def closeparenthesis: CharPredicate = CharPredicate(')')
  protected def comma: CharPredicate = CharPredicate(',')
  protected def space: CharPredicate = CharPredicate(' ')

  def whitespace = rule { zeroOrMore(space) }
  def numeral: Rule[HNil, HNil] = rule { oneOrMore(digit) }
  def smallAtom: Rule[HNil, HNil] = rule { lowercase ~ zeroOrMore(characters) }
  def stringAtom: Rule[HNil, HNil] = rule { quote ~ zeroOrMore(characters) ~ quote }
  def atom: Rule[HNil, HNil] = rule { smallAtom | stringAtom }
  def variable: Rule[HNil, HNil] = rule { uppercase ~ zeroOrMore(characters) }

  def term: Rule[HNil, HNil] = rule { numeral | atom | variable }
  def termlist: Rule[HNil, HNil] = rule { oneOrMore(term).separatedBy(comma) }
  def functor: Rule[HNil, HNil] = term

  def structContents: Rule[HNil, HNil] = rule { oneOrMore(noneOf("()")) }

  def structure: Rule[HNil, HNil] = rule {
    functor ~ openparenthesis ~ capture(structContents) ~ closeparenthesis ~>{
      (tc: String) =>
        val parsedContent = (new {val input: ParserInput = tc} with PrologRecognizer).structContents.run()
        test(parsedContent.isSuccess)
    }

  }

}
