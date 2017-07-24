package prolog
package tests

import org.parboiled2.ParseError
import org.scalatest.prop.{Checkers, GeneratorDrivenPropertyChecks}
import org.scalatest.{BeforeAndAfterAll, FunSuite, Inside, Matchers}
import org.typelevel.discipline.scalatest.Discipline
import prolog.parser.PrologParser
import prolog.tests.arbitrary.AllArbitrary

import scala.util.{Failure, Success}

/**
  * Base definition for Prolog test suites.
  */
class  PrologSuite extends FunSuite
  with BeforeAndAfterAll
  with Checkers
  with Matchers
  with GeneratorDrivenPropertyChecks
  with Discipline
  with  AllArbitrary {
  import Inside._

  test("Parsing a string of digits as a numeral succeeds") {
    forAll(numeral) {
      case PrologString(nstr) => new PrologParser(nstr).Numeral.run() shouldEqual Success(ast.Numeral(nstr))
    }
  }

  test("Parsing a string containing non-digits as a numeral fails") {
    val parser = new PrologParser("Failure")
    val parsed = parser.Numeral.run()
    inside(parsed) {
      case Failure(err: ParseError) => parser.formatError(err) startsWith "[Invalid input 'F', expected Numeral (line 1, column 1)"
    }
  }

  test("Parsing a small atom from a correct string succeeds") {
    forAll(smallAtom) {
      case PrologString(astr) => new PrologParser(astr).SmallAtom.run() shouldEqual Success(ast.Atom(astr))
    }
  }

  test("Parsing a small atom from an incorrect string fails") {
    val parser = new PrologParser("Failure")
    val parsed = parser.SmallAtom.run()
    inside(parsed) {
      case Failure(err: ParseError) => parser.formatError(err) startsWith "[Invalid input 'F', expected Numeral (line 1, column 1)"
    }
  }

  test("Parsing a string atom from a quoted string succeeds") {
    forAll(stringAtom) {
      case PrologString(astr) => new PrologParser(astr).StringAtom.run() shouldEqual Success(ast.Atom(astr))
    }
  }

  test("Parsing a string atom from a non-quoted string fails") {
    forAll(stringAtom) {
      case PrologString(astr) =>
        val parser = new PrologParser(astr.substring(1, astr.length - 1))
        val parsed = parser.StringAtom.run()
        inside(parsed) {
          case Failure(err: ParseError) => parser.formatError(err) startsWith "[Invalid input"
        }
    }
  }

  test("Parsing a general atom succeeds") {
    forAll(atom) {
      case PrologString(astr) => new PrologParser(astr).Atom.run() shouldEqual Success(ast.Atom(astr))
    }
  }

  test("Parsing a variable from a correct string succeeds") {
    forAll(variable) {
      case PrologString(vstr) => new PrologParser(vstr).Variable.run() shouldEqual Success(ast.Variable(vstr))
    }
  }

  test("Parsing a variable from an incorrect string fails") {
    val parser = new PrologParser("failure")
    val parsed = parser.Variable.run()
    inside(parsed) {
      case Failure(err: ParseError) => parser.formatError(err) startsWith "[Invalid input 'f'"
    }
  }

  test("Parsing a simple structure from a correct string succeeds") {
    forAll(structure) {
      case PrologString(str) =>
        val parser = new PrologParser(str)
        val parsed = parser.Structure.run()
        inside(parsed) {
          case Success(ast.Structure(ast.Atom(f), Vector(tl@_*))) => true
        }
    }
  }

  test("Parsing a recursive structure from correct string succeed") {
    val parser = new PrologParser("functor1(term1, Var1, functor2(term2))")
    val parsed = parser.Structure.run()
    inside(parsed) {
      case Success(ast.Structure(ast.Atom("functor1"), Vector(_, _, ast.Structure(_, _)))) => true
    }
  }

}
