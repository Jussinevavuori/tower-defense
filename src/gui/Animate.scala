package gui

import game._
import scalafx.scene.canvas.Canvas
import scala.collection.mutable.Map
import scalafx.scene.image.Image
import java.io.FileInputStream
import scalafx.scene.canvas.GraphicsContext
import java.io.File

/** Animate contains functions necessary to animate graphics from animation sheets. */
object Animate {
  
  /** Animates a chosen sprite with the given id to the given canvas (x, y)-coordinates
   *  using the given gfx, animating with a given framedelay (by default 20 frames).
   */
  def animate(id: String, cx: Double, cy: Double, gfx: GraphicsContext, delay: Int = 20, frame: Int = this.frame): Unit = {
    
    // Handle wrong ids
    if (!this.animations.contains(id)) throw new RenderingException(
        s"Given animation spritesheet as_$id.png does not exist in the data")
    
    // Spritesheet, sprite width and height, frame count
    val (ss, w, h, count) = this.animations(id)
    
    // Current sprite's frame number
    val f = this.currentFrame(count, delay, frame)
    
    // Drawing the cropped image
    gfx.drawImage(ss, f * w, 0, w, h, cx, cy, w * Render.resizeW, h * Render.resizeW)
  }
  
  /** The current frame count. */
  var frame = 0
  
  /** Function to be called each frame to advance the animations. */
  def advance() = this.frame += 1

  /** Returns the index of the current frame of an animation with the given length delay. */
  private def currentFrame(len: Int, delay: Int, f: Int = this.frame) = (f / delay) % len
  
  /** Shortcut to the animate method. */
  def apply(id: String, canvasX: Double, canvasY: Double, gfx: GraphicsContext, delay: Int = 20): Unit = {
    this.animate(id, canvasX, canvasY, gfx)
  }
  
  /** All the animation sheets stored in a map by their ids (ID -> Spritesheet, width, height, length). */
  private val animations = Map[String, (Image, Int, Int, Int)]( 
    "koala1"     -> this.loadAnimation("koala1",       60,   60, 2),
    "koala2"     -> this.loadAnimation("koala2",       60,   60, 2),
    "cannondog1" -> this.loadAnimation("cannondog1",   60,   60, 2),
    "cannondog2" -> this.loadAnimation("cannondog2",   60,   60, 2),
    "cannondog3" -> this.loadAnimation("cannondog3",   60,   60, 2),
    "panda1"     -> this.loadAnimation("panda1",       60,   60, 2),
    "towerup"    -> this.loadAnimation("towerup",      16,   16, 7),
    "explosion"  -> this.loadAnimation("explosion",    60,   60, 6),
    "gameover"   -> this.loadAnimation("gameover",   1920, 1080, 2)
  )
  
  /** Loads an animation and returns all it's attributes: spritesheet, width, height and length. */
  private def loadAnimation(id: String, spriteHeight: Int, spriteWidth: Int, frameCount: Int) = {
    val filepath = "assets/gfx/as_" + id + ".png"
    val inputStream = new FileInputStream(filepath)
    val animationSheet = new Image(inputStream)
    inputStream.close
    (animationSheet, spriteHeight, spriteWidth, frameCount)
  }
  
}






