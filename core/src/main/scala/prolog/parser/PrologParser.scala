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


  def SimpleTerm: Rule[HNil, ::[ast.Term , HNil]] = rule { Numeral | Variable | Atom }
  def Term: Rule[HNil, ::[ast.Term, HNil]] = rule { Structure | SimpleTerm }
  def TermList: Rule[HNil, ::[Seq[ast.Term], HNil]] = rule { oneOrMore(Term).separatedBy(comma ~ whitespace) }

  def Arguments: Rule[HNil, ::[Seq[ast.Term], HNil]] = rule { openparenthesis ~ TermList ~ closeparenthesis }
  def Structure: Rule[HNil, ::[ast.Structure, HNil]] = rule { Atom ~ Arguments ~>
    ((f, argv) => ast.Structure(f, argv))
  }

  def Predicate: Rule[HNil, ::[ast.Predicate, HNil]] = rule { Structure | Atom }
  def PredicateList: Rule[HNil, ::[Seq[ast.Predicate], HNil]] = rule {
    oneOrMore(Predicate).separatedBy(comma ~ whitespace)
  }

  def SimpleClause: Rule[HNil, ::[ast.SimpleClause, HNil]] = rule {
    Predicate ~ stop ~> ((pred) => ast.SimpleClause(pred))
  }
  def ComplexClause: Rule[HNil, ::[ast.ComplexClause, HNil]] = rule {
    Predicate ~ whitespace ~ implies ~ whitespace ~ PredicateList ~ stop ~> ((p, pl) => ast.ComplexClause(p, pl))
  }
  def Clause: Rule[HNil, ::[ast.Clause, HNil]] = rule { SimpleClause | ComplexClause }
  def ClauseList: Rule[HNil, ::[Seq[ast.Clause], HNil]] = rule {
    oneOrMore(Clause)
  }

  def Query: Rule[HNil, ::[ast.Query, HNil]] = rule {
    query ~ whitespace ~ PredicateList ~ stop ~> ((pl) => ast.Query(pl))
  }

  def QueryOnly: Rule[HNil, ::[ast.Program, HNil]] = rule {
    Query ~> ((q: ast.Query) => ast.Program(Vector(), q))
  }

  def ClausesAndQuery: Rule[HNil, ::[ast.Program, HNil]] = rule {
    ClauseList ~ Query ~> ((cl: Seq[ast.Clause],q: ast.Query) => ast.Program(cl, q))
  }

  def Program: Rule[HNil, ::[ast.Program, HNil]] = rule { QueryOnly | ClausesAndQuery }

}
