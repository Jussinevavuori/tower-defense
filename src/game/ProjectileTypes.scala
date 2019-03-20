package game

import scala.math._
import scala.collection.mutable.Buffer

/* Bullet projectiles have a strength and a range. They are shot at a target
 * and continue moving in a straight line at high speed until they hit something
 * or exceed their range.
 */
class BulletProjectile(x: Double, y: Double, str: Double, rng: Double,
    target: Enemy)
      extends Projectile(x, y, str, rng) {
  
  // Calculating the initial velocity, which will not change direction or magnitude
  val velocity = (target.pos - this.pos)
  velocity.scaleTo(0.4)
  
  // Moving in the direction of the precalculated velocity
  def move(): Unit = this.pos += this.velocity
}




/* Homing projectiles calculate their velocity based on the target's current
 * position and effectively work as smart target seeking projectiles. If the
 * target is lost (for example upon its death) the projectile will continue
 * in a straight line until it goes outside its range or hits something.
 * Explodes upon impact
 */
class HomingProjectile(x: Double, y: Double, str: Double, rng: Double,
    val blastRadius: Double, val target: Enemy, private val maxSpeed: Double, private val acceleration: Double)
      extends Projectile(x, y, str, rng) {
  
  
  private var vel = Vec(0, 0)    // The current velocity, starts from rest
   
  def dir() = toDegrees(atan2(this.vel.y, this.vel.x))  // The direction of movement for rendering purposes

  def move(): Unit = this.pos += this.velocity  // Moving in the direction of the updated velocity
  
  // Function to calculate the current velocity
  def velocity = {
    var speed = this.vel.size          // The current speed
    if (this.target.alive) {           // As long as target is alive, update velocity direction
      this.vel = target.pos - this.pos // Calculate new direction
    }    
    this.vel.scaleTo {    // Accelerate
      speed + { if (speed < this.maxSpeed) this.acceleration else 0 }
    }
    this.vel    // Return the updated velocity
  }
  
  // Hits all the enemies in the blastradius upon impact
  override def hit(enemies: Buffer[Enemy]): Unit = {
    
    // If outside of range, finish
    if (this.pos.distanceSqrd(origin) > this.range * this.range) {
      this.isOutOfRange = true
    } else {
      for (e <- enemies.diff(this.hitEnemies)) {
        if (this.pos.distanceSqrd(e.pos) < e.size * e.size) {  // If this is close inside enemy
          enemies.filter(e => {                                // Hurt everyone within blastradius
            e.pos.distanceSqrd(this.pos) < this.blastRadius * this.blastRadius
          }).foreach(_.damage(this.damage))
          this.hitEnemies += e
          gui.Effects.addExplosionEffect(e)
          return  // Break loop after explosion
        }
      }
    }
  }
  
}



/* Boomerang projectiles start at a given speed, slow down until they reach
 * given distance and start accelerating backwards until they return to the
 * original shooter.
 */
class BoomerangProjectile(x: Double, y: Double, str: Double,
    val target: Enemy, private var speed: Double)
      extends Projectile(x, y, str, Double.MaxValue) {
  
  
  // The current angle of boomerang rotation in degrees
  var angle: Double = 0.0
  
  // Starting position
  private val startingSpeed = this.speed
  
  // The precalculated direction vector
  private val direction = this.target.pos - this.pos
  
  // The acceleration constant
  private val acceleration = 0.005
  
  // Function to move the projectile
  def move(): Unit = {
    // Spin
    this.angle += 6.0
    
    // Calculate new velocity
    this.speed -= this.acceleration
    val currentVel = Vec(this.direction.x, this.direction.y)
    currentVel.scaleTo(this.speed)
    
    // Allow enemies to be hit twice: reset upon apex
    if (this.speed * this.speed < this.acceleration) this.resetHitEnemies
    
    // Move
    this.pos += currentVel  
  }
  
  // This projectile only finishes when it returns to original shooter
  override def finished: Boolean = {
    this.speed < -1 * this.startingSpeed
  }
}





