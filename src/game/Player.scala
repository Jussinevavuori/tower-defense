package game

/** A player describes the player of a game. A player consists of health and money
 *  and functions to handle health and money.
 */
class Player(initialHealth: Int = 100, initialMoney: Int = 1000) {
  
  /** The players current health. */
  private var _health: Int = initialHealth
  
  /** The players current health, cutting at zero. */
  def health: Int = this._health max 0
  
  /** Returns true when the player is alive. */
  def alive: Boolean = this._health > 0
  
  /** Returns true when the player is dead. */
  def dead: Boolean = !this.alive
  
  /** Damages the player by the given positive amount. */
  def damage(amount: Int) = if (amount > 0) this._health -= amount
  
  /** Heals the player by the given poistive amount, if the player is alive. */
  def heal(amount: Int) = if (this.alive && amount > 0) this._health += amount
  
  /** The player's money. Money can never go below zero. */
  private var _money: Int = initialMoney
  
  /** The player's money. */
  def money: Int = this._money
  
  /** Rewards the player the given positive amount of money. */
  def reward(amount: Int) = if (amount > 0) this._money += amount
    
  /** Returns true if player has enough money. */
  def canAfford(amount: Int) = this._money >= amount
    
  /** If the player has enough money, deducts the given positive amount from the player. 
   *  Returns true if charge succeeded (player had enough money). */
  def charge(amount: Int): Boolean = {
    val valid = amount > 0 && this._money >= amount
    if (valid) this._money -= amount
    valid
  }

  /** Returns a textual description of the player's current status. */
  override def toString(): String = s"Player: ${this._health} HP, ${this._money} $$"
  
}