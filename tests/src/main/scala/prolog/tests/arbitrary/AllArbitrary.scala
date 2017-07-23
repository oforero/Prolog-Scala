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
  private val uppercase = Gen.oneOf(upperLst)
  private val character = Gen.oneOf(characterLst)

  case class PrologString(str: String)

  val numeral: Gen[PrologString] = for {
    ds <- Gen.nonEmptyListOf(digits)
  } yield PrologString(ds.mkString)

  val smallAtom: Gen[PrologString] = for {
    l <- lowercase
    cs <- Gen.nonEmptyListOf(character)
  } yield PrologString((l :: cs).mkString)

  val stringAtom: Gen[PrologString] = for {
    cs <- Gen.nonEmptyListOf(character)
  } yield PrologString("'" + cs.mkString + "'")

  val atom: Gen[PrologString] = Gen.oneOf(smallAtom, stringAtom)

  val variable: Gen[PrologString] = for {
    u <- uppercase
    cs <- Gen.listOf(character)
  } yield PrologString((u :: cs).mkString)

  val functor: Gen[PrologString] = atom
  val term: Gen[PrologString] = for {
    t <- Gen.oneOf(numeral, atom, variable)
  } yield t

  val termlist: Gen[PrologString] = for {
    tl <- Gen.nonEmptyListOf(term)
  } yield PrologString(tl.map(_.str).mkString(","))

  val structure: Gen[PrologString] = for {
    f <- functor
    tl <- termlist
  } yield PrologString(s"${f.str}(${tl.str})")


}
