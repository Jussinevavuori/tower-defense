package gui

import game.Enemy
import scalafx.scene.canvas.Canvas
import scala.collection.mutable.Buffer

object Effects {
  
  def advance() = {
    this.moneyEffects.foreach(_.advance())
    this.moneyEffects = this.moneyEffects.filterNot(_.finished)
  }
  
  var moneyEffects = Buffer[MoneyEffect]()
  case class MoneyEffect(var x: Double, var y: Double) { 
    var finished = false
    var speed = 0.05
    var age = 0
    def advance() = {
      this.speed *= 0.9
      this.y -= this.speed
      if (this.age > 30) this.finished = true
      this.age += 1
    }
  }
  def addMoneyEffect(e: Enemy) = {
    this.moneyEffects += MoneyEffect(e.pos.x, e.pos.y)
    Audio.play("coin.wav", 0.15)
  }
  
}