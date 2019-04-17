package game

class TowerShop(var game: Game) {
  
  /** The current bought tower that is being placed down if any. */
  var activeTower: Option[Tower] = None
  
  /** True when the user has an active tower being bought. */
  def active: Boolean = this.activeTower.isDefined
  
  /** Function to create and choose an active tower based on the typeid. */
  def choose(id: String) = {
    val choice = id match {
        case "c1" => new CannonTower1(-1, -1)
        case "b1" => new BoomerTower1(-1, -1)
        case "h1" => new HomingTower1(-1, -1)
        case _ => throw new IllegalArgumentException("Unrecognized tower id")
    }
    if (this.activeTower.isEmpty && game.player.canAfford(choice.price)) {
      this.activeTower = Some(choice)
    } else {
      gui.Audio.play("error.wav")
    }
  }
  
  /** Function to place the active tower down at the chosen location and buy it. Returns true when succesful. */
  def purchase(x: Double, y: Double): Boolean = {
    if (this.activeTower.isDefined && game.isValidSpot(x, y) && (y < game.rows - 1)) {
      val purchased = this.activeTower.get
      this.activeTower = None
      purchased.pos.moveTo(Vec(x, y))
      game.player.charge(purchased.price)
      game.towers += purchased
      game.sortTowers()
      true
    } else false
  }
  
  /** Function to upgrade the selected tower. Returns the upgraded tower if any. */
  def upgrade(tower: Tower): Option[Tower] = {
    if (tower.upgrade.isDefined && game.player.canAfford(tower.upgrade.get.price)) {
      val upgraded = tower.upgrade.get
      tower.upgraded = true
      game.towers += upgraded
      upgraded.pos.moveTo(tower.pos)
      game.sortTowers()
      game.player.charge(upgraded.price)
      Some(upgraded)
    } else {
      None
    }
  }
}