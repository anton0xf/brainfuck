val v = Vector[Byte](0, 1, 2)
val vv = v.appended(3: Byte)
vv(2)
vv.updated(2, 5: Byte)
v.updated(5, 5: Byte)

'A'.toByte
0x41