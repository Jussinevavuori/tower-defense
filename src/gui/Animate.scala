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
  
  def animate(id: String, x: Double, y: Double,
              w: Double, h: Double, gfx: GraphicsContext,
              delay: Int = this.frequency) = {
    
    val animation = this.animations(id)
    val frame = this.currentFrame(animation.length, delay)
    gfx.drawImage(animation(frame), x, y, w, h)
    
  }
  
  // Called each frame to advance animation
  def advance() = this.frame += 1
  
  private var frame = 0      // Current frame
  private var frequency = 20 // Animation speed
  
  // Returns the current frame for an animation of a given length
  private def currentFrame(frames: Int, delay: Int) = { 
    (this.frame / delay) % frames
  }
  
  
  // Shortcut to the animate method
  def apply(id: String, canvasX: Double, canvasY: Double,
            gridW: Double, gridH: Double, gfx: GraphicsContext) = {
    this.animate(id, canvasX, canvasY, gridW, gridH, gfx)
  }
  
  
  // Gets the animation frame for the given id of frames length animation
  private def frame(id: String, frame: Int): Image = {
    val filepath = "assets/gfx/" + id + "-" + frame + ".png"
    val inputStream = new FileInputStream(filepath)
    val image = new Image(inputStream)
    inputStream.close
    image
  }
  
  
  // All animation frames stored in a map so they don't have to be loaded each frame
  private val animations = Map[String, Array[Image]](
    "cannondog" ->
      Array(this.frame("cannondog", 0), this.frame("cannondog", 1)),
    "towerupParticle" ->
      Array(this.frame("towerupParticle", 0), this.frame("towerupParticle", 1),
            this.frame("towerupParticle", 2), this.frame("towerupParticle", 3),
            this.frame("towerupParticle", 4), this.frame("towerupParticle", 5),
            this.frame("towerupParticle", 6))
  )
}






