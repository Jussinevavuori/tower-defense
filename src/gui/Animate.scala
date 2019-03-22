package gui

import game._
import scalafx.scene.canvas.Canvas
import scala.collection.mutable.Map
import scalafx.scene.image.Image
import java.io.FileInputStream
import scalafx.scene.canvas.GraphicsContext
import java.io.File

/* Animate can be called by the Render object
 * to render animated sprites on the canvas
 */

object Animate {
  
  
  /* Animates the sprite animation
   * 
   * @param	 The id or the filename of the image to be animated
   * @param	 The amount of frames should have
   * @param	 The x coordinate for the image to be drawn to on the canvas
   * @param	 The y coordinate for the image to be drawn to on the canvas
   * @param	 The width of a single grid unit
   * @param	 The height of a single grid unit
   * @param	 The graphics context used for drawing
   * @param	 The delay for each frame. Optional
   */
  
  def animate(id: String, canvasX: Double, canvasY: Double, gfx: GraphicsContext,
              delay: Int = 20, currentFrame: Int = this.frame): Unit = {
    
    // Handle wrong ids
    if (!this.animations.contains(id)) throw new RenderingException(
        s"Given animation spritesheet as_$id.png does not exist in the data")
    
    // Spritesheet, sprite size, frame count
    val (ss, w, h, count) = this.animations(id)
    
    // Current frame
    val frame = this.currentFrame(count, delay, currentFrame)
    
    // Drawing the cropped image
    gfx.drawImage(ss, frame * w, 0, w, h, canvasX, canvasY, w * Render.resizeW, h * Render.resizeW)
  }
  
  
  
  // Called each frame to advance animation
  def advance() = this.frame += 1
  var frame = 0
  
  // Returns the ongoing frame of an animation with the given length and delay
  private def currentFrame(length: Int, delay: Int, currentFrame: Int = this.frame) = { 
    (currentFrame / delay) % length
  }
  
  // Shortcut to the animate method
  def apply(id: String, canvasX: Double, canvasY: Double, gfx: GraphicsContext,
            delay: Int = 20, currentFrame: Int = this.frame): Unit = {
    this.animate(id, canvasX, canvasY, gfx)
  }
  
  

  
  // All animation frames stored in a map so they don't have to be loaded each frame
  // Animation ID -> Spritesheet, Spritewidth, Spriteheight, Framecount
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
  
  // Gets the animation frame for the given id of frames length animation
  private def loadAnimation(id: String, spriteHeight: Int, spriteWidth: Int, frameCount: Int) = {
    val filepath = "assets/gfx/as_" + id + ".png"
    val inputStream = new FileInputStream(filepath)
    val animationSheet = new Image(inputStream)
    inputStream.close
    (animationSheet, spriteHeight, spriteWidth, frameCount)
  }
  
}






