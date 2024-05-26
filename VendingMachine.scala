import chisel3._
import chisel3.util._

class VendingMachineIo extends Bundle {
  val price = Input(UInt(5.W))
  val setPrice = Input(Bool())
  val coin2 = Input(Bool())
  val coin5 = Input(Bool())
  val buy = Input(Bool())
  val nextItem = Input(Bool())
  val releaseCan = Output(Bool())
  val alarm = Output(Bool())
  val seg = Output(UInt(7.W))
  val an = Output(UInt(4.W))
}

class Top(maxCount: Int) extends Module {
  val io = IO(new VendingMachineIo)

  // Finite State Machine and Datapath
  val fsm = Module(new FSM)
  val datapath = Module(new DataPath)

  fsm.io.coin2 := io.coin2
  fsm.io.coin5 := io.coin5
  fsm.io.buy := io.buy
  fsm.io.nextItem := io.nextItem
  fsm.io.enoughMoney := datapath.io.enoughMoney

  datapath.io.add2 := fsm.io.add2
  datapath.io.add5 := fsm.io.add5
  datapath.io.purchase := fsm.io.purchase
  datapath.io.price := io.price
  datapath.io.cycle := fsm.io.cycle
  datapath.io.setPrice := io.setPrice

  // Seven Segment Display
  val sevSegController = Module(new SevenSegController(maxCount))

  sevSegController.io.idleScreen := fsm.io.idleScreen
  sevSegController.io.alarm := fsm.io.alarm
  sevSegController.io.price := datapath.io.activePrice
  sevSegController.io.sum := datapath.io.sum

  io.seg := sevSegController.io.seg
  io.an := sevSegController.io.an

  // LEDs
  io.releaseCan := fsm.io.releaseCan
  io.alarm := fsm.io.alarm
}

class VendingMachine(maxCount: Int) extends Module {
  val io = IO(new VendingMachineIo)

  val reset_sync = RegNext(reset)
  val coin2_sync = RegNext(io.coin2)
  val coin5_sync = RegNext(io.coin5)
  val buy_sync = RegNext(io.buy)
  val setPrice_sync = RegNext(io.setPrice)
  val nextItem_sync = RegNext(io.nextItem)

  val top = withReset(reset_sync) { Module(new Top(maxCount)) }

  top.io <> io
  top.io.coin2 := coin2_sync
  top.io.coin5 := coin5_sync
  top.io.buy := buy_sync
  top.io.setPrice := setPrice_sync
  top.io.nextItem := nextItem_sync
}

// generate Verilog
object VendingMachine extends App {
  (new chisel3.stage.ChiselStage).emitVerilog(new VendingMachine(100000))
}
