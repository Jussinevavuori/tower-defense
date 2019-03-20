package game

/* Enemies are represented by an abstract enemy class.
 * Each of the different enemytypes extend this class.
 * 
 * Each enemy type has its own specific health, strength
 * and speed stats.
 */

abstract class Enemy(
    val         typeid:  String,
    private var _health: Double,
    val         speed:   Double,
    val         size:    Double,
    val         reward:  Int,
    _x: Double, _y: Double,
    var         target:  Option[Path] ) {
  
  
  /* The position of the enemy.
   */
  
  val pos: Vec = Vec(_x, _y)
  
  
  /* Maximum health of the enemy, also the initial health.
   */
  
  val maxHealth = _health
  
  
  /* Is set to true when enemy has reached goal and cannot
   * move any more
   */
  
  var finished = false
  
  
  /* Function that when called moves the enemy towards its
   * next target. Returns true if the enemy reaches the goal,
   * false otherwise.
   */
  
  def advance(elapsedTime: Double): Boolean = {
    
    // Do not do anything else, but return true, when the enemy reaches
    // the last path segment. Consequences are handled elsewhere.
    if (this.target.isEmpty) {
      this.finished = true
      this._health = 0
      return true
    }
    
    // Else, calculate the direction vector to the target and scale
    // it to the enemy's speed to get a velocity vector
    val velocity: Vec = this.target.get.pos - this.pos
    
    // Only scale whenever the magnitude of velocity is greater than the speed
    if (velocity.size > this.speed) {
      velocity.scaleTo(this.speed)
    }
    
    // When velocity is effectively zero, enemy has reached the target.
    // Now the enemy requests its current target for the next target
    if (velocity.size < 0.000001) {
      this.target = this.target.get.next
    }
    
    // Move the enemy by the direction vector
    this.pos += velocity
    
    // By default return false
    false
    
  }

  
  /* Function to access the enemy's health without modifying
   * it.
   */
  
  def health: Double = this._health
  
  
  /* Function to check if enemy is alive.
   */
  
  def alive: Boolean = this._health > 0

  
  /* Function to check if enemy is dead.
   */
  
  def dead: Boolean = this._health <= 0
  
  
  
  /* Function for the enemy to take damage by a given amount.
   * Only positive numbers allowed.
   */
  
  def damage(amount: Double): Unit = {
    if (amount > 0) this._health -= amount
  }
  
  /* Function that is performed upon the death of this enemy.
   * The function returns an array of enemies, which are defined
   * for each enemy type seperately. Those defined enemies are
   * then spawned into the game upon death.
   */
  
  def death(): Array[Enemy]
  
  
  /* Returns a textual description of the enemy
   */
  
  override def toString() = s"${this.typeid} (${this._health} HP) @ (${this.pos.x}, ${this.pos.y})"

}



