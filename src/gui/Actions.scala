package gui

import game._

// Actions object defines useful functions for the GUI to interact with the game.
// Makes the GUI code much more readable

object Actions {
  
  def loadNextWave(g: Game) = {
    if (g.loadNextWave()) Audio.play("menu.wav") else error()
  }
  def buyCannonTower(g: Game) = {
    g.shop.choose("basic", g)
    if (g.shop.active) Audio.play("coin.wav") else error()
  }
  def buyRapidTower(g: Game) = {
    g.shop.choose("laser", g)
    if (g.shop.active) Audio.play("coin.wav") else error()
    }
  def buyHomingTower(g: Game) = {
    g.shop.choose("homing", g)
    if (g.shop.active) Audio.play("coin.wav") else error()
  }
  def purchaseTower(g: Game, x: Double, y: Double) = {
    if (g.shop.purchase(g, x, y)) {
        Audio.play("coincluster.wav")
        Audio.play("impact.wav")
    } else error()
  }
  def selectTower(g: Game, x: Double, y: Double): Option[Tower] = { 
    val selection = g.towers.find(t => Vec(x, y).distance(t.pos) < 0.6)
    if (selection.isDefined) Audio.play("menu.wav")
    selection
  }
  def findSelectableTower(g: Game, x: Double, y: Double): Option[Tower] = {
    g.towers.find(t => Vec(x, y).distance(t.pos) < 0.6)
  }
  def upgradeTower(g: Game, t: Option[Tower]): Option[Tower] = {
    if (t.isDefined) {
      val upgraded = g.shop.upgrade(g, t.get)
      if (upgraded.isDefined) Audio.play("fanfare.wav") else Audio.play("error.wav")
      upgraded
    } else None
  }
  def skipTitleScreen() = {
    Titlescreen.completed = true
    Titlescreen.fading = true
  }
  def activateGodmode(g: Game) = {
    g.player.reward(100000)
    g.player.heal(1000)
    Audio.play("fanfare.wav")
  }
  
  private def error() = Audio.play("error.wav")
}