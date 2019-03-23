package gui

import java.io.FileInputStream
import scalafx.scene.image.Image
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color
import scalafx.scene.canvas.Canvas


/*
 *  Titlescreen controls the titlescreen
 *  graphics, drawing and animation.
 */


object Titlescreen {
  
  // Timer
  var completed: Boolean = false
  var fading = false
  var opacity = 1.0
  private var time: Double = 0.0
  
  // Timer settings
  private val bgFadeinTime = 1.5
  private val fgFadeinTime = 1.5
  private val fgFadeoutTime = 1.5
  
  private val bgFadeinStart = 0.2
  private val fgFadeinStart = 1.0
  private val fgFadeoutStart = 4.0
  
  private val endTime = 6.0
  private val fadeTime = 2.0
  
  // Load the images
  private val bgFilepath = "assets/gfx/titlescreen_bg.png"
  private val fgFilepath = "assets/gfx/titlescreen_fg.png"
  private val bgInputStream = new FileInputStream(bgFilepath)
  private val fgInputStream = new FileInputStream(fgFilepath)
  private val bgImage = new Image(bgInputStream)
  private val fgImage = new Image(fgInputStream)
  bgInputStream.close()
  fgInputStream.close
  
  // Sound settings
  private val sfx = "titlescreen.wav"
  private var sfxPlayed = false
  private val sfxPlayTime = 1.0
  
  // Advance animation
  def advance(canvas: Canvas, elapsedTime: Double): Unit = {

    if (this.completed) return 

    this.time += elapsedTime
    this.completed = this.time > (this.endTime + this.fadeTime)
    this.fading = this.time > this.endTime
    
    val gfx = canvas.graphicsContext2D
    
    this.opacity = 1.0 - math.max(0.0, ((this.time - this.endTime) / this.fadeTime))
   
    val W = Main.stage.scene.value.getWidth
    val H = Main.stage.scene.value.getHeight
    
    gfx.setGlobalAlpha(1.0)
    gfx.fill = Color.Black
    gfx.fillRect(0, 0, W, H)
        
    if (this.time > this.bgFadeinStart) {
      val alpha = math.min(1.0, ((this.time - this.bgFadeinStart) / this.bgFadeinTime ))
      gfx.setGlobalAlpha(alpha)
      gfx.drawImage(this.bgImage, 0, 0, W, H)
    }
    
    val alpha = {
      if (this.time > this.fgFadeoutStart) {
        1.0 - { 1.0 min ((this.time - this.fgFadeoutStart) / this.fgFadeoutTime ) }
      } else if (this.time > fgFadeinStart) {
        1.0 min ((this.time - this.fgFadeinStart) / this.fgFadeinTime)
      } else 0
    }
   
    gfx.setGlobalAlpha(alpha)
    gfx.drawImage(this.fgImage, 0, 0, W, H)
    
    if (this.time > this.sfxPlayTime && !this.sfxPlayed) {
      this.sfxPlayed = true
      Audio.play(this.sfx)
    }
  }
}












