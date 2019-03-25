package gui

import scalafx.scene.image.ImageView
import javafx.event.{ EventHandler => EH }
import javafx.scene.input.{ MouseEvent => ME }
import scalafx.scene.image.Image


/* 
 * A collection of dynamic buttons to help in creating buttons and assigning repetitive
 * properties.
 */


/*
 * A dynamic hover button takes a single name as an input and from there loads
 * three images which should exist in the games assets to represent the normal
 * image, hover image and click image.
 * 
 * Dynamic hover buttons have functionality for automatically resizing themselves.
 * 
 * Dynamic hover buttons have easy access to assigning functions for a button.
 */
class DynamicHoverButton(val name: String)
  extends ImageView(Render.loadImage(name+"Normal")) {
  
  // Loaded images and properties
  val img_normal = Render.loadImage(name + "Normal")
  val img_click  = Render.loadImage(name + "Click")
  val img_hover  = Render.loadImage(name + "Hover")
  this.pickOnBounds = false
  val stdW = img_normal.getWidth
  val stdH = img_normal.getHeight
  
  // Automatic resizing given a width and a height
  override def resize(W: Double, H: Double) = {
    this.setFitWidth( this.stdW * (W / 1920))
    this.setFitHeight(this.stdH * (H / 1080))
  }
  
  // Overriding these functions will cause the button to have corresponding functionality
  def onClick():   Unit = ()
  def onExit():    Unit = ()
  def onEnter():   Unit = ()
  def onRelease(): Unit = ()
  
  // Setting the functionalities and image changing
  this.setOnMouseEntered( new EH[ME] { def handle(e: ME) = {
    image = img_hover
    onEnter()
  }})
  this.setOnMouseExited(  new EH[ME] { def handle(e: ME) = {
    image = img_normal
    onExit()
  }})
  this.setOnMouseReleased(new EH[ME] { def handle(e: ME) = {
    image = img_hover
    onRelease()
  }})
  this.setOnMousePressed( new EH[ME] { def handle(e: ME) = {
    image = img_click
    onClick()
  }})
}



/* 
 * A dynamic button is like a dynamic hover button, but only takes a single
 * image as input and does not change image based on mouse position.
 */
class DynamicButton(val img: Image) extends ImageView(img) {
  
  this.pickOnBounds = false
  val stdW = img.getWidth
  val stdH = img.getHeight
  
  override def resize(W: Double, H: Double) = {
    this.setFitWidth( this.stdW * (W / 1920))
    this.setFitHeight(this.stdH * (H / 1080))
  }
  
  def onClick():   Unit = ()
  def onExit():    Unit = ()
  def onEnter():   Unit = ()
  def onRelease(): Unit = ()
  
  this.setOnMouseEntered( new EH[ME] { def handle(e: ME) = { onEnter()   } } )
  this.setOnMouseExited(  new EH[ME] { def handle(e: ME) = { onExit()    } } )
  this.setOnMouseReleased(new EH[ME] { def handle(e: ME) = { onRelease() } } )
  this.setOnMousePressed( new EH[ME] { def handle(e: ME) = { onClick()   } } )
 
}


/*
 * The movable dynamic hover button and movable dynamic button assign extra
 * movability to buttons if they are to be moved around. They also upon
 * resizing automatically reassign a correct spot for themselves.
 */

class MovableDynamicHoverButton(name: String,
  private var _x: Double, private var _y: Double)
    extends DynamicHoverButton(name) {
  
  override def resize(W: Double, H: Double) = {
    this.setFitWidth( this.stdW * (W / 1920))
    this.setFitHeight(this.stdH * (H / 1080))
    this.x = _x * (W / 1920)
    this.y = _y * (H / 1080)
  }
  
  def moveTo(newX: Double, newY: Double) = {
    this._x = newX
    this._y = newY
  }
}

class MovableDynamicButton(img: Image,
  private var _x: Double, private var _y: Double)
    extends DynamicButton(img) {
  
  override def resize(W: Double, H: Double) = {
    this.setFitWidth( this.stdW * (W / 1920))
    this.setFitHeight(this.stdH * (H / 1080))
    this.x = _x * (W / 1920)
    this.y = _y * (H / 1080)
  }
  
  def moveTo(newX: Double, newY: Double) = {
    this._x = newX
    this._y = newY
  }
}
