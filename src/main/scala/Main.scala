
@main def hello(): Unit =
  println("Hello world!")
  val b = System.in.read().toByte
  println(s"char: ${b.toChar}")
