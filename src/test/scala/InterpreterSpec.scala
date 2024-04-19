import munit.FunSuite

import java.io.{ByteArrayInputStream, InputStream}

class InterpreterSpec extends FunSuite {
  test("empty program do nothing") {
    val res = Interpreter.fromString("").step(VM.init)
    assertEquals(res, (VM.init, None))
  }

  test("other characters are ignored") {
    val res = Interpreter.fromString("asdf").step(VM.init)
    assertEquals(res, (VM.init, None))
  }

  test("'>' Increment the data pointer by one") {
    val res = Interpreter.fromString(">").step(VM.init)
    assertEquals(res, (VM(ip = 1, dp = 1), None))
  }

  test("'<' Decrement the data pointer by one") {
    val res = Interpreter.fromString("<").step(VM(dp = 10))
    assertEquals(res, (VM(ip = 1, dp = 9), None))
  }

  test("'<' on zero data pointer is error") {
    val ex = intercept[RuntimeException] {
      Interpreter.fromString("<").step(VM.init)
    }
    assertEquals(ex.getMessage, "< on zero data pointer")
  }

  test("'+' Increment the byte at the data pointer (0) by one") {
    val res = Interpreter.fromString("+").step(VM.init)
    assertEquals(res, (VM(ip = 1, data = Data.fromBytes(Seq(1))), None))
  }

  test("'+' Increment the byte at the data pointer (2) by one") {
    val res = Interpreter.fromString("+").step(VM(dp = 2))
    val expectedVm = VM(ip = 1, dp = 2, data = Data.fromBytes(Seq(0, 0, 1)))
    assertEquals(res, (expectedVm, None))
  }

  test("'+' Increment the byte at the data pointer by one with overflow") {
    val vm = VM(data = Data.fromBytes(Seq(Byte.MaxValue)))
    val res = Interpreter.fromString("+").step(vm)
    val expectedVm = VM(ip = 1, data = Data.fromBytes(Seq(Byte.MinValue)))
    assertEquals(res, (expectedVm, None))
  }

  test("'-' Decrement the byte at the data pointer (0) by one") {
    val res = Interpreter.fromString("-").step(VM.init)
    val expectedVm = VM(ip = 1, data = Data.fromBytes(Seq(-1)))
    assertEquals(res, (expectedVm, None))
  }

  test("'-' Decrement the byte at the data pointer (2) by one") {
    val res = Interpreter.fromString("-").step(VM(dp = 2))
    val expectedVm = VM(ip = 1, dp = 2, data = Data.fromBytes(Seq(0, 0, -1)))
    assertEquals(res, (expectedVm, None))
  }

  test("'-' Decrement the byte at the data pointer by one with overflow") {
    val vm = VM(data = Data.fromBytes(Seq(Byte.MinValue)))
    val res = Interpreter.fromString("-").step(vm)
    val expectedVm = VM(ip = 1, data = Data.fromBytes(Seq(Byte.MaxValue)))
    assertEquals(res, (expectedVm, None))
  }

  test("'.' Output the byte at the data pointer") {
    val byte = 'A'.toByte
    val vm = VM(data = Data.fromBytes(Seq(byte)))
    val res = Interpreter.fromString(".").step(vm)
    assertEquals(res, (vm.copy(ip = 1), Some(byte)))
  }

  test("',' Accept one byte of input, storing its value in the byte at the data pointer") {
    val in: InputStream = ByteArrayInputStream("Asdf".getBytes)
    val res = Interpreter.fromString(",").step(VM.init, in)
    assertEquals(res, (VM(ip = 1, data = Data.fromBytes(Seq('A'.toByte))), None))
  }
}
