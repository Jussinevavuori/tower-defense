package game

class TowerShop {
  
  // Thw current tower being placed down
  var activeTower: Option[Tower] = None
  
  // True when a tower is being placed down
  def active: Boolean = this.activeTower.isDefined
  
  // Function to choose a tower to be the active tower by its id
  def choose(id: String, game: Game) = {
    val choice = id match {
        case "basic"  => new BasicTower(-1, -1)
        case "laser"  => new LaserTower(-1, -1)
        case "homing" => new HomingTower(-1, -1)
        case _ => throw new IllegalArgumentException("Unrecognized tower id")
    }
    if (this.activeTower.isEmpty && game.player.canAfford(choice.price)) {
      this.activeTower = Some(choice)
    } else {
      println("You could not afford this item")
    }
  }
  
  // Function to buy and place down the active tower
  def purchase(game: Game, x: Double, y: Double) = {
    if (this.activeTower.isDefined) {
      val purchased = this.activeTower.get
      this.activeTower = None
      purchased.pos.moveTo(Vec(x, y))
      game.player.charge(purchased.price)
      game.towers += purchased
    }
  }
  
  // Function to undo the chosen tower
  def forget() = {
    this.activeTower = None
  }
  
}