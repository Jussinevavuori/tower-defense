package gui

import scalafx.scene.Scene
import scalafx.animation.AnimationTimer
import scalafx.scene.control.SeparatorMenuItem

/** An animation scene is a base for all scenes in the program, for easy interaction. */
abstract class AnimationScene extends Scene {
  
  /** Function that is called each time before anything else when the animation scene is loaded up. */
  def loadUp() = ()
  
  /** Function that is called each time before the animation scene is stooped. */
  def shutDown() = ()
  
  /** Function to stop the current scene's animation. */
  def stop()  = this.animation.stop() 
  
  /** Function to start the current scene's animation. */
  def start() = this.animation.start()
  
  /** The animation timer of the current scene: to be implemented by extending classes. */
  var animation: AnimationTimer
  
  /** Shortcut to generate new menu separators for animation scene menus. */
  def sep() = new SeparatorMenuItem
  
  /** Resizing function. */
  def resize(W: Double, H: Double)
}