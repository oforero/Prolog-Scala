package prolog
package tests

import org.parboiled2.ParseError
import org.scalatest.prop.{Checkers, GeneratorDrivenPropertyChecks}
import org.scalatest.{BeforeAndAfterAll, FunSuite, Matchers, Inside}
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

  test("Parsing a string of digits as a numeral") {
    forAll(numeral) {
      (nstr: String) => {
        new PrologParser(nstr).Numeral.run() shouldEqual Success(ast.Numeral(nstr))
      }
    }
  }

  test("Parsing a string containing non-digits as a numeral") {
    val parser = new PrologParser("Failure")
    val parsed = parser.Numeral.run()
    inside(parsed) {
      case Failure(err: ParseError) => parser.formatError(err) startsWith "[Invalid input 'F', expected Numeral (line 1, column 1)"
    }

  }
}
