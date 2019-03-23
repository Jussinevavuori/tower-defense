package game

abstract class Projectile(_x: Double, _y: Double, val damage: Double, val range: Double) {
  
  
  /* The current and original position of the projectile
  */
  val pos = Vec(_x, _y)
  val origin = Vec(_x, _y)
  
  
  /* Methods for testing the state of the projectile
   */
  var isOutOfRange = false
  def hasHitEnemy  = !this.hitEnemies.isEmpty
  def finished: Boolean = this.isOutOfRange || this.hasHitEnemy
  
  
  /* Hits all enemies that haven't been already hit that are within range
   */
  def hit(enemies: Seq[Enemy]): Unit = { // Try to hit an enemy
    
    if (this.pos.distanceSqrd(origin) > range * range) {  // If outside of range, finish
      
      this.isOutOfRange = true
    
    } else {
      
      for (e <- enemies) {
        
        val withinRadius = this.pos.distanceSqrd(e.pos) < e.size * e.size
        val notYetHit    = !this.hitEnemies.contains(e)
        
        if (withinRadius && notYetHit) {
          
          e.damage(this.damage)
          this.hitEnemies = e +: this.hitEnemies
          gui.Audio.play("hit.wav", 0.1)
          return  // Break loop
        }
      }
    }
  }
  
  
  /* Contains all the hit enemies, specifically made for boomerangs
   */
  var hitEnemies = Seq[Enemy]()
  
  
  /* Resets hit enemies upon calling, allowing for enemies to be hit twice
   */
  def resetHitEnemies = this.hitEnemies = Seq[Enemy]()
  
  
  /* Function for projectile types to implement
   */
  def move(): Unit
  
}