package game

import scala.collection.mutable.Buffer

abstract class Projectile(_x: Double, _y: Double, damage: Double, range: Double) {
  
  val pos = Vec(_x, _y)  // The position of the vector
  
  val origin = Vec(_x, _y) // The original point
  
  var finished = false  // True when projectile has hit or is out of radius
  
  def hit(enemies: Buffer[Enemy]): Unit = { // Try to hit an enemy
    
    // If outside of range, finish
    if (this.pos.distanceSqrd(origin) > range * range) {
      this.finished = true
    } else {
      for (e <- enemies) {
        
        // If this is close inside enemy
        if (this.pos.distanceSqrd(e.pos) < e.size * e.size) {
          e.damage(this.damage)
          this.finished = true
          return
        } 
      }
    }
  }
  
  def move(): Unit
  
}