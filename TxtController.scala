import chisel3._
import chisel3.util._

class TxtController(maxCount: Int) extends Module {
  val io = IO(new Bundle {
    val active = Input(Bool())
    val seg = Output(UInt(7.W))
    val an = Output(UInt(4.W))
  })

  // Initialize 'Seven Segment Display'
  val sevSeg = Module(new TxtLookup)

  val segSelect = RegInit(0.U(2.W))
  val counter = RegInit(0.U(17.W))

  counter := counter + 1.U
  when(counter === (maxCount).U) {
    counter := 0.U
    segSelect := segSelect + 1.U
  }

  val txtSelect = RegInit(0.U(1.W))
  val txtCounter = RegInit(0.U(32.W))

  when(io.active) {
    txtCounter := txtCounter + 1.U
  } .otherwise {
    txtCounter := 0.U
    txtSelect := 0.U
  }

  when(txtCounter === (maxCount*1000).U) {
    txtCounter := 0.U
    when(txtSelect === 10.U) {
      txtSelect := 0.U
    } .otherwise {
      txtSelect := txtSelect + 1.U
    }
  }

  //Select characters to display
  io.an := 0.U
  sevSeg.io.in := 0.U
  switch(segSelect) {
    is(0.U) { // First Seg
      io.an := "b0111".U
      sevSeg.io.in := txtSelect + 0.U
    }
    is(1.U) { // Second Seg
      io.an := "b1011".U
      sevSeg.io.in := txtSelect + 1.U
    }
    is(2.U) { // Third Seg
      io.an := "b1101".U
      sevSeg.io.in := txtSelect + 2.U
    }
    is(3.U) { // Fourth Seg
      io.an := "b1110".U
      sevSeg.io.in := txtSelect + 3.U
    }
  }
  io.seg := ~sevSeg.io.out
}
