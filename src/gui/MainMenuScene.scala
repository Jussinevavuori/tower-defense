package gui

import game._
import javafx.event.{ EventHandler => EH }
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.{ MouseEvent => ME }
import scalafx.Includes.eventClosureWrapperWithParam
import scalafx.Includes.jfxActionEvent2sfx
import scalafx.animation.AnimationTimer
import scalafx.application.JFXApp
import scalafx.event.ActionEvent
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.Menu
import scalafx.scene.control.MenuBar
import scalafx.scene.control.MenuItem
import scalafx.scene.control.SeparatorMenuItem
import scalafx.scene.layout.StackPane
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.Group
import scalafx.scene.image.ImageView
import scalafx.scene.ImageCursor

object MainMenuScene extends AnimationScene {
  
  val canvas      = new Canvas(1920, 1080)
  val titleCanvas = new Canvas(1920, 1080)
  
  
  canvas.graphicsContext2D.fill = Color(0, 0, 0, 1)
  canvas.graphicsContext2D.fillRect(0, 0, 1920, 1080)

  
  
  var animation = AnimationTimer { now =>

    Time.updateElapsedTime(now)
    if (Titlescreen.completed) {
      titleCanvas.visible = false
      this.stop()
    }
    Titlescreen.advance(this.titleCanvas, Time.elapsedTime)
    titleCanvas.opacity = Titlescreen.opacity
    var started = false
    if (Titlescreen.fading && !started) {
      started = true
      Music.startLoop()
    }
  }

    
  val stack = new StackPane()
  stack.children = List(canvas, titleCanvas)
  stack.setAlignment(Pos.TopLeft)
  root = stack

      
  // INPUT: KEY PRESSED
  this.onKeyPressed = new EH[KeyEvent] {
    def handle(ke: KeyEvent) = { ke.getCode() match {
      
        // F11 to toggle fullscreen
        case KeyCode.F11 => Main.stage.fullScreen = !Main.stage.fullScreen.value
        
        // All keys skip titlescreen
        //case t if (!Titlescreen.completed) => Actions.skipTitleScreen()
        
        case KeyCode.F1 => Main.changeStatus(0)
        case KeyCode.F2 => Main.changeStatus(1)
        case KeyCode.F3 => Main.changeStatus(2)
        
        case _ => 
    }}
  }
}