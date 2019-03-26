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


object LoadGameScene extends AnimationScene {
  
  
  var animation = AnimationTimer { now => resize() }
  
  // The main elements
  val canvas: Canvas = new Canvas(1920, 1080)
  val gfx = canvas.graphicsContext2D
  gfx.fill = Color.Black
  gfx.fillRect(0, 0, 1920, 1080)
  
  def resize() = {
    val W = this.getWidth
    val H = this.getHeight
    b_music.resize(W, H)
    levelButtons.foreach(_.resize(W, H))
  }

  
  /*
   * LOADUP FUNCTION
   * Loads the buttons based on the currently saved levels
   */
  
  var levels = new VBox(32)
  var levelButtons = List[Node]() 
  levels.alignment = Pos.Center
  override def loadUp() = {
    val len = LevelSaver.loadLevelList().length
    if (len != 0) {
      levelButtons = List[Node]()
      for (i <- 0 until len) {
        levelButtons = levelButtons :+ {new DefaultButton(s"Load level ${i+1}") {
          override def onClick() = {
            Main.loadGame(GameLoader.loadCustomGame(i))
            Main.changeStatus(ProgramStatus.InGame)
            Music.changeMusic("celebration")
          }
        }}
      }
    } else {
      levelButtons = List[Node](new DefaultButton(s"No custom levels"))
    }
    levels.children = levelButtons
  }
  
  
  /*
   * BUTTONS
   */
  
  
  val buttons = new Group()
  val b_music = new MovableDynamicButton(Render.loadImage("note_on"), 1856, 32) {  // Toggle music
    this.pickOnBounds = true
    var muted = false
    val onImg = Render.loadImage("note_on")
    val offImg = Render.loadImage("note_off")
    override def onClick() = {
      Music.mute()
      this.image = { if (Music.muted) offImg else onImg }
    }
  }
  val scl1 = Rectangle(0, 0, 0, 0)      
  val scl2 = Rectangle(1920, 1080, 0, 0) 
  buttons.children = List(b_music, scl1, scl2)
  
  
  /*
   * MENU
   */
  
  
  val menuBar = new MenuBar { visible = false }
  val menu    = new Menu("Menu")
  val mMenu   = new MenuItem("Main menu")
  val mExit   = new MenuItem("Exit")
  menuBar.menus.add(menu)
  menu.items.addAll(mMenu, new SeparatorMenuItem, mExit)
  mExit.onAction = (e: AE) => sys.exit(0)
  mMenu.onAction = (e: AE) => {
    Music.changeMusic("warriors")
    Main.changeStatus(ProgramStatus.MainMenu)
  }

  
  /*
   * LAYOUT
   */
  
  
  val stack = new StackPane()
  stack.children = List(canvas, levels, buttons, menuBar)
  stack.setAlignment(Pos.TopLeft)
  root = stack
  
  
  
  /*
   * INPUT
   */
  
  
  this.onKeyPressed = new EH[KeyEvent] {
    def handle(ke: KeyEvent) = { ke.getCode() match {
      
        case KeyCode.F11   => Main.stage.fullScreen = !Main.stage.fullScreen.value
        
        case KeyCode.ESCAPE => Main.changeStatus(ProgramStatus.MainMenu)
       
        case KeyCode.F1    => Main.changeStatus(0)
        case KeyCode.F2    => Main.changeStatus(1)
        case KeyCode.F3    => Main.changeStatus(2)
        case _ => 
    }}
  }
  this.onMouseMoved = new EH[ME] {
    def handle(me: ME) = {
      menuBar.visible = me.getSceneY < 32 // Show and hide menubar
    }
  }
  
}