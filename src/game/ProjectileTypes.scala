package game

class BulletProjectile(_x: Double, _y: Double,
    strength: Double, range: Double, target: Enemy)
  extends Projectile(_x, _y, strength, range) {
  
  val velocity = (target.pos - this.pos)
  velocity.scaleTo(0.4)
  
  def move(): Unit = this.pos += this.velocity
  
}

class HomingProjectile(_x: Double, _y: Double,
    strength: Double, range: Double, val target: Enemy,
    val maxSpeed: Double, val acceleration: Double)
  extends Projectile(_x, _y, strength, range) {
  
  var speed = 0.02
  var latestTargetPos = target.pos
  def velocity = {
    if (this.target.alive) this.latestTargetPos = target.pos
    val vel = this.latestTargetPos - this.pos
    vel.scaleTo(this.speed)
    if (this.speed < this.maxSpeed) this.speed *= this.acceleration
    vel
  }
  
  def move(): Unit = this.pos += this.velocity
  
}