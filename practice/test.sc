import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.Scalaz._
import scalaz._

def dis: Exception \/ Int = 1.right

//dis >>= { x => x + 1 }


val m1 = Map(
  1 -> List("a", "b"),
  2 -> List("aa", "bb")
)

val m2 = Map(
  1 -> List("z"),
  3 -> List("yyy", "zzz")
)

m1 |+| m2

List(Future(1), Future(2)).sequence
