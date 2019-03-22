package gui

import game._

// Actions object defines useful functions for the GUI to interact with the game.
// Makes the GUI code much more readable

object Actions {
  
  // Automatically asks the game to load a new wave
  def loadNextWave(g: Game) = {
    if (g.loadNextWave()) Audio.play("menu.wav") else error()
  }
  
  // Methods to choose towers
  def buyCannonTower(g: Game) = {
    g.shop.choose("c1", g)
    if (g.shop.active) Audio.play("coin.wav") else error()
  }
  def buyBoomerangTower(g: Game) = {
    g.shop.choose("b1", g)
    if (g.shop.active) Audio.play("coin.wav") else error()
    }
  def buyHomingTower(g: Game) = {
    g.shop.choose("h1", g)
    if (g.shop.active) Audio.play("coin.wav") else error()
  }
  
  // Methods to purchase bought tower at the given game location
  def purchaseTower(g: Game, x: Double, y: Double) = {
    if (g.shop.purchase(g, x, y)) {
        Audio.play("coincluster.wav")
        Audio.play("impact.wav")
    } else error()
  }
  
  // Method to select a tower at the given game location and return it
  // along the dimensions for the upgrade graphic if necessary
  def selectTower(g: Game, _x: Double, _y: Double): (Option[Tower], Double, Double) = { 
    val selection = g.towers.find(t => Vec(_x, _y).distance(t.pos) < 0.6)
    var (x, y) = (0.0, 0.0)
    if (selection.isDefined) {
      Audio.play("menu.wav")
      if (selection.get.upgrade.isDefined) {
        val pos = selection.get.pos
        x = (Render.gridW * (pos.x - 0.45) * 1920) / Main.stage.scene.value.getWidth
        y = (Render.gridH * (pos.y - 1.25) * 1080) / Main.stage.scene.value.getHeight
      }
    }
    (selection, x, y)
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
    Titlescreen.completed = true
    Titlescreen.fading = true
  }
  
  // Method to activate godmode
  def activateGodmode(g: Game) = {
    g.player.reward(100000)
    g.player.heal(1000)
    Audio.play("fanfare.wav")
  }
  
  // Method to toggle fast forward
  def toggleFastForward(g: Game, setting: Boolean) = {
    g.toggleFastForward(setting)
  }
  
  // Shortcut to playing the error sound upon failure
  private def error() = Audio.play("error.wav")
}