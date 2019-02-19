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
  
  def animate(id: String, canvasX: Double, canvasY: Double,
              gridW: Double, gridH: Double, gfx: GraphicsContext,
              delay: Int = 20): Unit = {
    
    // Handle wrong ids
    if (!this.animations.contains(id)) throw new RenderingException(
        s"Given animation spritesheet as_$id.png does not exist in the data")
    
    // Spritesheet, sprite size, frame count
    val (ss, size, count) = this.animations(id)
    
    // Current frame
    val frame = this.currentFrame(count, delay)
    
    // Drawing the cropped image
    gfx.drawImage(ss, frame * size, 0, size, size, canvasX, canvasY, gridW, gridH)
  }
  
  
  
  // Called each frame to advance animation
  def advance() = this.frame += 1
  private var frame = 0
  
  // Returns the ongoing frame of an animation with the given length and delay
  private def currentFrame(length: Int, delay: Int) = { 
    (this.frame / delay) % length
  }
  
  
  
  // Shortcut to the animate method
  def apply(id: String, canvasX: Double, canvasY: Double,
            gridW: Double, gridH: Double, gfx: GraphicsContext,
            delay: Int = 20): Unit = {
    this.animate(id, canvasX, canvasY, gridW, gridH, gfx)
  }
  
  

  
  // All animation frames stored in a map so they don't have to be loaded each frame
  // Animation ID -> Spritesheet, Spritesize, Framecount
  private val animations = Map[String, (Image, Int, Int)]( 
    "koala"     -> this.loadAnimation("koala",     60, 2),
    "cannondog" -> this.loadAnimation("cannondog", 40, 2),
    "mage"      -> this.loadAnimation("mage",      60, 2),
    "towerup"   -> this.loadAnimation("towerup",   16, 7)
  )
  
  // Gets the animation frame for the given id of frames length animation
  private def loadAnimation(id: String, spriteSize: Int, frameCount: Int) = {
    val filepath = "assets/gfx/as_" + id + ".png"
    val inputStream = new FileInputStream(filepath)
    val animationSheet = new Image(inputStream)
    inputStream.close
    (animationSheet, spriteSize, frameCount)
  }
  
}






