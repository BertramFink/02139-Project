import chisel3._
import chisel3.util._

class SevenSegController(maxCount: Int) extends Module {
  val io = IO(new Bundle {
    val price = Input(UInt(8.W))
    val sum = Input(UInt(8.W))
    val seg = Output(UInt(7.W))
    val an = Output(UInt(4.W))
  })

  // Initialize 'Seven Segment Display'
  val sevSeg = Module(new SevenSegNum)

  val segSelect = RegInit(0.U(2.W))
  val counter = RegInit(0.U(17.W))

  // Initialize BCD for price/sum
  val bcd = Module(new BcdTable())
  bcd.io.address := Mux(segSelect > 1.U, io.sum, io.price)

  counter := counter + 1.U
  when(counter === maxCount.U) {
    counter := 0.U
    segSelect := segSelect + 1.U
  }

  io.an := "b1111".U
  switch(segSelect) {
    is (0.U) { io.an := "b1110".U }
    is (1.U) { io.an := "b1101".U }
    is (2.U) { io.an := "b1011".U }
    is (3.U) { io.an := "b0111".U }
  }

  sevSeg.io.in := 0.U
  switch(segSelect) {
    is (0.U) { sevSeg.io.in := bcd.io.data(3,0) }
    is (1.U) { sevSeg.io.in := bcd.io.data(7,4) }
    is (2.U) { sevSeg.io.in := bcd.io.data(3,0) }
    is (3.U) { sevSeg.io.in := bcd.io.data(7,4) }
  }

  io.seg := ~sevSeg.io.out
}
