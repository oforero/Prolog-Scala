package prolog.parser

import org.parboiled2._
import prolog.ast
import prolog.ast.{Atom, Numeral, Variable}
import shapeless.{HNil, ::}

class PrologParser(val input: ParserInput) extends PrologRecognizer {

  def Numeral: Rule[HNil, ::[Numeral, HNil]] = rule { capture(numeral) ~> (nstr => ast.Numeral(nstr)) }
  def SmallAtom: Rule[HNil, ::[Atom, HNil]] = rule { capture(smallAtom) ~> (str => ast.Atom(str)) }
  def StringAtom: Rule[HNil, ::[Atom, HNil]] = rule { capture(stringAtom) ~> (str => ast.Atom(str)) }
  def Atom: Rule[HNil, ::[Atom, HNil]] = rule { capture(atom) ~> (str => ast.Atom(str)) }
  def Variable: Rule[HNil, ::[Variable, HNil]] = rule { capture(variable) ~> (str => ast.Variable(str)) }
}
