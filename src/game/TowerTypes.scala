package game

import scala.collection.mutable.Buffer

/** Cannon towers shoot normal bullets at a steady pace. First, cheapest towers. */
class CannonTower1(x: Double, y: Double) extends Tower(x, y) {
  val typeid   = "c1"
  val strength = 10.0
  val radius   = 3.7
  val cooldown = 1.00
  val price    = 300
  val upgrade  = Some(new CannonTower2(x, y))
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("shot1.wav", 0.2)
    Buffer(new Bullet(this.pos.x, this.pos.y, this.strength, this.radius, target))
  }
}
class CannonTower2(x: Double, y: Double) extends Tower(x, y) {
  val typeid   = "c2"
  val strength = 16.0
  val radius   = 4.2
  val cooldown = 0.66
  val price    = 500
  val upgrade  = Some(new CannonTower3(x, y))
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("shot1.wav", 0.2)
    Buffer(new Bullet(this.pos.x, this.pos.y, this.strength, this.radius, target))
  }
}
class CannonTower3(x: Double, y: Double) extends Tower(x, y) {
  val typeid   = "c3"
  val strength = 20.0
  val radius   = 4.7
  val cooldown = 0.40
  val price    = 800
  val upgrade  = None
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("shot1.wav", 0.2)
    Buffer(new Bullet(this.pos.x, this.pos.y, this.strength, this.radius, target))
  }
}


/** Boomerang towers shoot boomerangs one at a time. */
class BoomerTower1(x: Double, y: Double) extends Tower(x, y) {
  val typeid   = "b1"
  val strength = 7.5
  val radius   = 3.3
  val cooldown = 2.0
  val price    = 600
  val upgrade  = Some(new BoomerTower2(x, y))
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("throw.wav", 0.2)
    Buffer(new Boomerang(this.pos.x, this.pos.y, this.strength, target, 0.25))
  }
}
class BoomerTower2(x: Double, y: Double) extends Tower(x, y) {
  val typeid   = "b2"
  val strength = 12.0
  val radius   = 4.0
  val cooldown = 1.40
  val price    = 500
  val upgrade  = None
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("throw.wav", 0.2)
    Buffer(new Boomerang(this.pos.x, this.pos.y, this.strength, target, 0.28))
  }
}


/** Homing towers shoot missiles at a slow pace, but deal great splash damage. */
class HomingTower1(x: Double, y: Double) extends Tower(x, y) {

  val typeid   = "h1"
  val strength = 40.0
  val radius   = 6.0
  val cooldown = 2.4
  val price    = 1200
  val upgrade  = Some(new HomingTower2(x, y))
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("shot2.wav", 0.2)
    Buffer(new Missile(this.pos.x, this.pos.y, this.strength, 2 * this.radius, 1.5, target))
  }
}
class HomingTower2(x: Double, y: Double) extends Tower(x, y) {

  val typeid   = "h2"
  val strength = 60.0
  val radius   = 6.6
  val cooldown = 1.90
  val price    = 1200
  val upgrade  = None
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("shot2.wav", 0.2)
    Buffer(new Missile(this.pos.x, this.pos.y, this.strength, 2 * this.radius, 2.5, target))
  }
}


/** Object that contains information about unlocking and buying towers. */
object TowerInfo {
  
  final val unlockCannon = 0
  final val unlockBoomer = 5
  final val unlockHoming = 10

  final val priceCannon = new CannonTower1(0, 0).price
  final val priceBoomer = new BoomerTower1(0, 0).price
  final val priceHoming = new HomingTower1(0, 0).price
  
}

