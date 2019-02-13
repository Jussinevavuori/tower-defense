package game

import scala.collection.mutable.Buffer

/* Towers represent the towers that the player can
 * purchase and place on the battlefield. The towers
 * automatically shoot at the enemies, when they get
 * near enough.
 * 
 * Each different tower type extends the Tower class.
 */

abstract class Tower(_x: Double, _y: Double,
                     val typeid:   String,
                     val strength: Double,
                     val radius:   Double,
                     val cooldown: Double,
                     val price:    Int,
                     val upgrade:  Option[Tower]) {
  
  
  /* The position of a tower
   */
  
  val pos: Vec = Vec(_x, _y)
  
  
  /* Each tower may have a target enemy it tries to
   * shoot at. If it doesn't, it is constantly looking
   * for an enemy, which is within its radius.
   */
  
  var target: Option[Enemy] = None
  
  
  /* Returns true if the tower has a target.
   */
  
  def hasTarget: Boolean = this.target.isDefined
  
  
  /* Flag that is set to true when the tower will is upgraded
   * to delete the tower
   */
  
  var upgraded = false
  
  /* The method the tower uses to find its targets
   * if it doesnt already have one.
   */
  
  def updateTarget(enemies: Buffer[Enemy]) = {
    
    // The radius, squared for lighter calculated comparison
    val radiusSqrd = this.radius * this.radius
    
    // Check if this tower has a viable target
    val hasViableTarget: Boolean = {
      this.target.isDefined &&
      this.target.get.alive &&
      (this.target.get.pos.distanceSqrd(this.pos) < radiusSqrd)
    }
    
    // If this tower has a viable target, do nothing. Else, find a new target
    // and remove the current target
    if (!hasViableTarget) {
      
      // Remove current target
      this.target = None
    
      // Find all viable enemies that are within range
      val viable = enemies.filter(_.pos.distanceSqrd(this.pos) < radiusSqrd)
      
      // Only if there are viable enemies, pick one. Else, let target remain unchaned
      if (!viable.isEmpty) {
      
        // It is irrelevant, which one we pick, so we pick the first viable enemy
        this.target = Some(viable.head)
      
      }
    }
  }
  
  
  /* Method that 'shoots' and damages the target enemy
   */
  
  private var heat = 0.0
  def shoot(elapsedTime: Double): Buffer[Projectile] = {
    
    // If ready to shoot and has target, shoot
    if (this.heat <= 0 && this.target.isDefined) {
      
      this.heat = this.cooldown  // Reset cooldown timer
      this.generateProjectiles(this.target.get)
      
    } else {
      this.heat -= elapsedTime // When not shooting, cool down
      Buffer()
    }
  }
  
  /* Method for generating projectiles upon the shot */
  
  def generateProjectiles(target: Enemy): Buffer[Projectile]
  
  
  /* Returns true when the tower has just shot. Used for rendering
   * purposes.
   */
  
  def hasShot = this.heat == this.cooldown
  
    
}



