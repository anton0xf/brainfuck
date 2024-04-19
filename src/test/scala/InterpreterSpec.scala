import munit.FunSuite

class InterpreterSpec extends FunSuite {
  test("empty program do nothing") {
    val res = Interpreter.fromString("").step(VM.init)
    assertEquals(res, VM.init)
  }
}
