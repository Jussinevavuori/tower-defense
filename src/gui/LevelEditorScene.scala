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
import scalafx.scene.shape.Rectangle
import scalafx.scene.Group
import scalafx.scene.control.Menu
import scalafx.scene.control.MenuBar
import scalafx.scene.control.MenuItem
import scalafx.scene.control.SeparatorMenuItem
import scalafx.Includes.eventClosureWrapperWithParam
import scalafx.Includes.jfxActionEvent2sfx


object LevelEditorScene extends AnimationScene {
    
  /** Loadup function. */
  override def loadUp() = {
    b_music.update()
  }
  
  /** The level editor canvas. */
  val canvas: Canvas = new Canvas(1920, 1080)
  
  /** The level editor graphics. */
  val gfx = canvas.graphicsContext2D
  
  /** Set to false when path is done. */
  var constructing = true
  
  /** The current path. */
  var path: Path = null
  
  /** The current path boolean grid. */
  var grid = Array.fill[Boolean](Main.currentGame.cols + 2, Main.currentGame.rows + 2)(false)
  
  /** The currented selected grid spot's integer coordinates. */
  var (selX, selY) = (-1, -1)
  
  /** The latest created path's integer coordinates. */
  var (latestX, latestY) = (-1, -1)
  
  /** The background created with a function. */
  var bg = this.renderMap()
  
  /** The selection box graphics. */
  var selection = this.renderSelectionBox()
  
  /** Function to create a new path segment to the given integer grid coordinates. */
  def createPath(x: Int, y: Int): Boolean = {
    
    // If constructing and coordinates on gamefield
    if (x >= 0 && y >= 0 && y < Main.currentGame.rows && x < Main.currentGame.cols && constructing) {
      
      // For first path, construct starting point next to path outside of view
      if (path == null) {
        path = {
          if      (x == 0)                         { latestX = x - 1; latestY = y; new Path(x - 1, y) }
          else if (x == Main.currentGame.cols - 1) { latestX = x + 1; latestY = y; new Path(x + 1, y) }
          else if (y == 0)                         { latestX = x; latestY = y - 1; new Path(x, y - 1) }
          else if (y == Main.currentGame.rows - 1) { latestX = x; latestY = y + 1; new Path(x, y + 1) }
          else null
        }
      }
      
      // Construct other paths normally if path is next to previous path and not overlapping
      if (path != null) {
        
        // Conditions
        var p = path.last
        val distance = Vec(x, y).distance(Vec(latestX, latestY))
        val sameRow = x.toInt == p.pos.x.toInt
        val sameCol = y.toInt == p.pos.y.toInt
        val pathInBetween = grid(((x + latestX) / 2).toInt + 1)(((y + latestY) / 2).toInt + 1)
        val isNotOverlapping = !grid(x + 1)(y + 1)
        
        // Construct normal path
        if (distance < 1.1 && isNotOverlapping) {
          p.assignNext(new Path(x, y))
          grid(x + 1)(y + 1) = true
          latestX = x
          latestY = y
          bg = renderMap()
          return true
        }
        
        // Construct intersection
        else if (distance < 2.1 && (sameRow || sameCol) && isNotOverlapping && pathInBetween) {
          p.assignNext(new Path(x, y))
          grid(x + 1)(y + 1) = true
          latestX = x
          latestY = y
          bg = renderMap()
          return true
        }
        
        else false
      } else false
    }
    // Else finish construction
    else {
      constructing = false
      false
    }
  }
  
  /** Function to reset the path. */
  def resetPath() = {
    grid = grid.map(_.map(x => false))
    constructing = true
    path = null
    bg = renderMap()
  }

  /** Two invisible rectangles for scaling purposes. */
  val scl1 = Rectangle(0, 0, 0, 0)      
  val scl2 = Rectangle(1920, 1080, 0, 0) 
  
  /** A button to toggle the music. */
  val b_music = Music.button(1856, 890)
  
  /** A save button. */
  val b_save = new MovableDefaultButton("Save", 44, 982) {
    override def onClick() = {
      if (!constructing) {
        LevelSaver.saveCustomLevel(path, input.value)
        Audio.play("iosfx.wav")
      }
      else Audio.play("error.wav")
    }
  }
  
  /** A reset button. */
  val b_reset = new MovableDefaultButton("Reset", 772, 896) {
    override def onClick() = {
      resetPath()
      Audio.play("iosfx.wav")
    }
  }
  
  /** A return to main menu button. */
  val b_menu = new MovableDefaultButton("Main menu", 772, 982) {
    override def onClick() = {
      Main.changeStatus(ProgramStatus.MainMenu)
    }
  }
  
  /** A group for all the buttons. */
  val buttons = new Group() { children = List(b_music, b_save, b_reset, b_menu, scl1, scl2) }
  
  /** List of all elements to resize. */
  val resizeList = List(b_music, b_save, b_menu, b_reset)
  
  /** The input bar. */
  val input = new InputBar("Enter name", 44, 896)
  
  /** Main animation timer. */
  var animation = AnimationTimer { now => {
    
    // Update time
    Time.updateElapsedTime(now)
    
    // Dimensions
    val W = LevelEditorScene.getWidth
    val H = LevelEditorScene.getHeight
    val w = W / Main.currentGame.cols
    val h = (840 * (H / 1080)) / Main.currentGame.rows
    
    // Draw background
    gfx.globalAlpha = 1.0
    gfx.drawImage(this.bg, 0, 0, W, H)
    
    // Draw selection box
    gfx.globalAlpha = 0.4
    gfx.drawImage(selection, selX * w, selY * h, w, h)
    
    // Resize
    resize()
  }}

  /** Function to resize elements. */
  def resize() = {
    val W = this.getWidth
    val H = this.getHeight
    resizeList.foreach(_.resize(W, H))
    this.input.resize(W, H)
  }
  
  /** Building the stack. */
  root = new StackPane() {
    children = List(canvas, buttons, input)
    alignment = Pos.TopLeft
  }    

  /** On key pressed. */
  this.onKeyPressed = new EH[KeyEvent] {
    def handle(ke: KeyEvent) = { ke.getCode() match {
      
      /** F11 toggles fullscreen. */
      case KeyCode.F11   => Main.stage.fullScreen = !Main.stage.fullScreen.value
      
      /** Escape returns to main menu. */
      case KeyCode.ESCAPE => Main.changeStatus(ProgramStatus.MainMenu)
      
      /** Arrow keys control path. */
      case KeyCode.UP    => if (!createPath(latestX, latestY - 1)) createPath(latestX, latestY - 2)
      case KeyCode.DOWN  => if (!createPath(latestX, latestY + 1)) createPath(latestX, latestY + 2)
      case KeyCode.LEFT  => if (!createPath(latestX - 1, latestY)) createPath(latestX - 2, latestY)
      case KeyCode.RIGHT => if (!createPath(latestX + 1, latestY)) createPath(latestX + 2, latestY)
      
      case _ => 
    }}
  }
  
  /** On mouse moved. */
  this.onMouseMoved = new EH[ME] {
    def handle(me: ME) = {

      /** Update selection box coordinates. */
      selX = me.getSceneX.toInt / (LevelEditorScene.getWidth.toInt  / Main.currentGame.cols)
      selY = me.getSceneY.toInt / ((840 * (LevelEditorScene.getHeight / 1080)).toInt / Main.currentGame.rows)
      if (selY >= Main.currentGame.rows) selY = -1
    }
  }
  
  /** On mouse pressed. */
  this.canvas.onMousePressed = new EH[ME] {
    def handle(me: ME) = {
      
      /** Request focus. */
      LevelEditorScene.canvas.requestFocus()
      
      /** If valid, create a path at selection. */
      if (selY > - 1) createPath(selX, selY)
    }
  }
  
  /*
   * RENDERING FUNCTIONS
   */
  
  /** Renders the background as an image. */
  def renderMap() = {
    
    // Loading a new canvas for the background to be drawn on
    val snapshotCanvas = new Canvas(1920, 1080)
    
    // Loading the canvas graphics
    val sgfx = snapshotCanvas.graphicsContext2D
    
    // Loading the spritesheet
    val spritesheet: Image = ImageLoader("ss_groundGrass")
    
    // Looping through the boolean grid
    for (i <- 1 to Main.currentGame.cols) {
      for (j <- 1 to Main.currentGame.rows) {
        val (sx, sy): (Int, Int) = {
          
          // Record the truth values for this and the eight neighboring grid cells
          val t = grid(i)(j)
          val l = grid(i - 1)(j)
          val r = grid(i + 1)(j)
          val u = grid(i)(j - 1)
          val d = grid(i)(j + 1)
          val ld = grid(i - i)(j + 1)
          val rd = grid(i + 1)(j + 1)
          val lu = grid(i - 1)(j - 1)
          val ru = grid(i + 1)(j - 1)
          
          // Find the correct spot in the spritesheet based on the neighbors
          
          if      (t)             (1, 1)  //  |Path
          else if (l & d & r & u) (6, 0)  //  |Full surround
          else if (l & d & r)     (6, 2)  //  |Peninsulas
          else if (l & d & u)     (7, 0)  //  | |
          else if (l & r & u)     (6, 1)  //  | |
          else if (d & r & u)     (8, 0)  //  | |
          else if (l & d)         (3, 2)  //  |Inner corners
          else if (r & d)         (5, 2)  //  | |
          else if (r & u)         (5, 0)  //  | |
          else if (l & u)         (3, 0)  //  | |
          else if (l & r)         (7, 1)  //  |Double edges
          else if (u & d)         (7, 2)  //  | |
          else if (d)             (1, 0)  //  |Single edges
          else if (r)             (0, 1)  //  | |
          else if (u)             (1, 2)  //  | |
          else if (l)             (2, 1)  //  | |
          else if (ld)            (2, 0)  //  |Outer corners
          else if (rd)            (0, 0)  //  | |
          else if (ru)            (0, 2)  //  | |
          else if (lu)            (2, 2)  //  | |
          else                    (0, 3)  //  |Grass
        }
        
        // Draw the correct sprite to the correct location
        sgfx.drawImage(spritesheet, sx * 60, sy * 60, 60, 60, (i-1)*60, (j-1)*60, 60, 60)
      }
    }
        
    // Draw the sidebar
    sgfx.drawImage(ImageLoader("llSidebar"), 0, 840)
    
    // Creating a new image to write to
    val image = new WritableImage(1920, 1080)
    
    // Snapshotting the canvas and returning the image
    snapshotCanvas.snapshot(new SnapshotParameters(), image)
  }
  
  /** Function that renders a small, see-through, white selection box and returns it. */
  def renderSelectionBox() = {
    val c = new Canvas(60, 60)
    c.graphicsContext2D.fill = Color(1.0, 1.0, 1.0, 0.5)
    c.graphicsContext2D.fillRect(0, 0, 60, 60)
    c.snapshot(new SnapshotParameters(), new WritableImage(60, 60))
  }
}





