package prolog

import org.parboiled2.ParseError
import prolog.parser.PrologParser

import scala.util.{Failure, Success}


object Prolog {
  def main(args: Array[String]): Unit = {
    args match {
      case Array(file) =>
        val source = scala.io.Source.fromFile(file)
        val programTxt = try source.mkString finally source.close()
        val parser = new PrologParser(programTxt)
        parser.Program.run() match {
          case Success(program) =>
            println(s"The file ($file) is a valid prolog program")
            println("AST: ")
            println(program)
          case Failure(err: ParseError) =>
            println(s"Unable to parse file: $file")
            println(parser.formatError(err))
        }
      case _ =>
        println("Illegal arguments")
        System.exit(-1)
    }
  }
}
