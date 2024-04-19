case class Data (data: Map[Int, Byte] = Map()):
  def get(dp: Int): Byte = data.getOrElse(dp, 0)
  def set(dp: Int, byte: Byte): Data = Data(data.updated(dp, byte))
  def inc(dp: Int) = set(dp, (get(dp) + 1).toByte)

object Data:
  def fromBytes(data: Seq[Byte]): Data =
    Data(Map.from(data.zipWithIndex.map((b,i) => (i, b))))