package game

import scala.collection.mutable.Buffer

/** Towers represents the objects on the player field that the player can purchase and place
 *  to automatically shoot at the enemies to stop them from reaching the end.
 */
abstract class Tower(x: Double, y: Double) {
  
  /** The tower's position. */
  val pos: Vec = Vec(x, y)
  
  /** The tower class' unique type id for each implementing tower type. */
  val typeid: String
  
  /** The tower's strength (dealt damage) for each implementing tower type. */
  val strength: Double
  
  /** The tower's radius of vision for each implementing tower type. */
  val radius: Double
  
  /** The tower's radius squared for lighter calculations while upgrading. */
  lazy val radiusSqrd = this.radius * this.radius
  
  /** The tower's cooldown period for shooting  for each implementing tower type. */
  val cooldown: Double
  
  /** The tower's price for each implementing tower type. */
  val price: Int
  
  /** The tower (if any) to which this tower can be upgraded to for each implementing tower type. */
  val upgrade: Option[Tower]
  
  /** The tower's current target. */
  var target: Option[Enemy] = None
  
  /** Returns true if the tower has a target. */
  def hasTarget: Boolean = this.target.isDefined
  
  /** Flag that is set to true, once this tower is upgraded so this tower can be removed. */
  var upgraded = false
  
  /** List of all projectiles this tower generates each time it shoots for each implementing tower type. */
  def generateProjectiles(target: Enemy): Buffer[Projectile]
    
  /** The tower's current "heat" which has to return to zero before tower can shoot again. */
  private var heat = 0.0
  
  /** Method to attempt update the current target. */
  def updateTarget(enemies: Iterator[Enemy]): Unit = {
    
    // Check if this tower has a viable target
    val hasViableTarget: Boolean = {
      this.target.isDefined && this.target.get.alive &&
      this.target.get.pos.distanceSqrd(this.pos) < radiusSqrd
    }

    // Reset target if has no current viable target
    if (!hasViableTarget) { this.target = None }
    
    // Attempt to find a new target
    while (!hasViableTarget && enemies.hasNext) {
      val candidate = enemies.next
      if (candidate.pos.distanceSqrd(this.pos) < radiusSqrd) {
        this.target = Some(candidate)
        return
      }
    }
  }

  /** Method to shoot. Returns a buffer that contains all the projectiles that were shot. */
  def shoot(elapsedTime: Double): Buffer[Projectile] = {
    if (this.heat <= 0 && this.target.isDefined) {
      this.heat = this.cooldown
      this.generateProjectiles(this.target.get)
    }
    else {
      this.heat -= elapsedTime
      Buffer()
    }
  }
}



