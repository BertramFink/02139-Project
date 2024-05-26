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
  val firstCounter = RegInit(0.U(17.W))

  firstCounter := firstCounter + 1.U
  val firstCount = (firstCounter === maxCount.U)
  when(firstCount) {
    firstCounter := 0.U
    segSelect := segSelect + 1.U
  }

  io.an := "b1111".U
  switch(segSelect) {
    is (0.U) { io.an := "b1110".U }
    is (1.U) { io.an := "b1101".U }
    is (2.U) { io.an := "b1011".U }
    is (3.U) { io.an := "b0111".U }
  }

  // Initialize BCD for price/sum
  val bcd = Module(new BcdTable())
  bcd.io.address := Mux(segSelect(1), io.sum, io.price)

  // Output number to display
  sevSegNum.io.in := Mux(segSelect(0), bcd.io.data(7,4), bcd.io.data(3,0))

  // Alarm
  val secondCounter = RegInit(0.U(6.W))
  val secondCount = (secondCounter === 40.U) & firstCount
  when (firstCount) {
    secondCounter := secondCounter + 1.U
    when (secondCount) {
      secondCounter := 0.U
    }
  }

  val alarmSelect = RegInit(false.B)
  when (secondCount) {
    when (io.alarm) {
      alarmSelect := ~alarmSelect
    } .otherwise {
      alarmSelect := false.B
    }
  }

  // Text
  val thirdCounter = RegInit(0.U(3.W))
  val thirdCount = (thirdCounter === 5.U) & secondCount
  when (secondCount) {
    thirdCounter := thirdCounter + 1.U
    when(thirdCount) {
      thirdCounter := 0.U
    }
  }

  val txtSelect = RegInit(0.U(5.W))
  when (thirdCount) {
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
  } .elsewhen(alarmSelect) {
    io.seg := "b1111111".U
  } .otherwise {
    io.seg := ~sevSegNum.io.out
  }

  // Init
  when (reset.asBool) {
    io.seg := "b0111111".U
    io.an := "b0000".U
  }
}
