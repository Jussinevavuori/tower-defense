package game

import scala.collection.mutable.Buffer

/** A tower type is created followingly
  * 
  * {{{
  * 	
  * class TowerType(_x: Double, _y: Double)

  * 	extends Tower(_x, _y, name, strength, radius, cooldown, price, unlock level, upgrade)
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
  extends Tower(_x, _y, "c1", 10.0, 3.7, 1.00, 300,
      Some(new CannonTower2(_x, _y))) {
  
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("shot1.wav", 0.2)
    Buffer(new BulletProjectile(this.pos.x, this.pos.y, this.strength, this.radius, target))
  }
}
class CannonTower2(_x: Double, _y: Double)
  extends Tower(_x, _y, "c2", 15.0, 4.2, 0.75, 300) {
  
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("shot1.wav", 0.2)
    Buffer(new BulletProjectile(this.pos.x, this.pos.y, this.strength, this.radius, target))
  }
}



class BoomerangTower1(_x: Double, _y: Double)
  extends Tower(_x, _y, "b1", 5.0, 3.0, 2.0, 1000,
      Some(new BoomerangTower2(_x, _y)), 5) {
  
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("throw.wav", 0.2)
    Buffer(new BoomerangProjectile(this.pos.x, this.pos.y, this.strength, target, 0.25))
  }
}
class BoomerangTower2(_x: Double, _y: Double) 
  extends Tower(_x, _y, "b2", 5.0, 3.3, 1.6, 1600) {
  
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("throw.wav", 0.2)
    Buffer(new BoomerangProjectile(this.pos.x, this.pos.y, this.strength, target, 0.28))
  }
}





class HomingTower1(_x: Double, _y: Double)
  extends Tower(_x, _y, "h1", 20.0, 7, 1.9, 700,
      Some(new HomingTower2(_x, _y))) {
  
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("shot2.wav", 0.2)
    Buffer(new HomingProjectile(this.pos.x, this.pos.y, this.strength, 2 * this.radius, 1.5, target, 0.4, 0.002))
  }
}
class HomingTower2(_x: Double, _y: Double)
  extends Tower(_x, _y, "h2", 30.0, 7.5, 1.3, 350) {
  
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("shot2.wav", 0.2)
    Buffer(new HomingProjectile(this.pos.x, this.pos.y, this.strength, 2 * this.radius, 2.5, target, 0.4, 0.002))
  }
}

