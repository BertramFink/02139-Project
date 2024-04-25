import chisel3._
import chisel3.util._

class TextController(maxCount: Int) extends Module {
  val io = IO(new Bundle {
    val seg = Output(UInt(7.W))
    val an = Output(UInt(4.W))
  })

  // Initialize 'Seven Segment Display'
  val sevSeg = Module(new SevenSegTxt)

  val segSelect = RegInit(0.U(2.W))
  val counter = RegInit(0.U(17.W))

  counter := counter + 1.U
  when(counter === maxCount.U) {
    counter := 0.U
    segSelect := segSelect + 1.U
  }

  val txtSelect = RegInit(0.U(1.W))
  val txtCounter = RegInit(0.U(32.W))

  txtCounter := txtCounter + 1.U
  when(txtCounter === maxCount.U*100) {
    txtCounter := 0.U
    txtSelect := txtSelect + 1.U
  }
  var seg = Wire(Vec(4, UInt(2.W)))

  val seg1 = WireDefault(0.U)
  val seg2 = WireDefault(0.U)
  val seg3 = WireDefault(0.U)
  val seg4 = WireDefault(0.U)

  when (txtCounter === 0.U) {
    switch(segSelect) {
      is (0.U) { sevSeg.io.in := 5.U }
      is (1.U) { sevSeg.io.in := 14.U }
      is (2.U) { sevSeg.io.in := 14.U }
      is (3.U) { sevSeg.io.in := 3.U }
    }
  } .otherwise {
    switch(segSelect) {
      is (0.U) { sevSeg.io.in := 18.U }
      is (1.U) { sevSeg.io.in := 14.U }
      is (2.U) { sevSeg.io.in := 3.U }
      is (3.U) { sevSeg.io.in := 0.U }
    }
  }

  seg(segSelect) := sevSeg.io.out



  
}
