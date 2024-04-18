import chisel3._
import chisel3.util._

class FSM extends Module {
  val io = IO(new Bundle {
    val coin2 = Input(Bool())
    val coin5 = Input(Bool())
    val buy = Input(Bool())
    val price = Input(UInt(5.W))
    val sum = Input(UInt(8.W))
    val newSum = Output(UInt(8.W))
    val alarm = Output(Bool())
    val releaseCan = Output(Bool())
  })

  // Rising Edge
  def rising_edge(input:Bool):Bool = {
    input & !RegNext(input)
  }
  
  // States
  object State extends ChiselEnum {
    val idle, coin2, coin5, buy, alarm, releaseCan = Value
  }
  import State._

  // The state register
  val stateReg = RegInit(idle)

  // Next state
  switch ( stateReg ) {
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
      when(io.sum < io.price) {
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

  io.newSum := io.sum
  io.alarm := false.B
  io.releaseCan := false.B
  switch (stateReg) {
    is (coin2) {
      io.newSum := 99.U  
      when(io.sum < 98.U) {
        io.newSum := io.sum + 2.U
      }
    }
    is (coin5) {
      io.newSum := 99.U
      when(io.sum < 95.U) {
        io.newSum := io.sum + 5.U
      }
    }
    is (buy) {
      io.newSum := io.sum
      // io.alarm := true.B
      when(io.sum >= io.price) {
        io.newSum := io.sum - io.price
        // io.alarm := false.B
      }
    }
    is (releaseCan) {
      io.releaseCan := true.B
    }
    is (alarm) {
      io.alarm := true.B
    }
  }

  // Output logic
  // io.alarm := false.B
  // when (io.sum < io.price) {
  //   io.alarm := io.buy
  // }
}
