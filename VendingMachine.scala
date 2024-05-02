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

  // Finite State Machine and Datapath
  val fsm = Module(new FSM)
  val datapath = Module(new DataPath)

  fsm.io.coin2 := coin2_sync
  fsm.io.coin5 := coin5_sync
  fsm.io.buy := buy_sync
  fsm.io.enoughMoney := datapath.io.enoughMoney

  datapath.io.add2 := fsm.io.add2
  datapath.io.add5 := fsm.io.add5
  datapath.io.purchase := fsm.io.purchase
  datapath.io.price := io.price

  // Seven Segment Display
  val sevSegController = Module(new SevenSegController(maxCount))

  sevSegController.io.idleScreen := fsm.io.idleScreen
  sevSegController.io.alarm := fsm.io.alarm
  sevSegController.io.price := io.price
  sevSegController.io.sum := datapath.io.sum

  io.seg := sevSegController.io.seg
  io.an := sevSegController.io.an

  // LEDs
  io.releaseCan := fsm.io.releaseCan
  io.alarm := fsm.io.alarm

}

// generate Verilog
object VendingMachine extends App {
  (new chisel3.stage.ChiselStage).emitVerilog(new VendingMachine(100000))
}


