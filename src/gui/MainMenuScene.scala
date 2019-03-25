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

object MainMenuScene extends AnimationScene {
  
  
  /*
   * INITIALIZATION OF NECESSARY ELEMENTS AND VARIABLES
   */
  
  
  // Canvases and graphics
  val canvas      = new Canvas(1920, 1080)
  val titleCanvas = new Canvas(1920, 1080)
  val gfx = canvas.graphicsContext2D
  val bg = Render.loadImage("mainMenuBg")
  
  
  /*
   * DYNAMIC GUI ELEMENTS
   */
  
  
  val mainButtons = new VBox(32)
  val moreButtons = new Group()
  mainButtons.alignment = Pos.Center
  
  val b_play = new DynamicDefaultButton("NEW GAME") {  // New game
    override def onClick() = {
      Actions.newGame()
      Main.changeStatus(ProgramStatus.InGame)
      Music.changeMusic("celebration")
    }
  }
  val b_cont = new DynamicDefaultButton("CONTINUE") {  // Continue game
    override def onClick() = {
      Actions.loadGame()
      Main.changeStatus(ProgramStatus.InGame)
      Music.changeMusic("celebration")
    }
  }
  val b_load = new DynamicDefaultButton("LOAD LEVEL") {  // Load game
    override def onClick() = {
      Main.changeStatus(ProgramStatus.LoadGame)
    }
  }
  val b_lvle = new DynamicDefaultButton("LEVEL EDITOR") {  // Level editor
    override def onClick() = Main.changeStatus(ProgramStatus.LevelEditor)
  }
  val b_exit = new DynamicDefaultButton("EXIT") {  // Exit
    override def onClick() = sys.exit()
  }
  val b_music = new MovableDynamicButton(Render.loadImage("note_on"), 1856, 32) {  // Toggle music
    this.pickOnBounds = true
    var muted = false
    val onImg = Render.loadImage("note_on")
    val offImg = Render.loadImage("note_off")
    override def onClick() = {
      muted = !muted
      this.image = { if (muted) offImg else onImg }
      Music.mute()
    }
  }
  
  // For scaling purposes
  val scl1 = Rectangle(0, 0, 0, 0)      
  val scl2 = Rectangle(1920, 1080, 0, 0) 
  
  mainButtons.children = List(b_play, b_cont, b_load, b_lvle, b_exit)
  moreButtons.children = List(b_music, scl1, scl2)

  
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
      Music.startLoop()
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
    mainButtons.spacing = (32 * H) / 1080
    b_play.resize(W, H)
    b_exit.resize(W, H)
    b_cont.resize(W, H)
    b_load.resize(W, H)
    b_lvle.resize(W, H)
    b_music.resize(W, H)
  }

  
  /*
   * LAYOUT OF SCENE
   */
    
  
  val stack = new StackPane()
  stack.children = List(canvas, mainButtons, moreButtons, titleCanvas)
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