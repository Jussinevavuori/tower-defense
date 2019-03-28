package game

import scala.math._


/** Bullets move in a straight line at a constant speed.
 */
class Bullet(x: Double, y: Double, dmg: Double, rng: Double, target: Enemy) extends Projectile(x, y, dmg, rng) {
  
  /** The calculated direction, scaled to a constant velocity. */
  private val velocity = (target.pos - this.pos)
  velocity.scaleTo(0.4)
  
  /** Always moves a constant step. */
  def move(): Unit = this.pos += this.velocity
}



/** Boomerang projectiles start at high speed, slow down and accelerate backwards, returning
 *  to the origin and damaging several enemies.
 */
class Boomerang(x: Double, y: Double, str: Double, trg: Enemy, spd: Double) extends Projectile(x, y, str, Double.MaxValue) {
  
  /** The boomerang's current speed. */
  private var speed = spd
  
  /** The boomerang's starting speed. */
  private val startingSpeed = spd
  
  /** The boomerang's starting direction. */
  private val direction = trg.pos - this.pos
  
  /** The boomerang's acceleration constant. */
  private val acc = 0.005
  
  /** The boomerang's current angle rotation in degrees. */
  var angle: Double = 0.0
  
  /** Boomerangs only finish upon returning to their origin. */
  override def finished: Boolean = this.speed < -1 * this.startingSpeed
  
  /** Move and accelerate backwards in an arc. */
  def move(): Unit = this.pos += {
    
    this.angle += 6.0
    this.speed -= this.acc
    if (speed * speed < acc * acc) this.resetHitEnemies

    val currentVel = Vec(this.direction.x, this.direction.y)
    currentVel.scaleTo(this.speed)

    currentVel
  }
}



/** Missiles home in on their targets, accelerating to a max speed and damaging several
 *  near-by enemies upon impact.
 */
class Missile(x: Double, y: Double, dmg: Double, rng: Double, br: Double, trg: Enemy) extends Projectile(x, y, dmg, rng) {
  
  /** The maximum speed constant of a missile. */
  val maxSpeed = 0.4
  
  /** The acceleration constant of a missile. */
  val acc = 0.002
  
  /** The missile's current target enemy, towards which it accelerates. */
  val target = trg
  
  /** The missile's blast radius, inside of which it damages enemies upon impact. */
  val blastRadius = br
  
  /** The missile's current direction in degrees. */
  def dir() = toDegrees(atan2(this.vel.y, this.vel.x))

  /** The current velocity. */
  private var vel = Vec(0, 0)
  
  /** Accelerate until max speed and steer towards target. */
  def move(): Unit = this.pos += {
    
    // The current speed magnitude
    val speed = this.vel.size
    
    // If target alive, set direction towards target
    if (this.target.alive) {
      this.vel = target.pos - this.pos
    }
    
    // Set speed to accelerated speed until maxspeed
    if (this.vel.size != 0) {
      this.vel.scaleTo { speed + { if (speed < maxSpeed) acc else 0 } }
    }
    
    // Return the velocity for moving
    this.vel
  }

  /** Hits all enemies within the blast radius upon impact */
  override def hit(enemyIterator: Iterator[Enemy]): Unit = {
    
    val enemiesCopy = enemyIterator.toArray
    val enemies = enemiesCopy.iterator
    
    this.outOfRange = this.pos.distanceSqrd(origin) > range * range
    
    if (!this.outOfRange) {
      enemies.foreach(e => {
        if (this.pos.distanceSqrd(e.pos) < e.size * e.size) {          
          enemiesCopy
            .filter(_.pos.distanceSqrd(this.pos) < this.blastRadius * this.blastRadius)
            .foreach(ee => {
              ee.damage(this.damage)
            })
          this.hitEnemies = Set(e)
          gui.Effects.addExplosionEffect(e)
          return
        }
      })
    }
  }
  
}





