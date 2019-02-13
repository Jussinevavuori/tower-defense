package gui

import game._


object Buttonactions {
  
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
  
  private def error() = Audio.play("error.wav")
}