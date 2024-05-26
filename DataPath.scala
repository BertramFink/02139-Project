import chisel3._
import chisel3.util._

class DataPath extends Module {
  val io = IO(new Bundle {
    val add2 = Input(Bool())
    val add5 = Input(Bool())
    val cycle = Input(Bool())
    val purchase = Input(Bool())
    val setPrice = Input(Bool())
    val price = Input(UInt(5.W))
    val activePrice = Output(UInt(5.W))
    val sum = Output(UInt(8.W))
    val enoughMoney = Output(Bool())
  })

  val priceAddr = RegInit(0.U(2.W))
  when (io.cycle === true.B) {
    priceAddr := priceAddr + 1.U
  }

  val priceMem = SyncReadMem(4, UInt(5.W))
  val price = priceMem.read(priceAddr)
  io.activePrice := price

  when (io.setPrice & !RegNext(io.setPrice)) {
    priceMem.write(priceAddr, io.price)
  }

  val sum = RegInit(0.U(8.W))
  io.sum := sum
  io.enoughMoney := (sum >= price)

  when (io.add2 === true.B) {
    when(sum < 98.U) {
      sum := sum + 2.U
    } .otherwise {
      sum := 99.U  
    }
  }.elsewhen (io.add5 === true.B) {
    when(sum < 95.U) {
      sum := sum + 5.U
    } .otherwise {
      sum := 99.U  
    }
  } .elsewhen (io.purchase === true.B) {
    sum := sum - price
  }
}
