# simple brainfuck implementation
See [wiki](https://en.wikipedia.org/wiki/Brainfuck)

## sbt project compiled with Scala 3
### Usage
This is a normal sbt project. You can compile code with `sbt compile`, run it with `sbt run`, and `sbt console` will start a Scala 3 REPL.

For more information on the sbt-dotty plugin, see the
[scala3-example-project](https://github.com/scala/scala3-example-project/blob/main/README.md).

Usage example:
```shell
$ sbt "run -c '++++++++[>++++[>++>+++>+++>+<<<<-]>+>+>->>+[<]<-]>>.>---.+++++++..+++.>>.<-.<.+++.------.--------.>>+.>++.'"
...
Brainfuck interpreter
Hello World!
[success] Total time: 1 s, completed Apr 20, 2024, 10:10:47 PM
```

## Language
The language consists of eight commands.
A brainfuck program is a sequence of these commands,
possibly interspersed with other characters (which are ignored).
The commands are executed sequentially, with some exceptions:
an instruction pointer begins at the first command, and each command it points to is executed,
after which it normally moves forward to the next command.
The program terminates when the instruction pointer moves past the last command.

The brainfuck language uses a simple machine model consisting of the program and instruction pointer,
as well as a one-dimensional array of at least 30,000 byte cells initialized to zero;
a movable data pointer (initialized to point to the leftmost byte of the array);
and two streams of bytes for input and output (stdin and stdout).

The eight language commands each consist of a single character:

* **Character** - **meaning**
* <code>&gt;</code> - Increment the data pointer by one
  (to point to the next cell to the right)
* <code>&lt;</code> - Decrement the data pointer by one
  (to point to the next cell to the left)
* <code>+</code> - Increment the byte at the data pointer by one
* <code>-</code> - Decrement the byte at the data pointer by one
* <code>.</code> - Output the byte at the data pointer
* <code>,</code> - Accept one byte of input, storing its value in the byte at the data pointer
* <code>[</code> - If the byte at the data pointer is zero,
  then instead of moving the instruction pointer forward to the next command,
  jump it "forward" to the command after the "matching" <code>]</code> command
* <code>]</code> - If the byte at the data pointer is nonzero,
  then instead of moving the instruction pointer forward to the next command,
  jump it "back" to the command after the "matching" <code>[</code> command.
  Alternatively, the <code>]</code> command may instead be translated as
  an unconditional jump "to" the corresponding <code>[</code> command, or vice versa;
  programs will behave the same but will run more slowly, due to unnecessary double searching.

<code>[</code> and <code>]</code> match as parentheses usually do:
each <code>[</code> matches exactly one <code>]</code> and vice versa,
the <code>[</code> comes first,
and there can be no unmatched <code>[</code> or <code>]</code> between the two.
