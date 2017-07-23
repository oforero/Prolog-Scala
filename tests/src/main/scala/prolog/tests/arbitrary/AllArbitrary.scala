package prolog
package tests
package arbitrary

import org.scalacheck.Gen

trait AllArbitrary {
  private val digits = Gen.oneOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')

  val numeral: Gen[String] = for {
    ns <- Gen.listOf(digits)
    if ns.nonEmpty
  } yield ns.mkString
}
