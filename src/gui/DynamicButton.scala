package gui

import scalafx.scene.image.ImageView
import javafx.event.{ EventHandler => EH }
import javafx.scene.input.{ MouseEvent => ME }
import scalafx.scene.image.Image

abstract class Movable(private var _x: Double, private var _y: Double) {
  def moveTo(newX: Double, newY: Double) = {
    this._x = newX
    this._y = newY
  }
}

class DynamicHoverButton(val name: String)
  extends ImageView(Render.loadImage(name+"Normal")) {
  
  this.pickOnBounds = false
  
  val img_normal = Render.loadImage(name + "Normal")
  val img_click  = Render.loadImage(name + "Click")
  val img_hover  = Render.loadImage(name + "Hover")
  
  def stdW = img_normal.getWidth
  def stdH = img_normal.getHeight
  
  override def resize(W: Double, H: Double) = {
    this.setFitWidth( this.stdW * (W / 1920))
    this.setFitHeight(this.stdH * (H / 1080))
  }
  
  def onClick():   Unit = ()
  def onExit():    Unit = ()
  def onEnter():   Unit = ()
  def onRelease(): Unit = ()
  
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
  this.setOnMouseClicked( new EH[ME] { def handle(e: ME) = {
    image = img_click
    onClick()
  }})
}

class DynamicButton(val img: Image) extends ImageView(img) {
  
  this.pickOnBounds = false
  
  def stdW = img.getWidth
  def stdH = img.getHeight
  
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
  this.setOnMouseClicked( new EH[ME] { def handle(e: ME) = { onClick()   } } )
 
}


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



