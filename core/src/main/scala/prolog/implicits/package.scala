package prolog

import cats.Show

package object implicits  {
  implicit val numeralShow: Show[ast.Numeral] = Show.show(_.text)
  implicit val atomShow: Show[ast.Atom] = Show.show(_.text)
}
