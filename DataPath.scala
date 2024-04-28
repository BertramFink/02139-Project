import chisel3._
import chisel3.util._

class DataPath extends Module {
  val io = IO(new Bundle {
    val add2 = Input(Bool())
    val add5 = Input(Bool())
    val purchase = Input(Bool())
    val price = Input(UInt(5.W))
    val sum = Output(UInt(8.W))
    val enoughMoney = Output(Bool())
  })

  val sum = RegInit(0.U(8.W))
  io.sum := sum
  io.enoughMoney := (sum >= io.price)

  sum := sum
  when (io.add2 === true.B) {
    sum := 99.U  
    when(sum < 98.U) {
      sum := sum + 2.U
    }
  }.elsewhen (io.add5 === true.B) {
    sum := 99.U  
    when(sum < 95.U) {
      sum := sum + 5.U
    }
  }.elsewhen (io.purchase === true.B) {
    sum := sum - io.price
  }
}
