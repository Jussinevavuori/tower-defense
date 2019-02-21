package gui

import game.Enemy
import game.Tower
import scalafx.scene.canvas.Canvas
import scala.collection.mutable.Buffer
import scala.util.Random.nextGaussian

object Effects {
  
  var effects = Buffer[Effect]()
  
  // An effect class that lives for a certain amount of time and dies
  sealed abstract class Effect(val maxAge: Int) {
    var age = 0
    var finished = false
    def update(): Unit
    def advance(): Unit = {
      this.update()
      this.age += 1
      this.finished = this.age >= maxAge
    }
  }
  
  // Advancing all effects
  def advance() = {
    this.effects.foreach(_.advance)
    this.effects = this.effects.filterNot(_.finished)
  }
  
  // Money from killing enemies
  case class MoneyEffect(var x: Double, var y: Double, reward: Int) extends Effect(30) { 
    var speed = 0.05
    val text = "$" + reward.toString()
    def update() = {
      this.speed *= 0.9
      this.y -= this.speed
    }
  }
  def addMoneyEffect(e: Enemy) = {
    this.effects += MoneyEffect(e.pos.x, e.pos.y, e.reward)
    Audio.play("coin.wav", 0.15)
  }
  
  // Particles from advancing towers
  case class TowerupEffect(_x: Double, _y: Double) extends Effect(35)  {
    var x = _x + 0.5 * (nextGaussian - 0.5)
    var y = _y + 0.5 * (nextGaussian - 0.5)
    var speed = 0.05
    def update() = {
      this.speed *= 0.9
      this.y -= this.speed
    }
  }
  def addTowerupEffect(t: Tower) = {
    for (i <- 0 until 10) {
      this.effects += TowerupEffect(t.pos.x, t.pos.y)
    }
  }
  
  // Explosions from homing projectiles
  case class ExplosionEffect(val x: Double, val y: Double) extends Effect(18) {
    def update() = Unit
  }
  def addExplosionEffect(e: Enemy) = {
    this.effects += ExplosionEffect(e.pos.x, e.pos.y)
    Audio.play("explosion.wav")
  }
  
}