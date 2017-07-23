package prolog.ast

sealed trait Constant {
  def text: String
}

case class Atom(text: String) extends Constant

case class Numeral(text: String) extends Constant {
  private val validator = """^\d+$"""
  require(text matches validator)
}
