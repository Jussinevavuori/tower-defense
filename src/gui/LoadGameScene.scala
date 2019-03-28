package gui

import game._
import javafx.event.{ EventHandler => EH }
import scalafx.event.{ ActionEvent => AE }
import javafx.scene.input.{ MouseEvent => ME }
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

/** LoadGameScene is a menu for selecting and loading one of the custom made saved levels. */
object LoadGameScene extends AnimationScene {
  
  /*
   * INITIALIZATION
   */
  
  /** The main animation timer. */
  var animation = AnimationTimer { now =>
    Time.updateElapsedTime(now)
    resize()
  }
  
  /** Drawing a black background canvas. */
  val canvas: Canvas = new Canvas(1920, 1080)
  canvas.graphicsContext2D.fill = Color.Black
  canvas.graphicsContext2D.fillRect(0, 0, 1920, 1080)
  
  /** The vertical alignment box for all the levels buttons. */
  var levels = new VBox(32) {
    alignment = Pos.Center
  }
  
  /** A list for all the buttons to update them all. */
  var levelButtons = List[Node]() 
  
  /** The loadup function to create the level buttons each time. */
  override def loadUp() = {
    
    // Update music button
    b_music.update()
    
    // The amount of custom levels
    val len = LevelSaver.loadLevelList().length
    
    // If there are custom levels
    if (len != 0) {
      
      // Clear the list
      levelButtons = List[Node]()
      
      // Create each level button seperately
      for (i <- 0 until len) {
        levelButtons = levelButtons :+ {new DefaultButton(s"Load level ${i+1}") {
          override def onClick() = {
            Main.loadGame(GameLoader.loadCustomGame(i))
            Main.changeStatus(ProgramStatus.InGame)
            Music.changeMusic("celebration")
          }
        }}
      }
    } 
    
    // Else create blank, non-interactive button
    else {
      levelButtons = List[Node](new DefaultButton("No custom levels", false))
    }
    
    // Set buttons to vertical alignment
    levels.children = levelButtons
  }
  
  /*
   * BUTTONS
   */
  
  /** Two invisible rectangles for scaling purposes. */
  val scl1 = Rectangle(0, 0, 0, 0)      
  val scl2 = Rectangle(1920, 1080, 0, 0) 
  
  /** A button to toggle music. */
  val b_music = Music.button
  
  /** The group for the buttons. */
  val buttons = new Group() { children = List(b_music, scl1, scl2) }
  
  /*
   * MENU
   */

  /** Return to main menu option. */
  val mMenu   = new MenuItem("Main menu") {
    onAction = (e: AE) => {
      Music.changeMusic("warriors")
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

  /*
   * LAYOUT
   */
  
  /** Function to resize all elements. */
  def resize() = {
    val W = this.getWidth
    val H = this.getHeight
    b_music.resize(W, H)
    levelButtons.foreach(_.resize(W, H))
  }
  
  /** Creating the stack. */
  root = new StackPane() {
    children = List(canvas, levels, buttons, menuBar)
    alignment = Pos.TopLeft
  }
  
  /*
   * INPUT
   */
  
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
  
  /** Mouse moved. */
  this.onMouseMoved = new EH[ME] {
    def handle(me: ME) = {
      
      /** Toggle menubar visibility. */
      menuBar.visible = me.getSceneY < 32 // Show and hide menubar
    }
  }
  
}