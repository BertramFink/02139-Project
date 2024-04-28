import chisel3._
import chisel3.util._

class FSM extends Module {
  val io = IO(new   Bundle {
    val coin2 = Input(Bool())
    val coin5 = Input(Bool())
    val buy = Input(Bool())
    val enoughMoney = Input(Bool())
    val alarm = Output(Bool())
    val releaseCan = Output(Bool())
    val idleScreen = Output(Bool())
    val add2 = Output(Bool())
    val add5 = Output(Bool())
    val purchase = Output(Bool())
  })
  io.idleScreen := false.B

  // Rising Edge
  def rising_edge(input:Bool):Bool = {
    input & !RegNext(input)
  }
  
  // States
  object State extends ChiselEnum {
    val idle, txt, coin2, coin5, buy, alarm, releaseCan = Value
  }
  import State._

  // The state register
  val stateReg = RegInit(txt)

  // Next state
  switch ( stateReg ) {
    is (txt) {
      when(rising_edge(io.coin2)) {
        stateReg := idle
      }.elsewhen(rising_edge(io.coin5)) {
        stateReg := idle
      }.elsewhen(rising_edge(io.buy)) {
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
      }
    }
    is (coin2) {
      stateReg := idle
    }
    is (coin5) {
      stateReg := idle
    }
    is (buy) {
      stateReg := releaseCan
      when(io.enoughMoney === false.B) {
        stateReg := alarm
      }
    }
    is (releaseCan) {
      stateReg := releaseCan
      when(!io.buy) {
        stateReg := idle
      }
    }
    is (alarm) {
      stateReg := alarm
      when(!io.buy) {
        stateReg := idle
      }
    }
  }

  io.idleScreen := false.B
  when (stateReg === txt) {
    io.idleScreen := true.B
  }
  io.add2 := false.B
  when (stateReg === coin2) {
    io.add2 := true.B
  }
  io.add5 := false.B
  when (stateReg === coin5) {
    io.add5 := true.B
  }
  io.purchase := false.B
  when (stateReg === buy & io.enoughMoney) {
    io.purchase := true.B
  }
  io.alarm := false.B
  when (stateReg === alarm) {
    io.alarm := true.B
  }
  io.releaseCan := false.B
  when (stateReg === releaseCan) {
    io.releaseCan := true.B
  }
}
