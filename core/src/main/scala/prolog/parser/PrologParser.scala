package prolog.parser

import org.parboiled2._
import prolog.ast
import shapeless.{::, HNil}

class PrologParser(val input: ParserInput) extends PrologRecognizer {

  def Numeral: Rule[HNil, ::[ast.Numeral, HNil]] = rule { capture(numeral) ~> (nstr => ast.Numeral(nstr)) }
  def SmallAtom: Rule[HNil, ::[ast.Atom, HNil]] = rule { capture(smallAtom) ~> (str => ast.Atom(str)) }
  def StringAtom: Rule[HNil, ::[ast.Atom, HNil]] = rule { capture(stringAtom) ~> (str => ast.Atom(str)) }
  def Atom: Rule[HNil, ::[ast.Atom, HNil]] = rule { capture(atom) ~> (str => ast.Atom(str)) }
  def Variable: Rule[HNil, ::[ast.Variable, HNil]] = rule { capture(variable) ~> (str => ast.Variable(str)) }


  def Term: Rule[HNil, ::[ast.Term, HNil]] = rule { Numeral | Variable | Structure  | Atom }
  def Functor: Rule[HNil, ::[ast.Functor, HNil]] = rule { capture(functor) ~> (str => ast.Functor(str)) }

  def StructContentsStr: Rule[HNil, ::[String, HNil]] = rule { capture(structContents) }
  def StructContents: Rule[HNil, ::[Seq[ast.Term], HNil]] = rule {
    oneOrMore(Numeral | Variable | Atom ).separatedBy( comma ~ whitespace )
  }
  def Structure: Rule[HNil, ::[ast.Structure, HNil]] = rule {
    Functor ~ openparenthesis ~ StructContentsStr ~ closeparenthesis ~> {
      (f: ast.Functor, tl: String) =>
        val parsedContent = new PrologParser(tl).StructContents.run()
        test( parsedContent.isSuccess ) ~ push(ast.Structure(f, parsedContent.get))
      }
  }

}
