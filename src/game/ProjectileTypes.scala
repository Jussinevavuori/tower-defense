package game

import scala.math._
import scala.collection.mutable.Buffer



/* Bullet projectile is the simplest projectile. It has a predetermined
 * velocity direction and magnitude and moves according to that velocity
 * until out of range or hits an enemy
 */

class BulletProjectile(x: Double, y: Double, str: Double, rng: Double,
    target: Enemy)
      extends Projectile(x, y, str, rng) {
  
  private val velocity = (target.pos - this.pos)
  velocity.scaleTo(0.4)
  
  def move(): Unit = this.pos += this.velocity
}




/* Homing projectiles explode upon impact damaging several enemies at once.
 * As long as homing projectiles have a valid target, they steer in the 
 * direction of the target. Homing projectiles accelerate to a max velocity
 */
class HomingProjectile(x: Double, y: Double, str: Double, rng: Double,
    val blastRadius: Double, val target: Enemy, val maxSpeed: Double, val acceleration: Double)
      extends Projectile(x, y, str, rng) {
  
  
   
  def dir() = toDegrees(atan2(this.vel.y, this.vel.x))

  def move(): Unit = this.pos += this.velocity
  
  private var vel = Vec(0, 0)
  def velocity = {
    
    var speed = this.vel.size            // Current speed magnitude
    
    if (this.target.alive) {             // As long as target is alive, update
      this.vel = target.pos - this.pos   // direction of movement
    }
    
    this.vel.scaleTo {                   // Scale movement to accelerate until max speed
      speed + { 
        if (speed < maxSpeed) acceleration else 0
      }
    }
    
    this.vel
  }

  
  /* Hits all enemies that haven't been already hit that are within range
   */
  override def hit(enemies: Seq[Enemy]): Unit = { // Try to hit an enemy
    
    if (this.pos.distanceSqrd(origin) > range * range) {  // If outside of range, finish
      
      this.isOutOfRange = true
    
    } else {
      
      for (e <- enemies) {
        
        val withinRadius = this.pos.distanceSqrd(e.pos) < e.size * e.size
        val notYetHit    = !this.hitEnemies.contains(e)
        
        if (withinRadius && notYetHit) {
          
          val radius = this.blastRadius * this.blastRadius
          
          val withinBlastRadius = enemies.filter(e => {
            val distance = e.pos.distanceSqrd(this.pos)
            distance < radius
          })
          
          withinBlastRadius.foreach(_.damage(this.damage))
          
          this.hitEnemies = this.hitEnemies ++ withinBlastRadius
          
          gui.Effects.addExplosionEffect(e)
          
          return  // Break loop
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
    val target: Enemy, var speed: Double)
      extends Projectile(x, y, str, Double.MaxValue) {
  
  
  /* The current angle of boomerang rotation in degrees
   */
  var angle: Double = 0.0
  
  
  /* The precalculated starting speed, direction and acceleration
   */
  private val startingSpeed = this.speed
  private val direction = this.target.pos - this.pos
  private val acceleration = 0.005
  
  
  /* Function to move the projectile in an arc
   */
  def move(): Unit = {
    
    this.angle += 6.0
    this.speed -= this.acceleration
    val currentVel = Vec(this.direction.x, this.direction.y)
    currentVel.scaleTo(this.speed)
    
    // All enemies can be hit twice, hits reset at apex
    val hitApex = speed * speed < acceleration * acceleration
    if (hitApex) this.resetHitEnemies
    
    this.pos += currentVel  
  }
  
  
  /* This projectile only finishes when it returns to original shooter
   */
  override def finished: Boolean = {
    this.speed < -1 * this.startingSpeed
  }
}





