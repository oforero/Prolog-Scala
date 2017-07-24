package prolog.tests

import org.scalatest.Inside.inside
import org.scalatest.{FunSuite, Matchers}
import prolog.ast
import prolog.parser.PrologParser

import scala.util.Success

class PrologParserTests extends FunSuite with Matchers{
  test("Parsing a compound program") {
    val program = "parent(chester,irvin) .\n" +
                  "parent(chester,clarence) .\n" +
                  "parent(chester,mildred) .\n" +
                  "parent(irvin,ron) .\n" +
                  "parent(irvin,ken) .\n" +
                  "parent(clarence,shirley) .\n" +
                  "parent(clarence,sharon) .\n" +
                  "parent(clarence,charlie) .\n" +
                  "parent(mildred,mary) .\n" +
                  "?- parent(chester,mildred) ."
    val parser = new PrologParser(program)

    inside(parser.Program.run()) {
      case Success(ast.Program(clauses, query)) =>
        clauses should have size 9
        query.predicates should have size 1
    }
  }
}
