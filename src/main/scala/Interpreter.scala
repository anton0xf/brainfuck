import java.io.InputStream

case class VM(
    /** instruction pointer */
    ip: Int = 0,
    /** data pointer */
    dp: Int = 0,
    data: Data = Data()
)

object VM:
  def init: VM = VM()

case class Interpreter(program: Vector[Char]):
  def step(vm: VM, in: InputStream = System.in): (VM, Option[Byte]) =
    program
      .lift(vm.ip)
      .collect {
        case '>' => (vm.copy(ip = vm.ip + 1, dp = vm.dp + 1), None)
        case '<' =>
          if vm.dp == 0
          then throw RuntimeException("< on zero data pointer")
          else (vm.copy(ip = vm.ip + 1, dp = vm.dp - 1), None)
        case '+' => (vm.copy(ip = vm.ip + 1, data = vm.data.inc(vm.dp)), None)
        case '-' => (vm.copy(ip = vm.ip + 1, data = vm.data.dec(vm.dp)), None)
        case '.' => (vm.copy(ip = vm.ip + 1), Some(vm.data.get(vm.dp)))
        case ',' =>
          val newData = vm.data.set(vm.dp, in.read().toByte)
          val newVM = vm.copy(ip = vm.ip + 1, data = newData)
          (newVM, None)
      }
      .getOrElse((vm, None))

object Interpreter:
  def fromString(s: String): Interpreter = Interpreter(Vector.from(s))
