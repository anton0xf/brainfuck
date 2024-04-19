import munit.FunSuite

class InterpreterSpec extends FunSuite {
  test("empty program do nothing") {
    val res = Interpreter.fromString("").step(VM.init)
    assertEquals(res, VM.init)
  }

  test("other characters are ignored") {
    val res = Interpreter.fromString("asdf").step(VM.init)
    assertEquals(res, VM.init)
  }

  test("'>' Increment the data pointer by one") {
    val res = Interpreter.fromString(">").step(VM.init)
    assertEquals(res, VM.init.copy(ip = 1, data = Data.fromBytes(Seq(1))))
  }
}
