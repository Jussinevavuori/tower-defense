package gui

import game._
import javafx.event.{ EventHandler => EH }
import scalafx.event.{ ActionEvent => AE }
import javafx.scene.input.{ MouseEvent => ME }
import javafx.scene.input.{ ScrollEvent => SE }
import scalafx.Includes.eventClosureWrapperWithParam
import scalafx.Includes.jfxActionEvent2sfx
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import scalafx.animation.AnimationTimer
import scalafx.geometry.Pos
import scalafx.geometry.HPos
import scalafx.scene.canvas.Canvas
import scalafx.scene.layout.StackPane
import scalafx.scene.image.ImageView
import scalafx.scene.layout.VBox
import scalafx.scene.text.TextAlignment
import scalafx.scene.paint.Color
import scalafx.scene.image.Image
import scalafx.scene.SnapshotParameters
import scalafx.scene.image.WritableImage
import scalafx.scene.shape.StrokeLineCap
import scalafx.scene.Node
import scalafx.scene.shape.Rectangle
import scalafx.scene.Group
import scalafx.scene.control.SeparatorMenuItem
import scalafx.scene.control.Menu
import scalafx.scene.control.MenuBar
import scalafx.scene.control.MenuItem
import scalafx.scene.layout.HBox

/** LoadGameScene is a menu for selecting and loading one of the custom made saved levels. */
object LoadGameScene extends AnimationScene {
  
  /** The main animation timer. */
  var animation = AnimationTimer { now =>
    Time.updateElapsedTime(now)
    resize()
  }
  
  /** Drawing a black background canvas. */
  val canvas: Canvas = new Canvas(1920, 1080)
  canvas.graphicsContext2D.fill = Color.Black
  canvas.graphicsContext2D.fillRect(0, 0, 1920, 1080)
  
  /** Drawing an overlay canvas. */
  val overlay: Canvas = new Canvas(1920, 1080) { this.pickOnBounds = false }
  def resizeOverlay(W: Double, H: Double) = {
    overlay.graphicsContext2D.fill = Color(0.1, 0.1, 0.1, 1.0)
    overlay.graphicsContext2D.fillRect(0, 0, Int.MaxValue, Int.MaxValue)
    overlay.translateY = 944 * (H / 1080)
  }
  
  /** Menu y position minimum coordinate. */
  def menuYmax = 125.0
  
  /** Menu y position maximum coordinate. */
  def menuYmin = -1 * (lvlButtons.size - 9) * 80
  
  /** Menu y position. */
  var menuY = menuYmax

  /** Class for delete buttons. */
  class DelButton(var n: Int) extends ImageButton(ImageLoader("deleteSquare")) {
    override def onClick() = {
      lvlButtons = lvlButtons.takeWhile(_.n != this.n) ++ lvlButtons.dropWhile(_.n != this.n).drop(1)
      delButtons = delButtons.takeWhile(_.n != this.n) ++ delButtons.dropWhile(_.n != this.n).drop(1)
      if (lvlButtons.isEmpty) {
        lvlButtons = List(new LvlButton("No custom levels", -1) { override val interactive = false } )
        delButtons = List(new DelButton(-1) { override val interactive = false })
      }
      lvls.children = lvlButtons
      dels.children = delButtons
      LevelSaver.deleteCustomLevel(n)
    }
  }
  
  /** Class for load buttons. */
  class LvlButton(name: String, val n: Int) extends DefaultButton(name) {
    override def onClick() = {
      Main.loadGame(GameLoader.loadCustomGame(n))
      Main.changeStatus(ProgramStatus.InGame)
      Music.changeMusic("celebration")
    }
  }
    
  /** The vertical alignment box for all the levels buttons. */
  var lvls = new VBox(32) { alignment = Pos.TopCenter; this.pickOnBounds = false }
  
  /** The vertical alignment box for all the delete buttons. */
  var dels = new VBox(32) { alignment = Pos.TopCenter; this.pickOnBounds = false }
  
  /** A list for all the level buttons to update them all. */
  var lvlButtons = List[LvlButton]() 
  
  /** A list for all the delete buttons to update them all. */
  var delButtons = List[DelButton]()
  
  /** The loadup function to create the level buttons each time. */
  override def loadUp() = {
    
    // Update music button
    b_music.update()
    
    // The amount of custom levels
    val list = LevelSaver.loadLevelList()
    
    // If there are custom levels
    if (list.nonEmpty) {
      
      // Clear the list
      lvlButtons = List[LvlButton]()
      delButtons = List[DelButton]()
      
      // Create each level button seperately
      for ((name, num) <- list) {
        delButtons = delButtons :+ new DelButton(num)
        lvlButtons = lvlButtons :+ new LvlButton(name, num)
      }
    } 
    
    // Else create blank, non-interactive button
    else {
      lvlButtons = List(new LvlButton("No custom levels", -1) { override val interactive = false } )
		  delButtons = List(new DelButton(-1)                     { override val interactive = false } )
    }
    
    // Set buttons to vertical alignment
    lvls.children = lvlButtons
    dels.children = delButtons
  }
  
  /** Two invisible rectangles for scaling purposes. */
  val scl1 = Rectangle(0, 0, 0, 0)      
  val scl2 = Rectangle(1920, 1080, 0, 0) 
  
  /** A button to toggle music. */
  val b_music = Music.button()
  
  /** A button to return to main menu. */
  val b_mainmenu = new MovableDefaultButton("Main menu", 36, 980) {
    override def onClick() = {
      Main.changeStatus(ProgramStatus.MainMenu)
    }
  }
  
  /** The group for the buttons. */
  val buttons = new Group() { children = List(b_music, b_mainmenu, scl1, scl2) }
  
  /** Return to main menu option. */
  val mMenu   = new MenuItem("Main menu") {
    onAction = (e: AE) => {
      Main.changeStatus(ProgramStatus.MainMenu)
    }
  }
  
  /** Exit option. */
  val mExit   = new MenuItem("Exit") {
    onAction = (e: AE) => {
      sys.exit(0)
    }
  }
  
  /** The menu containing the options. */
  val menu = new Menu("Menu") { items = List(mMenu, sep, mExit) }
  
  /** The menubar containing the menu. */
  val menuBar = new MenuBar {
    visible = false
    menus = List(menu)
  }

  /** Function to resize all elements. */
  def resize() = {
    val W = this.getWidth
    val H = this.getHeight
    lvls.translateY = menuY
    dels.translateY = menuY
    lvls.translateX = -48 * (W / 1920)
    dels.translateX = 368 * (W / 1920)
    lvlButtons.foreach(_.resize(W, H))
    delButtons.foreach(_.resize(W, H))
    resizeOverlay(W, H)
    b_music.resize(W, H)
    b_mainmenu.resize(W, H)
  }
  
  /** Creating the stack. */
  root = new StackPane() {
    children = List(canvas, lvls, dels, overlay, buttons, menuBar)
    alignment = Pos.TopLeft
  }
  
  /** Key pressed. */
  this.onKeyPressed = new EH[KeyEvent] {
    def handle(ke: KeyEvent) = { ke.getCode() match {
      
        /** [F11] toggles fullscreen. */
        case KeyCode.F11   => Main.stage.fullScreen = !Main.stage.fullScreen.value
        
        /** Escape returns to main menu. */
        case KeyCode.ESCAPE => Main.changeStatus(ProgramStatus.MainMenu)

        case _ => 
    }}
  }
  
  /** Mouse scrolled. */
  this.onScroll = new EH[SE] {
    def handle(se: SE) = {
      
      /** Scroll level list. */
      menuY = ((menuY + se.getDeltaY) max menuYmin) min menuYmax
    }
  }
  
  /** Mouse moved. */
  this.onMouseMoved = new EH[ME] {
    def handle(me: ME) = {
      
      /** Toggle menubar visibility. */
      menuBar.visible = me.getSceneY < 32 // Show and hide menubar
    }
  }
}


