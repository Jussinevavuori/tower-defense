package gui

import scalafx.scene.canvas.Canvas
import scalafx.scene.text.TextAlignment
import scalafx.scene.text.Font
import scalafx.scene.paint.Color
import javafx.event.{ EventHandler => EH }
import scalafx.event.{ ActionEvent => AE }
import javafx.scene.input.{ MouseEvent => ME }
import javafx.scene.input.{ ScrollEvent => SE }
import scalafx.Includes.eventClosureWrapperWithParam
import scalafx.Includes.jfxActionEvent2sfx
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import java.awt.event.KeyAdapter;

/** An input bar is a custom movable element, like the default button, but instead it functions
 *  as a responsive input field, into which the user can type.
 */
class InputBar(defaultText: String = "", _x: Double, _y: Double) extends Canvas(640, 64) {
  
  /** The coordinates of this input bar. */
  var x = _x
  var y = _y
  
  /** The current value of the text field. */
  var value: String = ""
  
  /** Clears the value. */
  def clear(): Unit = this.value = ""
    
  /** List of allowed characters. */
  private val allowed = "abcdefghijklmnopqrstuvwxyzäöåABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÅ1234567890"
  
  /** The image. */
  private val image = ImageLoader("inputBar")
  
  /** The graphics of the canvas. */
  private val gfx = this.graphicsContext2D
  
  /** The current text size. */
  var txtSize = 40
  
  /** Variables for flashing functionality. */
  private var frameCount = 0
  private def flashing: Boolean = (frameCount / 40) % 2 == 0

  /** Setting the graphics font and text alignment. */
  this.gfx.font = Font.loadFont("file:assets/font/gamegirl.ttf", txtSize)
  this.gfx.textAlign = TextAlignment.Center
  
  /** Function to automatically resize the button. */
  override def resize(W: Double, H: Double) = {
    this.width = 640 * (W / 1920)
    this.height = 64 * (H / 1080)
    this.translateX = this.x * (W / 1920)
    this.translateY = this.y * (H / 1080)
    this.gfx.font = Font.loadFont("file:assets/font/gamegirl.ttf", txtSize * (H / 1080))
    this.render()
  }

  /** Function to render the button onto the canvas */
  def render() = {
    val W = this.getWidth
    val H = this.getHeight
    val text = if (this.focused.value || this.value.nonEmpty) this.value else defaultText
    this.gfx.clearRect(0, 0, Int.MaxValue, Int.MaxValue)
    this.gfx.drawImage(this.image, 0, 0, W, H)
    this.gfx.fill = Color(0.0, 0.0, 0.0, {if (this.focused.value) 1.0 else 0.4})
    this.gfx.fillText(text, W / 2, 0.75 * H, W)
    this.frameCount += 1
    if (this.flashing && this.focused.value) {
      this.gfx.fillText("|", W * (59 * value.length / 1920.0 + 0.51), 0.75 * H, W)
    }
  }
  
  /** Request for focus. */
  this.setOnMousePressed(new EH[ME] { def handle(e: ME) = requestFocus() })
  
  /** On key pressed. */
  this.onKeyPressed = new EH[KeyEvent] {
    def handle(ke: KeyEvent) = {
      if (ke.getCode == KeyCode.BACK_SPACE) { 
        value = value.dropRight(1)
      } else if (ke.getCode == KeyCode.SPACE) {
        value = value + "_"
      } else if (allowed.contains(ke.getText) && value.length < 16) {
        value = value + ke.getText
      }
    }
  }
  
}




