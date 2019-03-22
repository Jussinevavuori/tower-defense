package game

import scala.collection.mutable.Buffer

/** A tower type is created followingly
  * 
  * {{{
  * 	
  * class TowerType(_x: Double, _y: Double)

  * 	extends Tower(_x, _y, id, strength, radius, cooldown, price, upgrade)
  * 
  * 	def generateProjectiles: Buffer[Projectile]
  * 
  * }}}
  * 
  */

class CannonTower1(_x: Double, _y: Double)
  extends Tower(_x, _y, "c1", 10.0, 3.7, 1.00, 300, Some(new CannonTower2(_x, _y))) {
  
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("shot1.wav", 0.2)
    Buffer(
      new BulletProjectile(this.pos.x, this.pos.y, this.strength, this.radius, target))
  }
}
class CannonTower2(_x: Double, _y: Double)
  extends Tower(_x, _y, "c2", 16.0, 4.2, 0.66, 500, Some(new CannonTower3(_x, _y))) {  
  
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("shot1.wav", 0.2)
    Buffer(
      new BulletProjectile(this.pos.x, this.pos.y, this.strength, this.radius, target))
  }
}
class CannonTower3(_x: Double, _y: Double)
  extends Tower(_x, _y, "c3", 20.0, 4.7, 0.40, 800) {
  
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("shot1.wav", 0.2)
    Buffer(
      new BulletProjectile(this.pos.x, this.pos.y, this.strength, this.radius, target))
  }
}



class BoomerangTower1(_x: Double, _y: Double)
  extends Tower(_x, _y, "b1", 5.0, 3.0, 2.0, 600, Some(new BoomerangTower2(_x, _y))) {
  
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("throw.wav", 0.2)
    Buffer(
        new BoomerangProjectile(this.pos.x, this.pos.y, this.strength, target, 0.25))
  }
}
class BoomerangTower2(_x: Double, _y: Double) 
  extends Tower(_x, _y, "b2", 10.0, 4.0, 1.4, 1000) {
  
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("throw.wav", 0.2)
    Buffer(
      new BoomerangProjectile(this.pos.x, this.pos.y, this.strength, target, 0.28))
  }
}





class HomingTower1(_x: Double, _y: Double)
  extends Tower(_x, _y, "h1", 40.0, 7, 2.4, 1200, Some(new HomingTower2(_x, _y))) {
  
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("shot2.wav", 0.2)
    Buffer(
      new HomingProjectile(this.pos.x, this.pos.y, this.strength, 2 * this.radius, 1.5, target, 0.4, 0.002))
  }
}
class HomingTower2(_x: Double, _y: Double)
  extends Tower(_x, _y, "h2", 60.0, 7.5, 1.9, 1200) {
  
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("shot2.wav", 0.2)
    Buffer(
      new HomingProjectile(this.pos.x, this.pos.y, this.strength, 2 * this.radius, 2.5, target, 0.4, 0.002))
  }
}



// Contains the unlock levels and prices
object TowerInfo {
  
  final val unlockCannon    = 0
  final val unlockBoomerang = 5
  final val unlockHoming    = 10

  final val priceCannon    = new CannonTower1(0, 0).price
  final val priceBoomerang = new BoomerangTower1(0, 0).price
  final val priceHoming    = new HomingTower1(0, 0).price
  
}

