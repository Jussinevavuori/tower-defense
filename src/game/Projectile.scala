package game

/** Abstract class projectile describes projectiles, which are shot by towers and can
 *  hit and damage enemies. Each different extending projectile moves and damages
 *  enemies in differents ways. A projectile is initialized with coordinates for its
 *  position, amount of damage it can deal and range it can fly.
 */
abstract class Projectile(x: Double, y: Double, val damage: Double, val range: Double) {
  
  /** The projectiles current position. */
  val pos = Vec(x, y)
  
  /** The projectiles original position. */
  val origin = Vec(x, y)
  
  /** Set to true once projectile flies out of range. */
  var outOfRange = false

  /** Returns true when the projectile is finished and can be removed. */
  def finished: Boolean = this.outOfRange || this.hitEnemies.nonEmpty
  
  /** Contains all the hit enemies, specifically made for boomerangs */
  var hitEnemies = Set[Enemy]()
  
  /** Resets set of hit enemies, allowing for enemies to be hit twice. */
  def resetHitEnemies = this.hitEnemies = Set[Enemy]()
  
  /** Function for all implementing projectile types to implement for moving. */
  def move(): Unit
  
  /** Hits all enemies that haven't been already hit that are within range */
  def hit(enemies: Iterator[Enemy]): Unit = {
    
    this.outOfRange = this.pos.distanceSqrd(origin) > range * range
    
    if (!this.outOfRange) {
      
      enemies.foreach(e => {

        val withinRadius = this.pos.distanceSqrd(e.pos) < e.size * e.size
        val notYetHit    = !this.hitEnemies.contains(e)
        
        if (withinRadius && notYetHit) {
          
          e.damage(this.damage)
          this.hitEnemies = this.hitEnemies + e
          gui.Audio.play("hit.wav", 0.1)
          return  // Break loop
        }
      })
    }
  }
  
  

  
}