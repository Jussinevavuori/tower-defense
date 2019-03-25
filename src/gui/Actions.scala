package gui

import game._

// Actions object defines useful functions for the GUI to interact with the game.
// Makes the GUI code much more readable

object Actions {
  
  
  
  // Automatically asks the game to load a new wave
  def loadNextWave(g: Game) = {
    if (g.loadNextWave()) Audio.play("menu.wav") else error()
    this.checkLocks()
  }
  
  
  // Save the game
  def save() = {
    GameSaver.save(Main.currentGame)
    Audio.play("iosfx.wav")
  }
  
  
  // Methods to choose towers
  def buyCannonTower(g: Game, godmode: Boolean) = {
    if (g.wave.number >= TowerInfo.unlockCannon || godmode) {
      g.shop.choose("c1", g)
      if (g.shop.active) Audio.play("coin.wav") else error()
    } else error()
  }
  def buyBoomerangTower(g: Game, godmode: Boolean) = {
    if (g.wave.number >= TowerInfo.unlockBoomerang || godmode) {
      g.shop.choose("b1", g)
      if (g.shop.active) Audio.play("coin.wav") else error()
    } else error()
  }
  def buyHomingTower(g: Game, godmode: Boolean) = {
    if (g.wave.number >= TowerInfo.unlockHoming || godmode) {
      g.shop.choose("h1", g)
      if (g.shop.active) Audio.play("coin.wav") else error()
    } else error()
  }
  
  
  
  
  // Methods to purchase bought tower at the given game location
  def purchaseTower(g: Game, x: Double, y: Double) = {
    if (g.shop.purchase(g, x, y)) {
        Audio.play("coincluster.wav")
        Audio.play("impact.wav")
    } else error()
  }
  
  
  
  // Selects a tower from the map
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
  
  
  
  // Method to find a selectable tower at the given game location and return it
  def findSelectableTower(g: Game, x: Double, y: Double): Option[Tower] = {
    g.towers.find(t => Vec(x, y).distance(t.pos) < 0.6)
  }
  
  
  
  // Method to upgrade selected tower
  def upgradeTower(g: Game, t: Option[Tower]): Option[Tower] = {
    if (t.isDefined) {
      val upgraded = g.shop.upgrade(g, t.get)
      if (upgraded.isDefined) {
        Audio.play("fanfare.wav") 
        Effects.addTowerupEffect(upgraded.get)
      } else error()
      upgraded
    } else None
  }
  
  
  
  // Method to skip the title screen
  def skipTitleScreen() = {
    MainMenuScene.titleCanvas.visible = false
    Titlescreen.completed = true
    Titlescreen.fading = true
  }
  
  
  
  // Method to activate godmode
  def activateGodmode(g: Game) = {
    g.player.reward(100000)
    g.player.heal(1000)
    Audio.play("fanfare.wav")
    InGameScene.godmode = true
    InGameScene.b_lock1.visible = false
    InGameScene.b_lock2.visible = false
    InGameScene.b_lock3.visible = false
  }
  
  
  
  // Method to toggle fast forward
  def toggleFastForward(g: Game, setting: Boolean) = {
    g.toggleFastForward(setting)
  }
  
  
  // Method to start new game
  def newGame() = {
    Main.loadGame(GameLoader("data/defaultdata.xml"))
    this.resetSettings()
  }
  
  
  // Method to load saved game
  def loadGame() = {
    Main.loadGame(GameLoader("data/savedata.xml"))
    this.resetSettings()
  }
  
  
  // Private method to reset settings
  private def resetSettings() = {
    Music.stopLoop()
    Music.startLoop()
    InGameScene.godmode = false
    InGameScene.b_upgrd.visible = false
    InGameScene.b_lock1.visible = false
    InGameScene.b_lock2.visible = true
    InGameScene.b_lock3.visible = true
    Audio.play("iosfx.wav")
    InGameScene.gameoverCanvas.disable = true
    InGameScene.gameoverCanvas.visible = false
  }
  
  
  // Method to check visual upgrade locks
  def checkLocks() = {
    InGameScene.b_lock1.visible = {
      TowerInfo.unlockCannon    > Main.currentGame.wave.number && !InGameScene.godmode
    }
    InGameScene.b_lock2.visible = {
      TowerInfo.unlockBoomerang > Main.currentGame.wave.number && !InGameScene.godmode
    }
    InGameScene.b_lock3.visible = {
      TowerInfo.unlockHoming    > Main.currentGame.wave.number && !InGameScene.godmode
    }
  }
  
  // Shortcut to playing the error sound upon failure
  private def error() = Audio.play("error.wav")
}





