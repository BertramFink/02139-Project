import chisel3._
import chisel3.util._

class SevenSegController extends Module {
  val io = IO(new Bundle {
    val price = Input(UInt(8.W))
    val sum = Input(UInt(8.W))
    val seg = Output(UInt(7.W))
    val an = Output(UInt(4.W))
  })

  val segSelect = RegInit(0.U(2.W))
  val counter = RegInit(0.U(17.W))

  counter := counter + 1.U
  when(counter === 100000.U) {
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

  val table = Wire(Vec(256 , UInt (8.W)))
  for (i <- 0 until 100) {
    table(i) := (((i/10)<<4) + i%10).U
  }
  for (i <- 100 until 256) {
    table(i) := 0.U
  }

  val priceBCD = WireDefault(0.U(8.W))
  val sumBCD = WireDefault(0.U(8.W))

  priceBCD := table(io.price)
  sumBCD := table(io.sum)

  val sevSeg = Module(new SevenSegDec)
  sevSeg.io.in := 0.U

  switch(segSelect) {
    is (0.U) { sevSeg.io.in := priceBCD(3,0) }
    is (1.U) { sevSeg.io.in := priceBCD(7,4) }
    is (2.U) { sevSeg.io.in := sumBCD(3,0) }
    is (3.U) { sevSeg.io.in := sumBCD(7,4) }
  }

  io.seg := ~sevSeg.io.out
}
