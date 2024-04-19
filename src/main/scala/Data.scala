case class Data (data: Map[Int, Byte] = Map()):
  def get(dp: Int): Byte = data.getOrElse(dp, 0)
  def set(dp: Int, byte: Byte): Data = Data(data.updated(dp, byte))
  def inc(dp: Int): Data = set(dp, (get(dp) + 1).toByte)
  def dec(dp: Int): Data = set(dp, (get(dp) - 1).toByte)

  override def equals(obj: Any): Boolean = obj match {
    case o: Data => (data.keySet ++ o.data.keySet).forall(k => get(k) == o.get(k))
    case _ => false
  }
end Data

object Data:
  def fromBytes(data: Seq[Byte]): Data =
    Data(Map.from(data.zipWithIndex.map((b,i) => (i, b))))