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
import scalafx.scene.text.TextAlignment

object LevelEditorScene extends AnimationScene {
  
  val canvas: Canvas = new Canvas(1920, 1080)
  val gfx = canvas.graphicsContext2D

  var h = 180.0
  var animation = AnimationTimer { now => {
    gfx.fill = Color.hsb(h % 360, 0.2, 0.2, 1.0)
    gfx.fillRect(0, 0, 1920, 1080)
    Render.setFontSize(gfx, 20)
    gfx.textAlign = TextAlignment.Left
    gfx.fill = Color(1.0, 1.0, 1.0, 1.0)
    gfx.fillText("THERE IS NOTHING HERE YET // LEVEL EDITOR UNDER WORK", 100, 600)
    h += 0.5
  } }

    
  val stack = new StackPane()
  stack.children = List(canvas)
  root = stack
  
      
      
  // INPUT: KEY PRESSED
  this.onKeyPressed = new EH[KeyEvent] {
    def handle(ke: KeyEvent) = { ke.getCode() match {
      
        case KeyCode.F11 => Main.stage.fullScreen = !Main.stage.fullScreen.value
        
        case KeyCode.F1 => Main.changeStatus(0)
        case KeyCode.F2 => Main.changeStatus(1)
        case KeyCode.F3 => Main.changeStatus(2)
        
        case _ => 
    }}
  }
}