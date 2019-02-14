package game

class TowerShop {
  
  // Thw current tower being placed down
  var activeTower: Option[Tower] = None
  
  // True when a tower is being placed down
  def active: Boolean = this.activeTower.isDefined
  
  // Function to choose a tower to be the active tower by its id
  def choose(id: String, game: Game) = {
    val choice = id match {
        case "basic"  => new CannonTower1(-1, -1)
        case "laser"  => new RapidTower1(-1, -1)
        case "homing" => new HomingTower1(-1, -1)
        case _ => throw new IllegalArgumentException("Unrecognized tower id")
    }
    if (this.activeTower.isEmpty && game.player.canAfford(choice.price)) {
      this.activeTower = Some(choice)
    } else {
      println("You could not afford this item")
    }
  }
  
  // Function to buy and place down the active tower
  def purchase(game: Game, x: Double, y: Double): Boolean = {
    if (this.activeTower.isDefined & game.isValidSpot(x, y)) {
      val purchased = this.activeTower.get
      this.activeTower = None
      purchased.pos.moveTo(Vec(x, y))
      game.player.charge(purchased.price)
      game.towers += purchased
      true
    } else false
  }
  
  // Function to upgrade the chosen tower
  def upgrade(game: Game, tower: Tower): Option[Tower] = {
    if (tower.upgrade.isDefined && game.player.canAfford(tower.upgrade.get.price)) {
      tower.upgraded = true
      game.towers += tower.upgrade.get
      tower.upgrade.get.pos.moveTo(tower.pos)
      game.player.charge(tower.upgrade.get.price)
      tower.upgrade
    } else {
      None
    }
  }
  
  // Function to undo the chosen tower
  def forget() = {
    this.activeTower = None
  }
  
}