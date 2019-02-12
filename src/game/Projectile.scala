package game

import scala.collection.mutable.Buffer

abstract class Projectile(_x: Double, _y: Double, damage: Double) {
  
  val pos = Vec(_x, _y)  // The position of the vector
  
  var finished = false  // True when projectile has hit
  
  def hit(enemies: Buffer[Enemy]): Unit = { // Try to hit an enemy
    
    for (e <- enemies) {
      
      // If this is close inside enemy
      if (this.pos.distanceSqrd(e.pos) < e.size * e.size) {
        e.damage(this.damage)
        this.finished = true
        return
      } 
    }
  }
  
  def move(): Unit
  
}