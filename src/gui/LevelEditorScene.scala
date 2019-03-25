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
  
  // The main elements
  val canvas: Canvas = new Canvas(1920, 1080)
  val gfx = canvas.graphicsContext2D
  
  // The path elements
  var constructing = true
  var path: Path = null
  var grid = Array.fill[Boolean](Main.currentGame.cols + 2, Main.currentGame.rows + 2)(false)
  var (selX, selY) = (-1, -1)
  var (latestX, latestY) = (-1, -1)
  
  // Graphic elements
  var bg = this.renderMap()
  var selection = this.renderSelectionBox()
  
  
  // Function to create a path to given coordinates
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
        else if (distance < 2.1 && (sameRow || sameCol) 
                && isNotOverlapping && pathInBetween) {
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
  
  // Resets the path
  def resetPath() = {
    grid = grid.map(_.map(x => false))
    constructing = true
    path = null
    bg = renderMap()
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
  val menu    = new Menu("Menu"); menuBar.menus.add(menu)
  val mSave   = new MenuItem("Save");      menu.items.addAll(mSave, new SeparatorMenuItem)
  val mReset  = new MenuItem("Reset");     menu.items.addAll(mReset, new SeparatorMenuItem)
  val mMenu   = new MenuItem("Main menu"); menu.items.addAll(mMenu, new SeparatorMenuItem)
  val mExit   = new MenuItem("Exit");      menu.items.addAll(mExit)
  mSave.onAction = (e: AE) => if (!constructing) LevelSaver.saveCustomLevel(path)
  mReset.onAction = (e: AE) => resetPath()
  mExit.onAction = (e: AE) => sys.exit(0)
  mMenu.onAction = (e: AE) => {
    Music.changeMusic("warriors")
    Main.changeStatus(ProgramStatus.MainMenu)
  }  
  
  /*
   * ANIMATION TIMER (MAINLY RENDERING)
   */
  
  
  var animation = AnimationTimer { now => {
    gfx.globalAlpha = 1.0
    val W = LevelEditorScene.getWidth
    val H = LevelEditorScene.getHeight
    gfx.drawImage(this.bg, 0, 0, W, H)
    gfx.globalAlpha = 0.4
    val w = LevelEditorScene.getWidth / Main.currentGame.cols
    val h = (840 * (LevelEditorScene.getHeight / 1080)) / Main.currentGame.rows
    gfx.drawImage(selection, selX * w, selY * h, w, h)
    resize()
  }}

  
  def resize() = {
    b_music.resize(this.getWidth, this.getHeight)
  }
  
  /*
   * SCENE LAYOUT
   */
  
  
  val stack = new StackPane()
  stack.children = List(canvas, buttons, menuBar)
  stack.setAlignment(Pos.TopLeft)
  root = stack
  
      
  
  /*
   * INPUT
   */
  
  this.onKeyPressed = new EH[KeyEvent] {
    def handle(ke: KeyEvent) = { ke.getCode() match {
      
        case KeyCode.F11   => Main.stage.fullScreen = !Main.stage.fullScreen.value
        
        case KeyCode.ESCAPE => Main.changeStatus(ProgramStatus.MainMenu)
        
        case KeyCode.ENTER => if (!constructing) LevelSaver.saveCustomLevel(path)
        
        case KeyCode.DELETE => LevelSaver.resetCustomLevels()
        
        case KeyCode.R     => resetPath()
        
        case KeyCode.UP    => if (!createPath(latestX, latestY - 1)) createPath(latestX, latestY - 2)
        case KeyCode.DOWN  => if (!createPath(latestX, latestY + 1)) createPath(latestX, latestY + 2)
        case KeyCode.LEFT  => if (!createPath(latestX - 1, latestY)) createPath(latestX - 2, latestY)
        case KeyCode.RIGHT => if (!createPath(latestX + 1, latestY)) createPath(latestX + 2, latestY)
        
        case KeyCode.F1    => Main.changeStatus(0)
        case KeyCode.F2    => Main.changeStatus(1)
        case KeyCode.F3    => Main.changeStatus(2)
        case _ => 
    }}
  }
  this.onMouseMoved = new EH[ME] {
    def handle(me: ME) = {
      menuBar.visible = me.getSceneY < 32 // Show and hide menubar
      selX = me.getSceneX.toInt / (LevelEditorScene.getWidth.toInt  / Main.currentGame.cols)
      selY = me.getSceneY.toInt / ((840 * (LevelEditorScene.getHeight / 1080)).toInt / Main.currentGame.rows)
      if (selY >= Main.currentGame.rows) selY = -1
    }
  }
  this.onMousePressed = new EH[ME] {
    def handle(me: ME) = {
      if (selY > - 1) {
        createPath(selX, selY)
      }
    }
  }
  
  /*
   * RENDERING FUNCTIONS
   */
  
  
  
  def renderMap() = {
    
    val snapshotCanvas = new Canvas(1920, 1080)
    
    val sgfx = snapshotCanvas.graphicsContext2D
    val spritesheet: Image = Render.loadImage("ss_ground")
    for (i <- 1 to Main.currentGame.cols) {
      for (j <- 1 to Main.currentGame.rows) {
        val (dx, dy) = ( (i-1)*60, (j-1)*60 )
        val (sx, sy): (Int, Int) = {
          
          // This, left, right, up, down: true if path exists
          val t = grid(i)(j)
          val l = grid(i - 1)(j)
          val r = grid(i + 1)(j)
          val u = grid(i)(j - 1)
          val d = grid(i)(j + 1)
          val ld = grid(i - i)(j + 1)
          val rd = grid(i + 1)(j + 1)
          val lu = grid(i - 1)(j - 1)
          val ru = grid(i + 1)(j - 1)
          
          // Path
          if (t)          (1, 1)
          
          // Full surround
          else if (l & d & r & u) (6, 0)
          
          // Peninsulas
          else if (l & d & r) (6, 2)
          else if (l & d & u) (7, 0)
          else if (l & r & u) (6, 1)
          else if (d & r & u) (8, 0)
                
          // Inner corners
          else if (l & d) (3, 2)
          else if (r & d) (5, 2)
          else if (r & u) (5, 0)
          else if (l & u) (3, 0)
          
          // Double edges
          else if (l & r) (7, 1)
          else if (u & d) (7, 2)
          
          // Single edges
          else if (d) (1, 0)
          else if (r) (0, 1)
          else if (u) (1, 2)
          else if (l) (2, 1)
          
          // Outer corners
          else if (ld) (2, 0)
          else if (rd) (0, 0)
          else if (ru) (0, 2)
          else if (lu) (2, 2)
          
          // Grass
          else (0, 3)
        }
        sgfx.drawImage(spritesheet, sx * 60, sy * 60, 60, 60, dx, dy, 60, 60)
      }
    }
    
    if (this.path != null) {
      sgfx.lineWidth = 4.0
      sgfx.stroke = Color(1.0, 1.0, 1.0, 0.5)
      sgfx.beginPath()
      var p: Option[Path] = Some(this.path)
      do {
        sgfx.lineTo((p.get.pos.x + 0.5) * 60, (p.get.pos.y + 0.5) * 60)
        p = p.get.next
      } while(p.isDefined)
      sgfx.strokePath()
    }
    
    // Rendering the sidebar graphics
    sgfx.drawImage(Render.loadImage("llSidebar"), 0, 840)
    
    // Creating a snapshot and saving it
    val image = new WritableImage(1920, 1080)
    snapshotCanvas.snapshot(new SnapshotParameters(), image)
  }
  
  def renderSelectionBox() = {
    val c = new Canvas(60, 60)
    val g = c.graphicsContext2D
    val img = new WritableImage(60, 60)
    g.fill = Color(1.0, 1.0, 1.0, 0.5)
    g.fillRect(0, 0, 60, 60)
    c.snapshot(new SnapshotParameters(), img)
  }
}





