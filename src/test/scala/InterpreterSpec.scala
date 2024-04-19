import munit.FunSuite

import java.io.{ByteArrayInputStream, InputStream}

class InterpreterSpec extends FunSuite {
  test("empty program do nothing") {
    val res = Interpreter.fromString("").step(VM.init)
    assertEquals(res, (VM.init, None))
  }

  test("other characters are ignored") {
    val res = Interpreter.fromString("asdf").step(VM.init)
    assertEquals(res, (VM(ip = 1), None))
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

  test(
    "',' Accept one byte of input, storing its value in the byte at the data pointer"
  ) {
    val in: InputStream = ByteArrayInputStream("Asdf".getBytes)
    val res = Interpreter.fromString(",").step(VM.init, in)
    assertEquals(
      res,
      (VM(ip = 1, data = Data.fromBytes(Seq('A'.toByte))), None)
    )
  }

  test("'[' If data is not zero, then move the instruction pointer forward") {
    val vm = VM.init.setData(1)
    val res = Interpreter.fromString("[ab]c").step(vm)
    assertEquals(res, (VM(ip = 1, data = Data.fromBytes(Seq(1))), None))
  }

  test("'[' If data is zero, move forward to the next command after ']'") {
    val res = Interpreter.fromString("[ab]c").step(VM.init)
    assertEquals(res, (VM(ip = 4), None))
  }

  test("'[' If data is zero, move forward to the next command after matching ']'") {
    val res = Interpreter.fromString("[a[b[]]c]d").step(VM.init)
    assertEquals(res, (VM(ip = 9), None))
  }

  test("']' If data is zero, then move forward") {
    val vm = VM(ip = 3)
    val res = Interpreter.fromString("[ab]c").step(vm)
    assertEquals(res, (VM(ip = 4), None))
  }

  test("']' If data is nonzero, then jump back to the command after the matching '['") {
    val data = Data.fromBytes(Seq(1))
    val vm = VM(ip = 8, data = data)
    val res = Interpreter.fromString("[a[b[]]c]d").step(vm)
    assertEquals(res, (VM(ip = 1, data = data), None))
  }
}
