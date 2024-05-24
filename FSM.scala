import chisel3._
import chisel3.util._

class FSM extends Module {
  val io = IO(new   Bundle {
    val coin2 = Input(Bool())
    val coin5 = Input(Bool())
    val buy = Input(Bool())
    val nextItem = Input(Bool())
    val enoughMoney = Input(Bool())
    val alarm = Output(Bool())
    val releaseCan = Output(Bool())
    val idleScreen = Output(Bool())
    val add2 = Output(Bool())
    val add5 = Output(Bool())
    val purchase = Output(Bool())
    val cycle = Output(Bool())
  })
  io.idleScreen := false.B

  // Rising Edge
  def rising_edge(input:Bool):Bool = {
    input & !RegNext(input)
  }
  
  // States
  object State extends ChiselEnum {
    val idle, txt, coin2, coin5, buy, nextItem, alarm, releaseCan = Value
  }
  import State._

  // The state register
  val stateReg = RegInit(txt)

  // Next state
  switch ( stateReg ) {
    is (txt) {
      when(io.coin2 | io.coin5 | io.buy | io.nextItem) {
        stateReg := idle
      }
    }
    is (idle) {
      when(rising_edge(io.coin2)) {
        stateReg := coin2
      }.elsewhen(rising_edge(io.coin5)) {
        stateReg := coin5
      }.elsewhen(rising_edge(io.buy)) {
        stateReg := buy
      }.elsewhen(rising_edge(io.nextItem)) {
        stateReg := nextItem
      }
    }
    is (coin2) {
      stateReg := idle
    }
    is (coin5) {
      stateReg := idle
    }
    is (buy) {
      when(io.enoughMoney === false.B) {
        stateReg := alarm
      }.otherwise {
        stateReg := releaseCan
      }
    }
    is (nextItem) {
      stateReg := idle
    }
    is (releaseCan) {
      when(!io.buy) {
        stateReg := idle
      }
    }
    is (alarm) {
      when(!io.buy) {
        stateReg := idle
      }
    }
  }

  io.idleScreen := (stateReg === txt)
  io.add2       := (stateReg === coin2)
  io.add5       := (stateReg === coin5)
  io.cycle      := (stateReg === nextItem)
  io.purchase   := (stateReg === buy & io.enoughMoney)
  io.alarm      := (stateReg === alarm)
  io.releaseCan := (stateReg === releaseCan)
}
