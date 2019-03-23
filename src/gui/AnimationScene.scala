package gui

import scalafx.scene.Scene
import scalafx.animation.AnimationTimer

/*
 * An animation scene has an animation loop and functionality with program
 * status to help unnecessary animations run in the background when they
 * are not being displayed.
 */

abstract class AnimationScene extends Scene {
  
  def stop()  = this.animation.stop() 
  def start() = this.animation.start()
  var animation: AnimationTimer
}