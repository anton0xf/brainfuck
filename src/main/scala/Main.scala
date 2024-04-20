
@main def run(cmd: String, opt: String): Unit =
  println("Brainfuck interpreter")
  cmd match {
    case "-c" => Interpreter.fromString(opt).run()
  } 
