package prolog
package tests

import org.parboiled2.ParseError
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSuite, Inside, Matchers}
import prolog.parser.PrologParser
import prolog.tests.arbitrary.AllArbitrary

import scala.util.{Failure, Success}

/**
  * Base definition for Prolog test suites.
  */
class  PrologParserProperties extends FunSuite
  with Matchers
  with GeneratorDrivenPropertyChecks
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

  test("Parsing a recursive structure from a correct string succeed") {
    val parser = new PrologParser("functor1(term1, Var1, functor2(term2))")
    val parsed = parser.Structure.run()
    inside(parsed) {
      case Success(ast.Structure(ast.Atom("functor1"), Vector(_, _, ast.Structure(_, _)))) => true
    }
  }

  test("Parsing a predicate from a correct string succeeds") {
    forAll(predicate) {
      case PrologString(str) =>
        val parser = new PrologParser(str)
        val parsed = parser.Predicate.run()
        inside(parsed) {
          case Success(_: ast.Predicate) => true
        }
    }
  }

  test("Parsing a predicate list from a correct string succeeds") {
    forAll(predicatelist) {
      case PrologString(str) =>
        val parser = new PrologParser(str)
        val parsed = parser.PredicateList.run()
        inside(parsed) {
          case Success(_: Vector[ast.Predicate]) => true
        }
    }
  }

  test("Parsing a simple clause from a correct string succeeds") {
    forAll(simpleclause) {
      case PrologString(str) =>
        val parser = new PrologParser(str)
        val parsed = parser.SimpleClause.run()
        inside(parsed) {
          case Success(_: ast.SimpleClause) => true
        }
    }
  }

  test("Parsing a complex clause from a correct string succeeds") {
    forAll(complexclause) {
      case PrologString(str) =>
        val parser = new PrologParser(str)
        val parsed = parser.ComplexClause.run()
        inside(parsed) {
          case Success(_: ast.ComplexClause) => true
        }
    }
  }

  test("Parsing a clause from a correct string succeeds") {
    forAll(clause) {
      case PrologString(str) =>
        val parser = new PrologParser(str)
        val parsed = parser.Clause.run()
        inside(parsed) {
          case Success(_: ast.Clause) => true
        }
    }
  }

  test("Parsing a clause list from a correct string succeeds") {
    forAll(clauselist) {
      case PrologString(str) =>
        val parser = new PrologParser(str)
        val parsed = parser.ClauseList.run()
        inside(parsed) {
          case Success(_: Vector[ast.Clause]) => true
        }
    }
  }

  test("Parsing a query from a correct string succeeds") {
    forAll(query) {
      case PrologString(str) =>
        val parser = new PrologParser(str)
        val parsed = parser.Query.run()
        inside(parsed) {
          case Success(_: ast.Query) => true
        }
    }
  }

  test("Parsing a Program from a correct string succeeds") {
    forAll(program) {
      case PrologString(str) =>
        val parser = new PrologParser(str)
        val parsed = parser.Program.run()
        inside(parsed) {
          case Success(ast.Program(_, _)) => true
          case Failure(err: ParseError) =>
            println(parser.formatError(err))
            throw err
        }
    }
  }

}
