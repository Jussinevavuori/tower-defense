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
import scalafx.scene.text.TextAlignment
import scalafx.scene.paint.Color
import scalafx.scene.image.Image
import scalafx.scene.SnapshotParameters
import scalafx.scene.image.WritableImage
import scalafx.scene.shape.StrokeLineCap
import scalafx.scene.Node


object LoadGameScene extends AnimationScene {
  
  
  var animation = AnimationTimer { now => () }
  
  // The main elements
  val canvas: Canvas = new Canvas(1920, 1080)
  val gfx = canvas.graphicsContext2D
  gfx.fill = Color.Black
  gfx.fillRect(0, 0, 1920, 1080)
  
  /* Each time when loading up loadGameScene
   * go through all available levels and load them as buttons
   */
  var levels = new VBox(32)
  levels.alignment = Pos.Center
  override def loadUp() = {
    levels.children.clear()
    val lvls = LevelSaver.loadLevelList()
    var i = 0
    for (lvl <- lvls) {
      i += 1
      levels.children.add({new DynamicDefaultButton(s"Load level $i") {
        override def onClick() = {
          ()
        }
      }}.asInstanceOf[Node])
    }
  }

  // The layout
  val stack = new StackPane()
  stack.children = List(canvas, levels)
  stack.setAlignment(Pos.TopLeft)
  root = stack
  
  // On key pressed
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
  
}