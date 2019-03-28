package gui

import game._
import javafx.event.{ EventHandler => EH }
import scalafx.event.{ ActionEvent => AE }
import javafx.scene.input.{ MouseEvent => ME }
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import scalafx.animation.AnimationTimer
import scalafx.geometry.Pos
import scalafx.geometry.HPos
import scalafx.scene.canvas.Canvas
import scalafx.scene.layout.StackPane
import scalafx.scene.image.ImageView
import scalafx.scene.layout.VBox
import scalafx.scene.Group
import scalafx.scene.shape.Rectangle
import scalafx.scene.Node

/** MainMenuScene is the main menu, from which the user starts and can continue to any
 *  other scene. MainMenuScene contains buttons to other scenes and the titlescreen.
 */
object MainMenuScene extends AnimationScene {
  
  /*
   * INITIALIZATION
   */
  
  /** The main canvas for the main menu. */
  val canvas = new Canvas(1920, 1080)
  
  /** The canvas for the titles. */
  val titleCanvas = new Canvas(1920, 1080)
  
  /** The main canvas graphics. */
  val gfx = canvas.graphicsContext2D
  
  /** The background image. */
  val bg = ImageLoader("mainMenuBg")

  /** Main animation loop. */
  var animation = AnimationTimer { now =>

    // Updating time
    Time.updateElapsedTime(now)
    
    // Menu animation
    val W = MainMenuScene.getWidth
    val H = MainMenuScene.getHeight
    gfx.drawImage(bg, 0, 0, W, H)
    resize(W, H)
    
    // Titlescreen animation
    if (Titlescreen.completed) {
      titleCanvas.visible = false
      Music.startLoop()
    } else {
      Titlescreen.advance(this.titleCanvas, Time.elapsedTime)
      titleCanvas.opacity = Titlescreen.opacity
      if (Titlescreen.fading) {
        Music.startLoop()
      }
    }
  }
  
  /** Loadup function. */
  override def loadUp() = {
    b_music.update()
  }
  
  /*
   * BUTTONS
   */
  
  /** The play button to start a new game. */
  val b_play = new DefaultButton("NEW GAME") {
    override def onClick() = {
      Actions.newGame()
      Main.changeStatus(ProgramStatus.InGame)
      Music.changeMusic("celebration")
    }
  }
  
  /** The continue button to continue previously saved game. */
  val b_cont = new DefaultButton("CONTINUE") {
    override def onClick() = {
      Actions.loadGame()
      Main.changeStatus(ProgramStatus.InGame)
      Music.changeMusic("celebration")
    }
  }
  
  /** The load button to load a custom level. */
  val b_load = new DefaultButton("LOAD LEVEL") {
    override def onClick() = {
      Main.changeStatus(ProgramStatus.LoadGame)
    }
  }
  
  /** The level editor button to open the level editor. */
  val b_lvle = new DefaultButton("LEVEL EDITOR") {
    override def onClick() = {
      Main.changeStatus(ProgramStatus.LevelEditor)
    }
  }
  
  /** The exit button to close the game. */
  val b_exit = new DefaultButton("EXIT") {
    override def onClick() = {
      sys.exit()
    }
  }
  
  /** Button to toggle music. */
  val b_music = Music.button

  /** Invisible rectangles in the corners to scale the buttons. */
  val scl1 = Rectangle(0, 0, 0, 0)
  val scl2 = Rectangle(1920, 1080, 0, 0)
  
  /** List of main buttons in the center column. */
  val mainButtons = new VBox(32) {
    alignment = Pos.Center
    children = List(b_play, b_cont, b_load, b_lvle, b_exit)
  }
  
  /** List of other buttons elsewhere. */
  val moreButtons = new Group() { children = List(b_music, scl1, scl2) }

  /** List of all buttons for resizing purposes. */
  val resizeList = Seq[ImageButton](b_play, b_cont, b_load, b_lvle, b_exit, b_music)

  /*
   * INPUT
   */  
      
  /** Key pressed. */
  this.onKeyPressed = new EH[KeyEvent] {
    def handle(ke: KeyEvent) = { ke.getCode() match {
      
      /** [F11] toggles fullscreen. */
      case KeyCode.F11 => Main.stage.fullScreen = !Main.stage.fullScreen.value
      
      /** [Any] skips titlescreen. */
      case t if (!Titlescreen.completed) => Actions.skipTitleScreen()
      
      case _ => 
    }}
  }
  
  /*
   * LAYOUR
   */

  /** Function to resize all elements. */
  def resize(W: Double, H: Double) = {
    mainButtons.spacing = (32 * H) / 1080
    resizeList.foreach(_.resize(W, H))
  }

  /** Creating the stack. */
  root = new StackPane() {
    children = List(canvas, mainButtons, moreButtons, titleCanvas)
    alignment = Pos.TopLeft
  }
}







