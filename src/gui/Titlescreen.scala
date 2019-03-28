package gui

import java.io.FileInputStream
import scalafx.scene.image.Image
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color
import scalafx.scene.canvas.Canvas



/** Titlescreen controls the animated titlescreen that shows on startup
 *  on top of the main menu.
 */
object Titlescreen {
  
  /** Set to true, when the titlescreen is fully completed. */
  var completed: Boolean = false
  
  /** Set to true, when the titlescreen is starting to fade. */
  var fading = false
  
  /** The current opacity of the titlescreen. */
  var opacity = 1.0
  
  /** The current time. */
  private var time: Double = 0.0
  
  /** Timer settings for background (bg) and foreground (fg) fading times. */
  private val bgFadeinTime = 1.5
  private val fgFadeinTime = 1.5
  private val fgFadeoutTime = 1.5
  private val bgFadeinStart = 0.2
  private val fgFadeinStart = 1.0
  private val fgFadeoutStart = 4.0
  
  /** Timer settings for start of fadeout and ending. */
  private val endTime = 6.0
  private val fadeTime = 2.0
  
  /** Loading the background (bg) and foreground (fg) images. */
  private val bgFilepath = "assets/gfx/titlescreen_bg.png"
  private val fgFilepath = "assets/gfx/titlescreen_fg.png"
  private val bgInputStream = new FileInputStream(bgFilepath)
  private val fgInputStream = new FileInputStream(fgFilepath)
  private val bgImage = new Image(bgInputStream)
  private val fgImage = new Image(fgInputStream)
  bgInputStream.close()
  fgInputStream.close()
  
  /** The sound clip name. */
  private val sfx = "titlescreen.wav"
  
  /** The sound clip status. Set to true after clip played. */
  private var sfxPlayed = false
  
  /** The timing for the sound clip. */
  private val sfxPlayTime = 1.0
  
  /** Function to render and advance the animation. */
  def advance(canvas: Canvas, elapsedTime: Double): Unit = {

    // When the titlescreen has completed, do nothing
    if (this.completed) return 

    // Else update time by elapsed time
    this.time += elapsedTime
    
    // Check if completed
    this.completed = this.time > (this.endTime + this.fadeTime)
    
    // Check if fading
    this.fading = this.time > this.endTime
    
    // Get the graphics of the titlecanvas
    val gfx = canvas.graphicsContext2D
    
    // Set the opacity to fade for fadeTime seconds after endTime seconds
    this.opacity = 1.0 - math.max(0.0, ((this.time - this.endTime) / this.fadeTime))
   
    // Get the width of the stage
    val W = Main.stage.scene.value.getWidth
    
    // Get the height of the stage
    val H = Main.stage.scene.value.getHeight
    
    // Draw a black, opaque background
    gfx.setGlobalAlpha(1.0)
    gfx.fill = Color.Black
    gfx.fillRect(0, 0, W, H)
        
    // If background fade has started, draw the background at the correct alpha
    if (this.time > this.bgFadeinStart) {
      val alpha = math.min(1.0, ((this.time - this.bgFadeinStart) / this.bgFadeinTime ))
      gfx.setGlobalAlpha(alpha)
      gfx.drawImage(this.bgImage, 0, 0, W, H)
    }
    
    // Set the correct alpha for the foreground
    gfx.setGlobalAlpha {
      if (this.time > this.fgFadeoutStart) {
        1.0 - { 1.0 min ((this.time - this.fgFadeoutStart) / this.fgFadeoutTime ) }
      } else if (this.time > fgFadeinStart) {
        1.0 min ((this.time - this.fgFadeinStart) / this.fgFadeinTime)
      } else 0
    }
   
    // Draw the background image
    gfx.drawImage(this.fgImage, 0, 0, W, H)
    
    // Play the sound effect once at the correct time.
    if (this.time > this.sfxPlayTime && !this.sfxPlayed) {
      this.sfxPlayed = true
      Audio.play(this.sfx)
    }
  }
}












