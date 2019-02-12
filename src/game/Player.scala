package game

class Player( initialHealth: Int = 100,   // Default health on a new player is 100
              initialMoney: Int = 1000    // Default money on a new player is 1000
            ) {
  
  /* The player's health as an integer and a flag
   * to keep track whether the player is still alive.
   * Once the player dies, the player cannot be revived.
   */
  
  private var _health: Int = initialHealth
  private var _alive: Boolean = true
  
  
  /* Function to return the player's health and state
   * without the ability to alter it.
   */
  
  def health: Int     = this._health
  def alive:  Boolean = this._alive
  def dead:   Boolean = !this._alive
  
  
  /* Functions to damage or heal the player by the given amount
   * only if the player is still alive. Only positive integers.
   */
  
  def damage(amount: Int) =
    if (this.alive && amount > 0)
      this._health -= amount
      
  def heal(amount: Int) =
    if (this.alive && amount > 0)
      this._health += amount
  
  
  /* The player's money. Money can never go below zero.
   */
  
  private var _money: Int = initialMoney
  
  
  /* Function to return the player's money amount without
   * the ability to alter it.
   */
  
  def money: Int = this._money
  
  
  /* Decreases the player's money by the chosen amount if
   * possible and returns true. If the player does not have
   * enough money, returns false and does not change the
   * player's money amount. Only positive integers.
   */
  
  def charge(amount: Int): Boolean = {
    val valid = amount > 0 && this._money >= amount
    if (valid) this._money -= amount
    valid
  }
  
  
  /* Increase the player's money by the chosen amount.
   * Only positive integers.
   */
  
  def reward(amount: Int) = {
    if (amount > 0) this._money += amount
  }
    
  
  /* Returns true if player has enough money to purchase
   * the tower.
   */
  def canAfford(amount: Int) = {
    this._money >= amount
  }
    
  
  /* Returns a textual description of the player's current
   * status.
   */
  override def toString(): String = 
    s"Player: ${this._health} HP, ${this._money} $$"
  
}