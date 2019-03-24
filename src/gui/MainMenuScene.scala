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

object MainMenuScene extends AnimationScene {
  
  
  /*
   * INITIALIZATION OF NECESSARY ELEMENTS AND VARIABLES
   */
  
  
  // Canvases
  val canvas      = new Canvas(1920, 1080)
  val titleCanvas = new Canvas(1920, 1080)
  
  
  /*
   * GRAPHICS
   */
  
  
  val gfx = canvas.graphicsContext2D
  val bg = Render.loadImage("mainMenuBg")
  
  
  /*
   * DYNAMIC GUI ELEMENTS
   */
  
  
  val buttons = new VBox(32)
  buttons.alignment = Pos.Center
  
  val b_play = new DynamicHoverButton("mainMenuButton") {
    override def onClick() = Main.changeStatus(ProgramStatus.InGame)
  }
  val b_exit = new DynamicHoverButton("mainMenuButton") {
    override def onClick() = sys.exit()
  }
  buttons.children = List(b_play, b_exit)

  
  /*
   * MAIN ANIMATION LOOP
   */
  
  
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
    } else {
      Titlescreen.advance(this.titleCanvas, Time.elapsedTime)
      titleCanvas.opacity = Titlescreen.opacity
      if (Titlescreen.fading) {
        Music.startLoop()
      }
    }
  }
  
  
  /*
   * RESIZING
   */
  
  
  def resize(W: Double, H: Double) = {
    buttons.spacing = (32 * H) / 1080
    b_play.resize(W, H)
    b_exit.resize(W, H)
  }

  
  /*
   * LAYOUT OF SCENE
   */
    
  
  val stack = new StackPane()
  stack.children = List(canvas, buttons, titleCanvas)
  stack.setAlignment(Pos.TopLeft)
  root = stack

  
  /*
   * INPUT
   */
  
      
  // INPUT: KEY PRESSED
  this.onKeyPressed = new EH[KeyEvent] {
    def handle(ke: KeyEvent) = { ke.getCode() match {
      
        // F11 to toggle fullscreen
        case KeyCode.F11 => Main.stage.fullScreen = !Main.stage.fullScreen.value
        
        // All keys skip titlescreen
        case t if (!Titlescreen.completed) => Actions.skipTitleScreen()
        
        case KeyCode.F1 => Main.changeStatus(0)
        case KeyCode.F2 => Main.changeStatus(1)
        case KeyCode.F3 => Main.changeStatus(2)
        
        case _ => 
    }}
  }
}