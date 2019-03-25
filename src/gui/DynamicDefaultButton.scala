package gui

import scalafx.scene.canvas.Canvas
import scalafx.scene.image.Image
import scalafx.scene.text.TextAlignment
import scalafx.scene.text.Font
import scalafx.scene.paint.Color
import javafx.event.{ EventHandler => EH }
import javafx.scene.input.{ MouseEvent => ME }
import scalafx.event.{ ActionEvent => AE }


/* A dynamic default button has always the same default button graphic
 * with a given text and functions like a button should.
 */

class DefaultButton(val name: String) extends Canvas(640, 64) {
  
  // Loaded images and properties
  val gfx = this.graphicsContext2D
  var txtSize = 40
  val img: Image = Render.loadImage("emptyButton")
  this.gfx.font = Font.loadFont("file:assets/font/gamegirl.ttf", txtSize)
  this.gfx.textAlign = TextAlignment.Center
    
  // Automatic resizing given a width and a height
  override def resize(W: Double, H: Double) = {
    this.width = 640 * (W / 1920)
    this.height = 64 * (H / 1080)
    this.gfx.font = Font.loadFont("file:assets/font/gamegirl.ttf", txtSize * (H / 1080))
    render
  }
  var overlay = 0.0
  
  def render() = {
    val W = this.getWidth
    val H = this.getHeight
    this.gfx.clearRect(0, 0, Int.MaxValue, Int.MaxValue)
    this.gfx.drawImage(this.img, 0, 0, W, H)
    this.gfx.fill = Color(1.0, 1.0, 1.0, overlay)
    this.gfx.fillRect(0, 0, W, H)
    this.gfx.fill = Color(1.0, 1.0, 1.0, 1.0)
    this.gfx.fillText(this.name, W / 2, 0.75 * H, W)
  }
  
  // Overriding these functions will cause the button to have corresponding functionality
  def onClick():   Unit = ()
  def onExit():    Unit = ()
  def onEnter():   Unit = ()
  def onRelease(): Unit = ()
  
  // Setting the functionalities and image changing
  this.setOnMouseEntered( new EH[ME] { def handle(e: ME) = {
    overlay = 0.10
    onEnter()
  }})
  this.setOnMouseExited(  new EH[ME] { def handle(e: ME) = {
    overlay = 0.00
    onExit()
  }})
  this.setOnMouseReleased(new EH[ME] { def handle(e: ME) = {
    overlay = 0.10
    onRelease()
  }})
  this.setOnMousePressed( new EH[ME] { def handle(e: ME) = {
    overlay = 0.25
    onClick()
  }})
}