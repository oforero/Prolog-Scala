package prolog
package tests
package arbitrary

import org.scalacheck.Gen

trait AllArbitrary {
  private val digitsLst = List('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
  private val lowerLst = ('a' to 'z').toList
  private val upperLst = '_' :: ('A' to 'Z').toList
  private val specialLst = List('+', '-', '*', '/', '\\', '^', '~', ':', '.', '?', '#', '$', '&')
  private val characterLst = digitsLst ++ lowerLst ++ upperLst ++ specialLst

  private val digits = Gen.oneOf(digitsLst)
  private val lowercase = Gen.oneOf(lowerLst)
  private val character = Gen.oneOf(characterLst)

  case class PrologString(str: String)

  val numeral: Gen[PrologString] = for {
    ds <- Gen.listOf(digits)
    if ds.nonEmpty
  } yield PrologString(ds.mkString)

  val smallAtom: Gen[PrologString] = for {
    l <- lowercase
    cs <- Gen.listOf(character)
  } yield PrologString((l :: cs).mkString)

  val string: Gen[PrologString] = for {
    cs <- Gen.listOf(character)
    if cs.nonEmpty
  } yield PrologString(cs.mkString)

}
