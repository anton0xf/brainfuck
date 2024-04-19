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
  def step(vm: VM): VM =
    if program.indices contains vm.ip
    then {
      val cmd = program(vm.ip)
      cmd match {
        case '>' => vm.copy(ip = vm.ip + 1, data = vm.data.inc(vm.dp))
        case _ => vm
      }
    } else vm

object Interpreter:
  def fromString(s: String): Interpreter = Interpreter(Vector.from(s))