import java.io.InputStream
import scala.annotation.tailrec

case class VM(
    /** instruction pointer */
    ip: Int = 0,
    /** data pointer */
    dp: Int = 0,
    data: Data = Data()
) {
  def nextIP: VM = copy(ip = ip + 1)
  def incDP: VM = copy(dp = dp + 1)
  def decDP: VM = if dp == 0
  then throw RuntimeException("< on zero data pointer")
  else copy(dp = dp - 1)
  def incData: VM = copy(data = data.inc(dp))
  def decData: VM = copy(data = data.dec(dp))
  def setData(byte: Byte): VM = copy(data = data.set(dp, byte))
  def getData: Byte = data.get(dp)
}

object VM:
  def init: VM = VM()

case class Interpreter(program: Vector[Char]):
  def step(vm: VM, in: InputStream = System.in): (VM, Option[Byte]) =
    program
      .lift(vm.ip)
      .collect {
        case '>' => (vm.nextIP.incDP, None)
        case '<' => (vm.nextIP.decDP, None)
        case '+' => (vm.nextIP.incData, None)
        case '-' => (vm.nextIP.decData, None)
        case '.' => (vm.nextIP, Some(vm.data.get(vm.dp)))
        case ',' => (vm.nextIP.setData(in.read().toByte), None)
        case '[' =>
          val newVM =
            if vm.getData != 0 then vm.nextIP
            else vm.copy(ip = skipParens(program, vm.ip))
          (newVM, None)
        case _ => (vm.nextIP, None)
      }
      .getOrElse((vm, None))

object Interpreter:
  def fromString(s: String): Interpreter = Interpreter(Vector.from(s))

def skipParens(p: Vector[Char], ip: Int): Int =
  assert(p(ip) == '[', "sequence of parens should start from '[': " + p)
  @tailrec
  def go(i: Int, open: Int): Int =
    if open == 0 || i > p.length then i
    else {
      p(i) match {
        case '[' => go(i + 1, open + 1)
        case ']' => go(i + 1, open - 1)
        case _   => go(i + 1, open)
      }
    }
  go(ip + 1, 1)
