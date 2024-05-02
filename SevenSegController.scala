import chisel3._
import chisel3.util._

class SevenSegController(maxCount: Int) extends Module {
  val io = IO(new Bundle {
    val idleScreen = Input(Bool())
    val alarm = Input(Bool())
    val price = Input(UInt(8.W))
    val sum = Input(UInt(8.W))
    val seg = Output(UInt(7.W))
    val an = Output(UInt(4.W))
  })

  // Initialize 'Seven Segment Display'
  val sevSegNum = Module(new SevenSegNum)
  val sevSegChar = Module(new SevenSegChar)

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

  sevSegNum.io.in := 0.U
  switch(segSelect) {
    is (0.U) { sevSegNum.io.in := bcd.io.data(3,0) }
    is (1.U) { sevSegNum.io.in := bcd.io.data(7,4) }
    is (2.U) { sevSegNum.io.in := bcd.io.data(3,0) }
    is (3.U) { sevSegNum.io.in := bcd.io.data(7,4) }
  }

  // Alarm

  val alarmCounter = RegInit(0.U(32.W))
  val alarmSelect = RegInit(0.U(1.W))
  when(io.alarm === true.B |alarmSelect === false.B) {
    alarmCounter := alarmCounter + 1.U
    when (alarmCounter === (maxCount*40).U) {
      alarmSelect := ~alarmSelect
      alarmCounter := 0.U
    }
  }

  // Text

  val txtSelect = RegInit(0.U(5.W))
  val txtCounter = RegInit(0.U(32.W))

  txtCounter := txtCounter + 1.U
  when(txtCounter === (maxCount*1000).U) {
    txtCounter := 0.U
    txtSelect := txtSelect + 1.U
    when (txtSelect === 11.U) {
      txtSelect := 0.U
    }
  }

  val string = "food  soda  food"
  val text = Wire(Vec(string.length(), UInt(5.W)))

  for (i <- 0 until string.length()) {
    if (string(i) == ' ') {
      text(i) := 26.U // space
    } else {
      text(i) := (string(i) - 97).U
    }
  }

  sevSegChar.io.in := text(txtSelect - segSelect + 3.U)


                // val txtSelect = RegInit(0.U(5.W))
                // val txtCounter = RegInit(0.U(32.W))

                // when(io.active) {
                //   txtCounter := txtCounter + 1.U
                // } .otherwise {
                //   txtCounter := 0.U
                //   txtSelect := 0.U
                // }

                // when(txtCounter === (maxCount*1000).U) {
                //   txtCounter := 0.U
                //   when(txtSelect === 13.U) {
                //     txtSelect := 0.U
                //   } .otherwise {
                //     txtSelect := txtSelect + 1.U
                //   }
                // }

                // //Select characters to display
                // io.an := 0.U
                // sevSeg.io.in := 0.U
                // switch(segSelect) {
                //   is(0.U) { // First Seg
                //     io.an := "b0111".U
                //     sevSeg.io.in := txtSelect + 0.U
                //   }
                //   is(1.U) { // Second Seg
                //     io.an := "b1011".U
                //     sevSeg.io.in := txtSelect + 1.U
                //   }
                //   is(2.U) { // Third Seg
                //     io.an := "b1101".U
                //     sevSeg.io.in := txtSelect + 2.U
                //   }
                //   is(3.U) { // Fourth Seg
                //     io.an := "b1110".U
                //     sevSeg.io.in := txtSelect + 3.U
                //   }
                // }

  // Toggle between modes

  io.seg := ~sevSegNum.io.out
  when (io.idleScreen) {
    io.seg := ~sevSegChar.io.out
  } .elsewhen(alarmSelect === false.B) {
    io.seg := "b1111111".U
  }

  // Init

  val init = RegInit(1.U(1.W))
  when (init === 1.U) {
    init := 0.U
    io.seg := "b0111111".U
    io.an := "b0000".U
  }
}
