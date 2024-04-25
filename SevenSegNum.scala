import chisel3._
import chisel3.util._

class SevenSegNum extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(4.W))
    val out = Output(UInt(7.W))
  })

  val sevSeg = WireDefault(0.U)

  // *** add your table from Lab 6 here or use the version from Lab 8.

  switch(io.in) {
    is (0.U)  { sevSeg := "b0111111".U} // 0
    is (1.U)  { sevSeg := "b0000110".U} // 1
    is (2.U)  { sevSeg := "b1011011".U} // 2
    is (3.U)  { sevSeg := "b1001111".U} // 3
    is (4.U)  { sevSeg := "b1100110".U} // 4
    is (5.U)  { sevSeg := "b1101101".U} // 5
    is (6.U)  { sevSeg := "b1111101".U} // 6
    is (7.U)  { sevSeg := "b0000111".U} // 7
    is (8.U)  { sevSeg := "b1111111".U} // 8
    is (9.U)  { sevSeg := "b1101111".U} // 9
    is (10.U) { sevSeg := "b1110111".U} // a
    is (11.U) { sevSeg := "b1111100".U} // b
    is (12.U) { sevSeg := "b1011000".U} // c
    is (13.U) { sevSeg := "b1011110".U} // d
    is (14.U) { sevSeg := "b1111001".U} // e
    //is (15.U) { sevSeg := "b1110001".U} // f
    is (15.U) { sevSeg := "b1111111".U} // Blank
  }

  // *** end adding the table

  io.out := sevSeg
}
