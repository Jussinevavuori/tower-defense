package game

class BulletProjectile(_x: Double, _y: Double, strength: Double, target: Enemy)
  extends Projectile(_x, _y, strength) {
  
  val velocity = (target.pos - this.pos)
  velocity.scaleTo(0.4)
  
  def move(): Unit = this.pos += this.velocity
  
}

class HomingProjectile(_x: Double, _y: Double, strength: Double, val target: Enemy)
  extends Projectile(_x, _y, strength) {
  
  var latestTargetPos = target.pos
  def velocity = {
    if (this.target.alive) this.latestTargetPos = target.pos
    val vel = this.latestTargetPos - this.pos
    vel.scaleTo(0.3)
    vel
  }
  
  def move(): Unit = this.pos += this.velocity
  
}