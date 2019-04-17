package gui

import game._

/** Actions contains functions that the GUI can access and modify the game and program
 *  with simpler syntax.
 */
object Actions {
  
  /** Loads the next wave to the game. */
  def loadNextWave(g: Game) = {
    if (g.loadNextWave()) Audio.play("menu.wav") else error()
    this.checkLocks()
    Render.startFade()
  }
  
  /** Saves the current game. */
  def save() = {
    GameSaver.save(Main.currentGame)
    Audio.play("iosfx.wav")
  }
  
  /** Method to buy a new cannon tower. */
  def buyCannonTower(g: Game, godmode: Boolean) = {
    if (g.wave.number >= TowerInfo.unlockCannon || godmode) {
      g.shop.choose("c1")
      if (g.shop.active) Audio.play("coin.wav") else error()
    } else error()
  }
  
  /** Method to buy a new boomerang tower. */
  def buyBoomerTower(g: Game, godmode: Boolean) = {
    if (g.wave.number >= TowerInfo.unlockBoomer || godmode) {
      g.shop.choose("b1")
      if (g.shop.active) Audio.play("coin.wav") else error()
    } else error()
  }
  
  /** Method to buy a new homing tower. */
  def buyHomingTower(g: Game, godmode: Boolean) = {
    if (g.wave.number >= TowerInfo.unlockHoming || godmode) {
      g.shop.choose("h1")
      if (g.shop.active) Audio.play("coin.wav") else error()
    } else error()
  }
  
  /** Method to purchase the current active tower. */
  def purchaseTower(g: Game, x: Double, y: Double) = {
    if (g.shop.purchase(x, y)) {
      Audio.play("coincluster.wav")
      Audio.play("impact.wav")
    } else error()
  }
  
  /** Attempts to select and return a tower from the game with coordinates for the upgrade box. */
  def selectTower(g: Game, _x: Double, _y: Double): (Option[Tower], Double, Double) = { 
    val selection = g.towers.find(t => Vec(_x, _y).distance(t.pos) < 0.6)
    var (x, y) = (0.0, 0.0)
    if (selection.isDefined) {
      Audio.play("menu.wav")
      if (selection.get.upgrade.isDefined) {
        val pos = selection.get.pos
        x = (Render.gridW * (pos.x - 0.45) * 1920) / Main.stage.scene.value.getWidth
        y = (Render.gridH * (pos.y - 1.05) * 1080) / Main.stage.scene.value.getHeight
      }
    }
    (selection, x, y)  // Returns position for offset upgrade button and option[tower]
  }
  
  /** Method that tries to select a tower. */
  def findSelectableTower(g: Game, x: Double, y: Double): Option[Tower] = {
    g.towers.find(t => Vec(x, y).distance(t.pos) < 0.6)
  }
  
  /** Method to upgrade given tower. */
  def upgradeTower(g: Game, t: Option[Tower]): Option[Tower] = {
    if (t.isDefined) {
      val upgraded = g.shop.upgrade(t.get)
      if (upgraded.isDefined) {
        Audio.play("fanfare.wav") 
        Effects.addTowerupEffect(upgraded.get)
      } else error()
      upgraded
    } else None
  }
  
  /** Method to skip the title screen. */
  def skipTitleScreen() = {
    MainMenuScene.titleCanvas.visible = false
    Titlescreen.completed = true
    Titlescreen.fading = true
  }
  
  /** Method to activate godmode. */
  def activateGodmode(g: Game) = {
    g.player.reward(100000)
    g.player.heal(1000)
    Audio.play("fanfare.wav")
    InGameScene.godmode = true
    InGameScene.b_lock1.visible = false
    InGameScene.b_lock2.visible = false
    InGameScene.b_lock3.visible = false
  }
  
  /** Method to toggle fast forward. */
  def toggleFastForward(g: Game, setting: Boolean) = {
    g.toggleFastForward(setting)
  }
  
  /** Method to start a new game. */
  def newGame() = {
    Main.loadGame(GameLoader.loadNewGame())
    this.resetSettings()
  }
  
  /** Method to load the saved game. */
  def loadGame() = {
    Main.loadGame(GameLoader.loadSavedGame())
    Main.currentGame.setEmptyWave()
    this.resetSettings()
  }
  
  /** Method to reset all game settings to initial. */
  def resetSettings() = {
    Music.stopLoop()
    Music.startLoop()
    Audio.play("iosfx.wav")
    InGameScene.godmode = false
    InGameScene.b_upgrd.visible = false
    InGameScene.b_lock1.visible = false
    InGameScene.b_lock2.visible = true
    InGameScene.b_lock3.visible = true
    InGameScene.gameoverCanvas.visible = false
  }
  
  /** Method to check the locks for the upgrade buttons. */
  def checkLocks() = {
    InGameScene.b_lock1.visible = TowerInfo.unlockCannon > Main.currentGame.wave.number && !InGameScene.godmode
    InGameScene.b_lock2.visible = TowerInfo.unlockBoomer > Main.currentGame.wave.number && !InGameScene.godmode
    InGameScene.b_lock3.visible = TowerInfo.unlockHoming > Main.currentGame.wave.number && !InGameScene.godmode
  }
  
  /** Shortcut to playing the error sound. */
  private def error() = Audio.play("error.wav")
}





