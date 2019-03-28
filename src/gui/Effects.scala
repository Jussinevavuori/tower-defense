package gui

import game.Enemy
import game.Tower
import scalafx.scene.canvas.Canvas
import scala.collection.mutable.Buffer
import scala.util.Random.nextGaussian

/** Contains all the effect particles and functions for them. */
object Effects {
  
  /** Buffer of all existing effects. */
  var effects = Buffer[Effect]()
  
  /** The abstract class for all effects, that live for maxAge frames. */
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
  
  /** Function to advance all effects and remove the finished. */
  def advance() = {
    this.effects.foreach(_.advance)
    this.effects = this.effects.filterNot(_.finished)
  }
  
  /** A money effect ($ sign) created each time an enemy is killed. */
  case class MoneyEffect(var x: Double, var y: Double, reward: Int) extends Effect(30) { 
    var speed = 0.05
    val text = "$" + reward.toString()
    def update() = {
      this.speed *= 0.9
      this.y -= this.speed
    }
  }
  
  /** Function to create a money effect from an enemy. */
  def addMoneyEffect(e: Enemy) = {
    this.effects += MoneyEffect(e.pos.x, e.pos.y, e.reward)
    Audio.play("coin.wav", 0.15)
  }
  
  /** Animated particle from upgrading a tower. */
  case class TowerupEffect(_x: Double, _y: Double) extends Effect(35)  {
    var x = _x + 0.5 * (nextGaussian - 0.5)
    var y = _y + 0.5 * (nextGaussian - 0.5)
    var speed = 0.05
    def update() = {
      this.speed *= 0.9
      this.y -= this.speed
    }
  }
  
  /** Function to create towerup effects from a tower. */
  def addTowerupEffect(t: Tower) = {
    for (i <- 0 until 10) {
      this.effects += TowerupEffect(t.pos.x, t.pos.y)
    }
  }
  
  /** An explosion effect from missile collisions. */
  case class ExplosionEffect(val x: Double, val y: Double) extends Effect(18) {
    def update() = Unit
  }
  
  /** Function to create explosions from an enemy. */
  def addExplosionEffect(e: Enemy) = {
    this.effects += ExplosionEffect(e.pos.x, e.pos.y)
    Audio.play("explosion.wav", 0.2)
  }
  
}