package gui

import game.Enemy
import game.Tower
import scalafx.scene.canvas.Canvas
import scala.collection.mutable.Buffer
import scala.util.Random.nextGaussian

object Effects {
  
  def advance() = {
    this.moneyEffects.foreach(_.advance())
    this.towerupEffects.foreach(_.advance())
    this.moneyEffects = this.moneyEffects.filterNot(_.finished)
    this.towerupEffects = this.towerupEffects.filterNot(_.finished)
  }
  
  var moneyEffects = Buffer[MoneyEffect]()
  case class MoneyEffect(var x: Double, var y: Double, reward: Int) { 
    var finished = false
    var speed = 0.05
    var age = 0
    val text = "$" + reward.toString()
    def advance() = {
      this.speed *= 0.9
      this.y -= this.speed
      if (this.age > 30) this.finished = true
      this.age += 1
    }
  }
  def addMoneyEffect(e: Enemy) = {
    this.moneyEffects += MoneyEffect(e.pos.x, e.pos.y, e.reward)
    Audio.play("coin.wav", 0.15)
  }
  
  var towerupEffects = Buffer[TowerupEffect]()
  case class TowerupEffect(_x: Double, _y: Double) {
    var x = _x + 0.5 * (nextGaussian - 0.5)
    var y = _y + 0.5 * (nextGaussian - 0.5)
    var speed = 0.05
    var age = 0
    var finished = false
    def advance() = {
      this.speed *= 0.9
      this.y -= this.speed
      if (this.age > 35) this.finished = true
      this.age += 1
    }
  }
  def addTowerupEffect(t: Tower) = {
    for (i <- 0 until 10) {
      this.towerupEffects += TowerupEffect(t.pos.x, t.pos.y)
    }
  }
  
}