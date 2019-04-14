package game


/* Enemies are represented by an abstract class that contains most of the enemies
 * functionality.
 * 
 * Upon creating an enemy, an initial position and target must be given.
 * 
 * The functions and values typeid, speed, size, reward and death must be
 * implemented by each extending enemytype class.
 */
abstract class Enemy(x: Double, y: Double, var target: Option[Path] ) {
  
  /** A unique string for each extending enemy type. */
  val typeid: String
  
  /** The speed of this enemy for each extending enemy type. */
  val speed: Double
  
  /** The size of each extending enemy type. */
  val size: Double
  
  /** The reward awarded to the player upon killing this enemy for each extending enemy type. */
  val reward: Int
  
  /** Maximum health of the enemy for each extending enemy type..*/
  def maxhp: Double
  
  /** The position of the enemy as a position vector. */
  val pos: Vec = Vec(x, y)
  
  /** The enemy's current health. */
  private var _health = this.maxhp
  
  /** Is set to true when enemy has reached goal and cannot move any more */
  var finished = false
    
  /** Returns the enemy's current health. */
  def health: Double = this._health
  
  /** Returns true when the enemy is alive */
  def alive: Boolean = this._health > 0.0
  
  /** Returns true when the enemy is dead */
  def dead: Boolean = !this.alive
  
  /** Minimum speed threshold under which the enemy is considered to have stopped. */
  def minimumSpeed = 1.0 / 1e7
  
  /** Function that damages the enemy by the given (positive) amount */
  def damage(amount: Double): Unit = {
    if (amount > 0) this._health -= amount
  }
  
  /** Function that contains all the enemies spawned upon this enemy's death
   *  for each extending enemy type individually. */
  def death(): Iterator[Enemy]
  
  /** Function that moves the enemy. Returns true if the enemy reaches the goal */
  def advance(elapsedTime: Double): Boolean = {
        
    // When the enemy has no target, it has reached the end. Return true.
    if (this.target.isEmpty) {
      this.finished = true
      this._health = 0
      return true
    }
    
    // Calculate the velocity towards the next target and limit it to the enemy's speed
    val velocity: Vec = this.target.get.pos - this.pos
    val scaledSpeed = this.speed * 60 * elapsedTime
    velocity.limit(scaledSpeed)
    
    // When the enemy stops, it has reached its target and asks the target for the next target
    if (velocity.size < minimumSpeed) {
      this.target = this.target.get.next
    }
    
    // Move the enemy by the direction vector
    this.pos += velocity
    
    // By default return false
    return false
  }

  /** Returns a textual description of the enemy */
  override def toString() = s"${this.typeid} (${this._health} HP) @ (${this.pos.x}, ${this.pos.y})"

}



