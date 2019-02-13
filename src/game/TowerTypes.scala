package game

import scala.collection.mutable.Buffer

/** A tower type is created followingly
  * 
  * {{{
  * 	
  * class TowerType(_x: Double, _y: Double)
<<<<<<< HEAD
  * 	extends Tower(_x, _y, name, strength, radius, cooldown, price)
=======
  * 	extends Tower(_x, _y, name, strength, radius, cooldown, price, upgrade)
>>>>>>> 0f16db16ba94389b4d22fec28e1a327d9ba7c676
  * 
  * 	def generateProjectiles: Buffer[Projectile]
  * 
  * }}}
  * 
  * Where... 
  * - The _x, _y parameters specify where the tower is placed.
  * - Name is a string which describes the tower's name
  * - Strength is a double which describes how much damage the tower
  * 	inflicts on enemies
  * - Radius is a double which describes how far the tower can spot
  * 	and shoot enemies
  * - Cooldown is amount of cooldown between shots in seconds
  * - GenerateProjectiles returns the projectiles this tower generates
  * 	upon shooting
  */

class CannonTower1(_x: Double, _y: Double)
  extends Tower(_x, _y, "basic", 12.0, 5.5, 0.9, 600,
      Some(new CannonTower2(_x, _y))) {
  
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("shot1.wav", 0.2)
    Buffer(new BulletProjectile(this.pos.x, this.pos.y, this.strength, this.radius, target))
  }
}
class CannonTower2(_x: Double, _y: Double)
  extends Tower(_x, _y, "basic", 18.0, 5.8, 0.8, 500, None) {
  
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("shot1.wav", 0.2)
    Buffer(new BulletProjectile(this.pos.x, this.pos.y, this.strength, this.radius, target))
  }
}



class RapidTower1(_x: Double, _y: Double)
  extends Tower(_x, _y, "laser", 1.5, 3.5, 0.12, 1200,
      Some(new RapidTower2(_x, _y))) {
  
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("shot3.wav", 0.2)
    Buffer(new BulletProjectile(this.pos.x, this.pos.y, this.strength, this.radius,target))
  }
}
class RapidTower2(_x: Double, _y: Double)
  extends Tower(_x, _y, "laser", 1.8, 3.8, 0.10, 800, None) {
  
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("shot3.wav", 0.2)
    Buffer(new BulletProjectile(this.pos.x, this.pos.y, this.strength, this.radius,target))
  }
}



class HomingTower1(_x: Double, _y: Double)
  extends Tower(_x, _y, "homing", 20.0, 7, 1.4, 1000,
      Some(new HomingTower2(_x, _y))) {
  
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("shot2.wav", 0.2)
    Buffer(new HomingProjectile(this.pos.x, this.pos.y, this.strength, 2 * this.radius, target, 0.3, 0.0001))
  }
}
class HomingTower2(_x: Double, _y: Double)
  extends Tower(_x, _y, "homing", 30.0, 7.5, 1.3, 1000, None) {
  
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("shot2.wav", 0.2)
    Buffer(new HomingProjectile(this.pos.x, this.pos.y, this.strength, 2 * this.radius, target, 0.3, 0.0001))
  }
}