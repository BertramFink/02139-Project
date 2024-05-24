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
  bcd.io.address := Mux(segSelect(1), io.sum, io.price)

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
  when(io.alarm === true.B | alarmSelect === false.B) {
    alarmCounter := alarmCounter + 1.U
    when (alarmCounter === (maxCount*40).U) {
      alarmSelect := ~alarmSelect
      alarmCounter := 0.U
    }
  } q

  // Text

  val txtSelect = RegInit(0.U(5.W))
  val txtCounter = RegInit(0.U(32.W))

  txtCounter := txtCounter + 1.U
  when(txtCounter === (maxCount*200).U) {
    txtCounter := 0.U
    txtSelect := txtSelect + 1.U
    when (txtSelect === 13.U) {
      txtSelect := 0.U
    }
  }

  val string = "food  soda    food"
  val text = Wire(Vec(string.length(), UInt(5.W)))

  for (i <- 0 until string.length()) {
    if (string(i) == ' ') {
      text(i) := 26.U // space
    } else {
      text(i) := (string(i) - 'a').U
    }
  }

  sevSegChar.io.in := text(txtSelect - segSelect + 3.U)

  // Toggle between modes

  when (io.idleScreen) {
    io.seg := ~sevSegChar.io.out
  }.elsewhen(alarmSelect === false.B) {
    io.seg := "b1111111".U
  }.otherwise {
    io.seg := ~sevSegNum.io.out
  }

  // Init

  when (reset.asBool) {
    io.seg := "b0111111".U
    io.an := "b0000".U
  }
}
