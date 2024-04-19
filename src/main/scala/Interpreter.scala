val dataSize = 30_000

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
  def step(vm: VM): VM = vm

object Interpreter:
  def fromString(s: String): Interpreter = Interpreter(Vector.from(s))