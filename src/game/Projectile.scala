package game

import scala.collection.mutable.Buffer

abstract class Projectile(_x: Double, _y: Double, damage: Double, range: Double) {
  
  // The current and original position of the projectile
  val pos = Vec(_x, _y)
  val origin = Vec(_x, _y)
  
  // Methods for testing the state of the projectile
  var isOutOfRange = false  // True when projectile is out of radius
  def hasHitEnemy  = !this.hitEnemies.isEmpty  // True when projectile has hit an enemy
  
  // Default finished condition
  def finished: Boolean = this.isOutOfRange || this.hasHitEnemy
  
  // Hits all enemies that haven't been already hit that are within range
  def hit(enemies: Buffer[Enemy]): Unit = { // Try to hit an enemy
    
    // If outside of range, finish
    if (this.pos.distanceSqrd(origin) > range * range) {
      this.isOutOfRange = true
    } else {
      for (e <- enemies.diff(this.hitEnemies)) {
        
        // If this is close inside enemy
        if (this.pos.distanceSqrd(e.pos) < e.size * e.size) {
          e.damage(this.damage)
          this.hitEnemies += e
          gui.Audio.play("hit.wav", 0.1)
          return
        }
      }
    }
  }
  
  // Contains all the hit enemies, specifically made for boomerangs
  private var hitEnemies = Buffer[Enemy]()
  
  // Resets hit enemies upon calling, allowing for enemies to be hit twice
  def resetHitEnemies = this.hitEnemies = Buffer[Enemy]()
  
  // Function for projectile types to implement
  def move(): Unit
  
}