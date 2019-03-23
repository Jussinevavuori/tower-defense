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
  val buttonNormal = Render.loadImage("mainMenuButtonNormal")
  val buttonClick  = Render.loadImage("mainMenuButtonClick")
  val buttonHover  = Render.loadImage("mainMenuButtonHover")
  
  
  /*
   * DYNAMIC GUI ELEMENTS
   */
  
  
  val buttons = new VBox(32)
  buttons.alignment = Pos.Center
  
  val b_play = new ImageView(buttonNormal)
  val b_exit = new ImageView(buttonNormal)
  
  buttons.children = List(b_play, b_exit)

  val b_playStdW = b_play.image.value.getWidth; val b_playStdH = b_play.image.value.getHeight
  val b_exitStdW = b_exit.image.value.getWidth; val b_exitStdH = b_exit.image.value.getHeight
  
  b_play.setOnMouseEntered( new EH[ME] { def handle(e: ME) = b_play.image = buttonHover  })
  b_exit.setOnMouseEntered( new EH[ME] { def handle(e: ME) = b_exit.image = buttonHover  })
  b_play.setOnMouseExited(  new EH[ME] { def handle(e: ME) = b_play.image = buttonNormal })
  b_exit.setOnMouseExited(  new EH[ME] { def handle(e: ME) = b_exit.image = buttonNormal })
  b_play.setOnMouseReleased(new EH[ME] { def handle(e: ME) = b_play.image = buttonHover  })
  b_exit.setOnMouseReleased(new EH[ME] { def handle(e: ME) = b_exit.image = buttonHover  })

  b_play.setOnMouseClicked( new EH[ME] { def handle(e: ME) = {
    b_play.image = buttonClick
    Main.changeStatus(ProgramStatus.InGame)
  }})
  b_exit.setOnMouseClicked( new EH[ME] { def handle(e: ME) = {
    b_exit.image = buttonClick
    sys.exit()
  }})


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
    val (rw, rh) = (W / 1920, H / 1080)
    b_play.setFitWidth(b_playStdW * rw); b_play.setFitHeight(b_playStdH * rh)
    b_exit.setFitWidth(b_exitStdW * rw); b_exit.setFitHeight(b_exitStdH * rh)
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