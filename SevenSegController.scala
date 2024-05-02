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
    // switch(alarmSelect) {
    //   is(0.U) {
    //     io.an := "b0000".U
    //     io.seg := "b1111111".U
    //   }
    //   is(1.U) {
    //     io.seg := sevSegController.io.seg
    //     io.an := sevSegController.io.an
    //   }
    // }
  }

  // when(alarmSelect === false.B) {
  //   io.an := "b0000".U
  //   io.seg := "b1111111".U
  // }

  // Text

  val txtSelect = RegInit(0.U(1.W))
  val txtCounter = RegInit(0.U(32.W))

  txtCounter := txtCounter + 1.U
  when(txtCounter === (maxCount*1000).U) {
    txtCounter := 0.U
    txtSelect := ~txtSelect
  }

  val foodString = "food"
  val sodaString = "soda"

  val food = Wire(Vec(4, UInt(5.W)))
  val soda = Wire(Vec(4, UInt(5.W)))

  for (i <- 0 until 4) {
    food(i) := (foodString(3-i) - 97).U
    soda(i) := (sodaString(3-i) - 97).U
  }

  sevSegChar.io.in := food(segSelect)
  when (txtSelect === 1.U) {
    sevSegChar.io.in := soda(segSelect)
  }

  io.seg := ~sevSegNum.io.out
  when (io.idleScreen) {
    io.seg := ~sevSegChar.io.out
  } .elsewhen(alarmSelect === false.B) {
    io.seg := "b1111111".U
  }

  val init = RegInit(1.U(1.W))
  when (init === 1.U) {
    init := 0.U
    io.seg := "b0111111".U
    io.an := "b0000".U
  }
}
