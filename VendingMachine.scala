import chisel3._
import chisel3.util._

class VendingMachine(maxCount: Int) extends Module {
  val io = IO(new Bundle {
    val price = Input(UInt(5.W))
    val coin2 = Input(Bool())
    val coin5 = Input(Bool())
    val buy = Input(Bool())
    val releaseCan = Output(Bool())
    val alarm = Output(Bool())
    val seg = Output(UInt(7.W))
    val an = Output(UInt(4.W))
  })

  // Syncronized inputs
  val coin2_sync = RegInit(0.U(1.W))
  val coin5_sync = RegInit(0.U(1.W))
  val buy_sync = RegInit(0.U(1.W))
  coin2_sync := io.coin2
  coin5_sync := io.coin5
  buy_sync := io.buy

  // Bank
  val sum = RegInit(0.U(8.W))

  // Finite State Machine
  val fsm = Module(new FSM)
  fsm.io.coin2 := coin2_sync
  fsm.io.coin5 := coin5_sync
  fsm.io.buy := buy_sync
  fsm.io.sum := sum
  fsm.io.price := io.price

  sum := fsm.io.newSum

  // Seven Segment Display
  val sevSegController = Module(new SevenSegController(maxCount))

  sevSegController.io.price := io.price
  sevSegController.io.sum := sum


  val init = RegInit(1.U(1.W))
  when (init === 1.U) {
    init := 0.U
    io.seg := "b0111111".U
    io.an := "b0000".U
  }

  // Idle Screen
  val txtController = Module(new TxtController(maxCount))
  when (fsm.io.idleScreen) {
    io.an := txtController.io.an
    io.seg := txtController.io.seg
  } .otherwise {
    io.seg := sevSegController.io.seg
    io.an := sevSegController.io.an
  }

  // LEDs
  io.releaseCan := fsm.io.releaseCan
  io.alarm := fsm.io.alarm
}

// generate Verilog
object VendingMachine extends App {
  (new chisel3.stage.ChiselStage).emitVerilog(new VendingMachine(100000))
}


