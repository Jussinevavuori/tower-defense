package game

import scala.collection.mutable.Buffer

/** A tower type is created followingly
  * 
  * {{{
  * 	
  * class TowerType(_x: Double, _y: Double)
  * 	extends Tower(_x, _y, name, strength, radius, cooldown, price)
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

class BasicTower(_x: Double, _y: Double)
  extends Tower(_x, _y, "basic", 10.0, 5.5, 0.8, 500) {
  
  def generateProjectiles(target: Enemy) = {
    Buffer(new BulletProjectile(this.pos.x, this.pos.y, this.strength, target))
  }
}

class LaserTower(_x: Double, _y: Double)
  extends Tower(_x, _y, "laser", 2.0, 3.5, 0.02, 800) {
  
  def generateProjectiles(target: Enemy) = {
    Buffer(new BulletProjectile(this.pos.x, this.pos.y, this.strength, target))
  }
}

class HomingTower(_x: Double, _y: Double)
  extends Tower(_x, _y, "homing", 20.0, 7, 1.4, 1000) {
  
  def generateProjectiles(target: Enemy) = {
    Buffer(new HomingProjectile(this.pos.x, this.pos.y, this.strength, target))
  }
}