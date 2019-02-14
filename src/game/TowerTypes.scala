package game

import scala.collection.mutable.Buffer

/** A tower type is created followingly
  * 
  * {{{
  * 	
  * class TowerType(_x: Double, _y: Double)

  * 	extends Tower(_x, _y, name, strength, radius, cooldown, price, upgrade)
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
  extends Tower(_x, _y, "c1", 10.0, 3.7, 1.00, 600,
      Some(new CannonTower2(_x, _y))) {
  
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("shot1.wav", 0.2)
    Buffer(new BulletProjectile(this.pos.x, this.pos.y, this.strength, this.radius, target))
  }
}
class CannonTower2(_x: Double, _y: Double)
  extends Tower(_x, _y, "c2", 12.5, 4.0, 0.90, 400,
    Some(new CannonTower3(_x, _y))) {
  
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("shot1.wav", 0.2)
    Buffer(new BulletProjectile(this.pos.x, this.pos.y, this.strength, this.radius, target))
  }
}
class CannonTower3(_x: Double, _y: Double)
  extends Tower(_x, _y, "c3", 17.0, 4.3, 0.80, 600,
    Some(new CannonTower4(_x, _y))) {
  
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("shot1.wav", 0.2)
    Buffer(new BulletProjectile(this.pos.x, this.pos.y, this.strength, this.radius, target))
  }
}
class CannonTower4(_x: Double, _y: Double)
  extends Tower(_x, _y, "c4", 20.0, 4.6, 0.70, 1000, None) {
  
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("shot1.wav", 0.2)
    Buffer(new BulletProjectile(this.pos.x, this.pos.y, this.strength, this.radius, target))
  }
}



class RapidTower1(_x: Double, _y: Double)
  extends Tower(_x, _y, "r1", 1.8, 2.8, 0.12, 800,
      Some(new RapidTower2(_x, _y))) {
  
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("shot3.wav", 0.2)
    Buffer(new BulletProjectile(this.pos.x, this.pos.y, this.strength, this.radius,target))
  }
}
class RapidTower2(_x: Double, _y: Double)
  extends Tower(_x, _y, "r2", 2.2, 3.2, 0.10, 600,
      Some(new RapidTower3(_x, _y))) {
  
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("shot3.wav", 0.2)
    Buffer(new BulletProjectile(this.pos.x, this.pos.y, this.strength, this.radius,target))
  }
}
class RapidTower3(_x: Double, _y: Double)
  extends Tower(_x, _y, "r3", 2.6, 3.6, 0.08, 900,
    Some(new RapidTower4(_x, _y))) {
  
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("shot3.wav", 0.2)
    Buffer(new BulletProjectile(this.pos.x, this.pos.y, this.strength, this.radius,target))
  }
}
class RapidTower4(_x: Double, _y: Double)
  extends Tower(_x, _y, "r4", 3.0, 4.0, 0.06, 1500, None) {
  
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("shot3.wav", 0.2)
    Buffer(new BulletProjectile(this.pos.x, this.pos.y, this.strength, this.radius,target))
  }
}


class HomingTower1(_x: Double, _y: Double)
  extends Tower(_x, _y, "h1", 20.0, 7, 1.4, 700,
      Some(new HomingTower2(_x, _y))) {
  
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("shot2.wav", 0.2)
    Buffer(new HomingProjectile(this.pos.x, this.pos.y, this.strength, 2 * this.radius, target, 0.4, 1.1))
  }
}
class HomingTower2(_x: Double, _y: Double)
  extends Tower(_x, _y, "h2", 30.0, 7.5, 1.3, 350, None) {
  
  def generateProjectiles(target: Enemy) = {
    gui.Audio.play("shot2.wav", 0.2)
    Buffer(new HomingProjectile(this.pos.x, this.pos.y, this.strength, 2 * this.radius, target, 0.4, 1.1))
  }
}