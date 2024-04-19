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
    assertEquals(res, VM.init.copy(ip = 1, dp = 1))
  }

  test("'<' Decrement the data pointer by one") {
    val res = Interpreter.fromString("<").step(VM(dp = 10))
    assertEquals(res, VM.init.copy(ip = 1, dp = 9))
  }

  test("'<' on zero data pointer is error") {
    val ex = intercept[RuntimeException] {
      Interpreter.fromString("<").step(VM.init)
    }
    assertEquals(ex.getMessage, "< on zero data pointer")
  }

  test("'+' Increment the byte at the data pointer (0) by one") {
    val res = Interpreter.fromString("+").step(VM.init)
    assertEquals(res, VM.init.copy(ip = 1, data = Data.fromBytes(Seq(1))))
  }

  test("'+' Increment the byte at the data pointer (2) by one") {
    val res = Interpreter.fromString("+").step(VM(dp = 2))
    assertEquals(
      res,
      VM.init.copy(ip = 1, dp = 2, data = Data.fromBytes(Seq(0, 0, 1)))
    )
  }

  test("'+' Increment the byte at the data pointer by one with overflow") {
    val vm = VM(data = Data.fromBytes(Seq(Byte.MaxValue)))
    val res = Interpreter.fromString("+").step(vm)
    assertEquals(res, VM.init.copy(ip = 1, data = Data.fromBytes(Seq(Byte.MinValue))))
  }

  test("'-' Decrement the byte at the data pointer (0) by one") {
    val res = Interpreter.fromString("-").step(VM.init)
    assertEquals(res, VM.init.copy(ip = 1, data = Data.fromBytes(Seq(-1))))
  }

  test("'-' Decrement the byte at the data pointer (2) by one") {
    val res = Interpreter.fromString("-").step(VM(dp = 2))
    assertEquals(
      res,
      VM.init.copy(ip = 1, dp = 2, data = Data.fromBytes(Seq(0, 0, -1)))
    )
  }

  test("'-' Decrement the byte at the data pointer by one with overflow") {
    val vm = VM(data = Data.fromBytes(Seq(Byte.MinValue)))
    val res = Interpreter.fromString("-").step(vm)
    assertEquals(res, VM.init.copy(ip = 1, data = Data.fromBytes(Seq(Byte.MaxValue))))
  }
}
