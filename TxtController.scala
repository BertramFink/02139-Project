import chisel3._
import chisel3.util._

class TxtController(maxCount: Int) extends Module {
  val io = IO(new Bundle {
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

  txtCounter := txtCounter + 1.U
  when(txtCounter === (maxCount*100).U) {
    txtCounter := 0.U
    txtSelect := txtSelect + 1.U
  }

  //Select characters to display
  sevSeg.io.in := 0.U
  switch(txtCounter) {
    is(0.U) { // Display "FOOD"
      switch(segSelect) {
        is(0.U) {
          sevSeg.io.in := 3.U
        } // D
        is(1.U) {
          sevSeg.io.in := 14.U
        } // O
        is(2.U) {
          sevSeg.io.in := 14.U
        } // O
        is(3.U) {
          sevSeg.io.in := 5.U
        } // F
      }
    }
    is(1.U) { // Display "SODA"
      switch(segSelect) {
        is(0.U) {
          sevSeg.io.in := 0.U
        } // A
        is(1.U) {
          sevSeg.io.in := 3.U
        } // D
        is(2.U) {
          sevSeg.io.in := 14.U
        } // O
        is(3.U) {
          sevSeg.io.in := 18.U
        } // S
      }
    }
  }
  io.seg := ~sevSeg.io.out

  // Choose Display
  io.an := "b1111".U
  switch(segSelect) {
    is (0.U) { io.an := "b1110".U }
    is (1.U) { io.an := "b1101".U }
    is (2.U) { io.an := "b1011".U }
    is (3.U) { io.an := "b0111".U }
  }
}
