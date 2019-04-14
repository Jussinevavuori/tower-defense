package gui

import scalafx.scene.canvas.Canvas
import scalafx.scene.image.Image
import scalafx.scene.text.TextAlignment
import scalafx.scene.text.Font
import scalafx.scene.paint.Color
import javafx.event.{ EventHandler => EH }
import javafx.scene.input.{ MouseEvent => ME }
import scalafx.event.{ ActionEvent => AE }
import scalafx.delegate._
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty


/** Image buttons implement easy button funcionalities and dynamic overlays for
 *  a chosen image to be converted into a button.
 */
class ImageButton(var image: Image) extends Canvas(image.getWidth, image.getHeight) {
  
  /** The graphics of the canvas. */
  val gfx = this.graphicsContext2D
  
  /** The current white overlay opacity (0.0 - 1.0). */
  var overlay = 0.0
  
  /** By default all buttons are interactive. This value can be overridden. */
  var interactive: Boolean = true
  
  /** Function to automatically resize the button. */
  override def resize(W: Double, H: Double) = {
    this.width  = this.image.getWidth  * (W / 1920)
    this.height = this.image.getHeight * (H / 1080)
    this.render()
  }
  
  /** Function to render the button onto the canvas */
  def render() = {
    val W = this.getWidth
    val H = this.getHeight
    this.gfx.clearRect(0, 0, Int.MaxValue, Int.MaxValue)
    this.gfx.drawImage(this.image, 0, 0, W, H)
    this.gfx.fill = Color(1.0, 1.0, 1.0, overlay)
    this.gfx.fillRect(0, 0, W, H)
  }
  
  /** Function that can be overridden for functionality upon clicking the button. */
  def onClick():   Unit = ()
  /** Function that can be overridden for functionality upon exiting the button. */
  def onExit():    Unit = ()
  /** Function that can be overridden for functionality upon entering the button. */
  def onEnter():   Unit = ()
  /** Function that can be overridden for functionality upon releasing the button. */
  def onRelease(): Unit = ()
  
  /** Setting the dynamic overlay changes and functionalities. */
  this.setOnMouseEntered( new EH[ME] { def handle(e: ME) = { if (interactive) {
    overlay = 0.10
    onEnter()
  }}})
  this.setOnMouseExited(  new EH[ME] { def handle(e: ME) = { if (interactive) {
    overlay = 0.00
    onExit()
  }}})
  this.setOnMouseReleased(new EH[ME] { def handle(e: ME) = { if (interactive) {
    overlay = 0.10
    onRelease()
  }}})
  this.setOnMousePressed( new EH[ME] { def handle(e: ME) = { if (interactive) {
    overlay = 0.25
    onClick()
  }}})
}


/** An image button that has coordinates and can automatically resize them or be moved. */
class MovableImageButton(img: Image, _x: Double, _y: Double) extends ImageButton(img) {
  
  /** The static x coordinate. */
  var x = _x
  /** The static y coordinate. */
  var y = _y
  
  /** Function to automatically resize the button. */
  override def resize(W: Double, H: Double) = {
    this.width  = this.image.getWidth  * (W / 1920)
    this.height = this.image.getHeight * (H / 1080)
    this.layoutX = this.x * (W / 1920)
    this.layoutY = this.y * (H / 1080)
    this.render()
  }
  
  /** Changes the static coordinates, moving the image. */
  def moveTo(newX: Double, newY: Double) = {
    this.x = newX
    this.y = newY
  }
}



/** Dynamic default buttons use the default button image, overlaying text and implementing
 *  easy button functionality.
 */
class DefaultButton(val name: String, ia: Boolean = true) extends ImageButton(ImageLoader("emptyButton")) {
  
  /** The current text size. */
  var txtSize = 40
  
  /** Override the interactivity. */
  interactive = ia
  
  /** Setting the graphics font and text alignment. */
  this.gfx.font = Font.loadFont("file:assets/font/gamegirl.ttf", txtSize)
  this.gfx.textAlign = TextAlignment.Center
  
  /** The default width and height of the button. */
  var buttonWidth = 640
  var buttonHeight = 64
  
  /** Function to automatically resize the button. */
  override def resize(W: Double, H: Double) = {
    this.width = buttonWidth * (W / 1920)
    this.height = buttonHeight * (H / 1080)
    this.gfx.font = Font.loadFont("file:assets/font/gamegirl.ttf", txtSize * (H / 1080))
    this.render()
  }
  
  /** Function to render the button onto the canvas */
  override def render() = {
    val W = this.getWidth
    val H = this.getHeight
    this.gfx.clearRect(0, 0, Int.MaxValue, Int.MaxValue)
    this.gfx.drawImage(this.image, 0, 0, W, H)
    this.gfx.fill = Color(1.0, 1.0, 1.0, overlay)
    this.gfx.fillRect(0, 0, W, H)
    this.gfx.fill = Color(1.0, 1.0, 1.0, 1.0)
    this.gfx.fillText(this.name, W / 2, 0.75 * H, W)
  }
}


/** A default button that has coordinates and can automatically resize them or be moved. */
class MovableDefaultButton(name: String, _x: Double, _y: Double, ia: Boolean = true) extends DefaultButton(name, ia) {
    
  /** The static x coordinate. */
  var x = _x
  /** The static y coordinate. */
  var y = _y
  
  /** Function to automatically resize the button. */
  override def resize(W: Double, H: Double) = {
    this.width  = this.image.getWidth  * (W / 1920)
    this.height = this.image.getHeight * (H / 1080)
    this.width = buttonWidth * (W / 1920)
    this.height = buttonHeight * (H / 1080)
    this.gfx.font = Font.loadFont("file:assets/font/gamegirl.ttf", txtSize * (H / 1080))
    this.layoutX = this.x * (W / 1920)
    this.layoutY = this.y * (H / 1080)
    this.render()
  }
  
  /** Changes the static coordinates, moving the image. */
  def moveTo(newX: Double, newY: Double) = {
    this.x = newX
    this.y = newY
  }
}




