import chisel3._
import chisel3.util._

class SevenSegChar extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(5.W))
    val out = Output(UInt(7.W))
  })

  val sevSeg = WireDefault(0.U)

  switch(io.in) {
    is (0.U) { sevSeg := "b1110111".U } // A
    is (1.U) { sevSeg := "b1111100".U } // b
    is (2.U) { sevSeg := "b0111001".U } // C
    is (3.U) { sevSeg := "b1011110".U } // d
    is (4.U) { sevSeg := "b1111001".U } // E
    is (5.U) { sevSeg := "b1110001".U } // F
    is (6.U) { sevSeg := "b0111101".U } // G
    is (7.U) { sevSeg := "b1110110".U } // H
    is (8.U) { sevSeg := "b0110000".U } // I
    is (9.U) { sevSeg := "b0011110".U } // J
    is (10.U) { sevSeg := "b1110101".U } // K
    is (11.U) { sevSeg := "b0111000".U } // L
    is (12.U) { sevSeg := "b0010101".U } // M
    is (13.U) { sevSeg := "b0110111".U } // N
    is (14.U) { sevSeg := "b1011100".U } // O
    is (15.U) { sevSeg := "b1110011".U } // P
    is (16.U) { sevSeg := "b1101011".U } // q
    is (17.U) { sevSeg := "b0110001".U } // r
    is (18.U) { sevSeg := "b1101101".U } // S
    is (19.U) { sevSeg := "b1111000".U } // t
    is (20.U) { sevSeg := "b0111110".U } // U
    is (21.U) { sevSeg := "b0111110".U } // V
    is (22.U) { sevSeg := "b0101010".U } // W
    is (23.U) { sevSeg := "b1110110".U } // X
    is (24.U) { sevSeg := "b1101110".U } // Y
    is (25.U) { sevSeg := "b1011011".U } // Z
    is (26.U) { sevSeg := "b1111111".U }
    is (27.U) { sevSeg := "b1111111".U }
    is (28.U) { sevSeg := "b1111111".U }
    is (29.U) { sevSeg := "b1111111".U }
    is (30.U) { sevSeg := "b1111111".U }
    is (31.U) { sevSeg := "b1111111".U }
  }
  io.out := sevSeg

  // Food select?
  
}
