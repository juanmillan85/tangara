

trait Data[A] {
  def create : A
}

trait Wrapper {
  type Implementation
  implicit val instance: Data[Implementation]
}
object DataInstances {
  implicit object IntData extends Data[Int] { def create = 0}
  implicit object StringData extends Data[String] { def create = "<empty>"}
  implicit object FloatData extends Data[Float] { def create = 0.5F}
}


import DataInstances._

object IntWrapper extends Wrapper { type Implementation = Int; implicit val instance = IntData }
object StringWrapper extends Wrapper { type Implementation = String; implicit val instance = StringData}
object FloatWrapper extends Wrapper { type Implementation = Float; implicit val instance = FloatData}



object Test {
  def run[W <: Wrapper](wrapper: W)(implicit data: Data[W#Implementation]) : W#Implementation = {
    data.create
  }
}

Test.run(StringWrapper)