package gui

import java.io.File
import java.io.FileInputStream
import scala.collection.mutable.Buffer
import game._
import scalafx.scene.SnapshotParameters
import scalafx.scene.canvas.Canvas
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.image.Image
import scalafx.scene.image.WritableImage
import scalafx.scene.paint.Color
import scalafx.scene.text.Font
import scalafx.scene.text.TextAlignment
import javax.imageio.ImageIO
import scalafx.embed.swing.SwingFXUtils
import scalafx.scene.image.PixelReader
import gui.Effects.TowerupEffect
import gui.Effects.ExplosionEffect
import scalafx.scene.shape.ArcType


/** Render contains all the functions (except animations) necessary for rendering the game. */
object Render {
  
  /** Render game renders the given game fully to the selected canvas, with a given selected tower. */
  def renderGame(game: Game, canvas: Canvas, selectedTower: Option[Tower]): Unit = {
    if (this.bg == null) throw new RenderingException(
      "Background is null. Prerender must be performed before rendering")

    val gfx = canvas.graphicsContext2D

    // Recalculate W and H
    this.W = Main.stage.scene.value.getWidth
    this.H = Main.stage.scene.value.getHeight
    
    // Draw background
    gfx.drawImage(this.bg, 0, 0, W, H)
    
    // Draw all game elements
    gfx.translate(0.5 * this.gridW, 0.5 * this.gridH)
    this.renderEnemies(gfx, game.enemies)
    this.renderProjectiles(gfx, game.projectiles)
    this.renderEffects(gfx)
    gfx.translate(-0.5 * this.gridW, -0.5 * this.gridH)
    this.renderTowers(gfx, game.towers)
    
    // Finally redraw bottom bar and its info
    gfx.drawImage(this.bg, 0, 840, 1920, 240, 0, this.mainH, W, this.sideH)
    
    // Draw player HP and money
    this.setFontSize(gfx, 30)
    gfx.fillText(s"${game.player.health.toInt} HP", 192 * resizeW, 1024 * resizeH)
    gfx.fillText(s"${"$"} ${game.player.money}",    192 * resizeW,  941 * resizeH)
    
    // Draw tower prices
    this.setFontSize(gfx, 20)
    gfx.textAlign = TextAlignment.Center  // Shop prices
    gfx.fillText("$ " + TowerInfo.priceCannon.toString,  760 * resizeW, 1044 * resizeH)
    gfx.fillText("$ " + TowerInfo.priceBoomer.toString,  960 * resizeW, 1044 * resizeH)
    gfx.fillText("$ " + TowerInfo.priceHoming.toString, 1160 * resizeW, 1044 * resizeH)
    
    // Draw wave number
    gfx.fill = Color(1.0, 1.0, 1.0, 1.0)  // Draw game info
    gfx.textAlign = TextAlignment.Center
    this.setFontSize(gfx, 40)
    gfx.fillText(s"${game.wave.number}", 1789 * resizeW, 950 * resizeH)
    
    // Show controls if necessary
    if (this.showControls) this.renderControls(gfx)
    
    // Show FPS if necessary
    if (this.showFPS) this.fps(Time.elapsedTime, canvas)
  }
  
  /** Function to set the font in the given graphics to the given size of gamegirl.ttf. */
  def setFontSize(gfx: GraphicsContext, size: Double) =
    gfx.setFont(Font.loadFont("file:assets/font/gamegirl.ttf", (size * (resizeW min resizeH)).toInt))

  /** Converts any grid position to a coordinate position on the canvas. */
  def canvasCoords(x: Double, y: Double) = (x * this.gridW, y * this.gridH)
  
  /** Converts a radius to elliptical radii. */
  def radius(r: Double) = (r * this.gridW, r * this.gridH)
  
  
  /** Renders the active tower in the towershop that is being purchased */
  def renderActiveTower(canvas: Canvas, game: Game, gridX: Double, gridY: Double) = {

    val gfx = canvas.graphicsContext2D
    
    val (x, y)   = this.canvasCoords(gridX, gridY)
    val (cx, cy) = this.canvasCoords(gridX + 0.5, gridY + 0.5)
    
    if (game.shop.active) {
      
      val tower = game.shop.activeTower.get
      
      tower.typeid match {
        case "c1" => Animate("cannondog1", x, y, gfx)
        case "b1" => Animate("koala1",     x, y, gfx)
        case "h1" => Animate("panda1",     x, y, gfx)
        case _    => Animate("cannondog1", x, y, gfx)
      }
      
      val (rx, ry) = radius(tower.radius)
      gfx.fill   = Color(1.0, 1.0, 1.0, 0.07)
      gfx.stroke = Color(1.0, 1.0, 1.0, 0.70)
      gfx.lineWidth = 10
      gfx.fillOval(  cx - rx, cy - ry, 2 * rx, 2 * ry)
      gfx.strokeOval(cx - rx, cy - ry, 2 * rx, 2 * ry)
    }
  }
  
  
  
  /** Renders a selectable tower when mouse is hovering over it. */
  def renderSelectableTower(canvas: Canvas, game: Game, tower: Tower): Unit = {
    
    val gfx = canvas.graphicsContext2D
    val (x, y) = this.canvasCoords(tower.pos.x + 0.5, tower.pos.y + 0.5)
    val (rx, ry) = radius(0.7)
    gfx.fill = Color(1.0, 1.0, 1.0, 0.2)
    gfx.fillOval(x - rx, y - ry, 2 * rx, 2 * ry)
    
  }
  
  
  
  /** Renders the selected tower. */ 
  def renderSelectedTower(canvas: Canvas, tower: Tower): Unit = {
    
    val gfx = canvas.graphicsContext2D
    val (x, y) = this.canvasCoords(tower.pos.x, tower.pos.y)
    val (rx, ry) = radius(tower.radius)
    
    gfx.translate(this.gridW / 2, this.gridH / 2)
    gfx.fill   = Color(1.0, 1.0, 1.0, 0.07)
    gfx.stroke = Color(1.0, 1.0, 1.0, 0.70)
    gfx.lineWidth = 10
    gfx.fillOval(  x - rx, y - ry, 2 * rx, 2 * ry)
    gfx.strokeOval(x - rx, y - ry, 2 * rx, 2 * ry)
    if (tower.upgrade.isDefined) {
      this.setFontSize(gfx, 20)
      gfx.fill = Color(1.0, 1.0, 1.0, 1.0)
      gfx.textAlign = TextAlignment.Center
      gfx.fillText("$" + tower.upgrade.get.price.toString(), x, y - (60 * resizeH))
    }
    
    gfx.translate(-this.gridW / 2, -this.gridH / 2)
  }
  
  
  
  /** Renders towers in the shop. */
  def renderShopTowers(canvas: Canvas): Unit = {
    
    val gfx = canvas.graphicsContext2D
    Animate("cannondog1",  730 * resizeW, 916 * resizeH, gfx)
    Animate("koala1",      930 * resizeW, 916 * resizeH, gfx)
    Animate("panda1",     1130 * resizeW, 916 * resizeH, gfx)    
  }
  
  
  
  /** Renders the game over screen. */
  def renderGameover(canvas: Canvas) = {
    val gfx = canvas.graphicsContext2D
    gfx.clearRect(0, 0, Int.MaxValue, Int.MaxValue)
    Animate("gameover", 0, 0, gfx)
  }

  
  
  /** Renders the towers. */
  private def renderTowers(gfx: GraphicsContext, towers: Buffer[Tower]) = {
    for (t <- towers.sortBy(_.pos.y)) {
      val (x, y) = this.canvasCoords(t.pos.x, t.pos.y)
      t.typeid match {
        case "c1" => Animate("cannondog1", x, y, gfx)
        case "c2" => Animate("cannondog2", x, y, gfx)
        case "c3" => Animate("cannondog3", x, y, gfx)
        case "c4" => Animate("cannondog2", x, y, gfx)
        case "b1" => Animate("koala1", x, y, gfx)
        case "b2" => Animate("koala2", x, y, gfx)
        case "b3" => Animate("koala2", x, y, gfx)
        case "b4" => Animate("koala2", x, y, gfx)
        case "h1" => Animate("panda1", x, y, gfx)
        case "h2" => Animate("panda1", x, y, gfx)
        case "h3" => Animate("panda1", x, y, gfx)
        case "h4" => Animate("panda1", x, y, gfx)
        case _    => Animate("cannondog1", x, y, gfx)
      }
    }
  }
  
  

  /** Renders the projectiles. */
  private def renderProjectiles(gfx: GraphicsContext, projectiles: Buffer[Projectile]) = {

    gfx.fill = Color(1.0, 1.0, 1.0, 1.0)
    
    for (p <- projectiles) {
      val (x, y) = this.canvasCoords(p.pos.x, p.pos.y)
      
      if (p.isInstanceOf[Missile]) {
        val angle = p.asInstanceOf[Missile].dir() 
        gfx.translate(x, y)
        gfx.rotate(angle)
        gfx.drawImage(this.homingProjImage, -8, -8, 16, 16)
        gfx.rotate(-angle)
        gfx.translate(-x, -y)
      }
      
      else if (p.isInstanceOf[Bullet]) {
        gfx.drawImage(this.bulletProjImage, x - 8, y - 8, 16, 16)
      }
      
      else if (p.isInstanceOf[Boomerang]) {
        val angle = p.asInstanceOf[Boomerang].angle
        gfx.translate(x, y)
        gfx.rotate(angle)
        gfx.drawImage(this.boomerangProjImage, -12, -12, 24, 24)
        gfx.rotate(-angle)
        gfx.translate(-x, -y)
      }
    }
  }


  
  /** Renders the enemies as coloured circles of the correct size with HP bars. */
  private def renderEnemies(gfx: GraphicsContext, enemies: Buffer[Enemy]) = {
    
    for (e <- enemies) {
      
      val (x, y) = this.canvasCoords(e.pos.x, e.pos.y)

      val (sx, sy) = radius(e.size * 1.2)  // HP radius
      val (rx, ry) = radius(e.size)
      
      val hp = (e.health / e.maxhp) max 0
     
      gfx.stroke = Color(0, 0, 0, 0.8)
      gfx.lineWidth = 5
      gfx.strokeOval(x - rx, y - ry, 2 * rx, 2 * ry)
      
      gfx.stroke = Color(1, 1, 1, 0.8)
      gfx.lineWidth = 3
      gfx.strokeArc(x - sx, y - sy, 2 * sx, 2 * sy, -90, hp * 360, ArcType.OPEN)
      
      val c = 1 / 255.0

      gfx.fill = e.typeid match { // Fill based on the color and remaining health
        case "n1" => Color( 46 * c, 117 * c, 219 * c, 1.0)
        case "n2" => Color(  0 * c, 160 * c,  23 * c, 1.0)
        case "n3" => Color(229 * c, 174 * c,  21 * c, 1.0)
        case "n4" => Color(229 * c,  75 * c,   0 * c, 1.0)
        case "n5" => Color(188 * c,   4 * c,   4 * c, 1.0)
        case _ => Color(0.0, 0.0, 0.0, 1.0)
      }

      gfx.fillOval(x - rx, y - ry, 2 * rx, 2 * ry)
    }
  }
  
  
  
  /** Renders the ongoing special effects in the effects object. */
  def renderEffects(gfx: GraphicsContext) = {  
    
    this.setFontSize(gfx, 20)
    gfx.fill = Color.White
    
    for (eff <- Effects.effects) {
      
      if (eff.isInstanceOf[gui.Effects.MoneyEffect]) {
        val m = eff.asInstanceOf[gui.Effects.MoneyEffect]
        val (x, y) = this.canvasCoords(m.x, m.y)
        gfx.fillText(m.text, x, y)
      }
      
      else if (eff.isInstanceOf[gui.Effects.TowerupEffect]) {
        val t = eff.asInstanceOf[gui.Effects.TowerupEffect]
        val (x, y) = this.canvasCoords(t.x, t.y)
        Animate.animate("towerup", x, y, gfx, 5, t.age)
      }
      
      else if (eff.isInstanceOf[gui.Effects.ExplosionEffect]) {
        val e = eff.asInstanceOf[gui.Effects.ExplosionEffect]
        val (x, y) = this.canvasCoords(e.x, e.y)
        Animate.animate("explosion", x - 30, y - 30, gfx, 3, e.age)
      }
    }
  }
  
  /** Renders the controls onscreen. */
  def renderControls(gfx: GraphicsContext) {
    this.controlsTimer += 1
    val (fadeStart, fadeStop) = (300, 480)
    if (controlsTimer > fadeStop) {
      controlsTimer = 0
      this.showControls = false
    }
    gfx.setGlobalAlpha {
      if (controlsTimer < fadeStart) 1.0
      else 1.0 - ((controlsTimer - fadeStart).toDouble / (fadeStop - fadeStart))
    }
    this.setFontSize(gfx, 15)
    val (c, n) = (resizeW min resizeH, controls.length)
    val line = 20 * c
    var (tx, ty, tw, th) = (20 * c, 780 * c, 600 * c, n * line)
    gfx.fill = Color(0.0, 0.0, 0.0, 0.6)
    gfx.fillRect(tx - 4 * c, ty - th + 4 * c, tw, th) 
    gfx.fill = Color(1.0, 1.0, 1.0, 1.0)
    gfx.textAlign = TextAlignment.Left
    controls.reverse.foreach(instr => {
      gfx.fillText(instr, tx, ty)
      ty -= line
    })
    gfx.setGlobalAlpha(1.0)
  }
  
  /** The controls. */
  private val controls = Array[String](
    "[Spacebar]: Fast forward",
    "[Spacebar]: Next Wave",
    "[Enter]:    Upgrade selected tower",
    "[1]:        Buy cannon dog",
    "[2]:        Buy boomerang koala",
    "[3]:        Buy tank panda",
    "[F11]:      Fullscreen"
  )
  
  
  /** The current state of control visibility. */
  private var showControls = false
  
  /** The timer for controls: controls are only visible for a certain amount of time. */
  private var controlsTimer = 0
  
  /** Function to toggle whether the controls are showing or not. */
  def toggleControls() = this.showControls = !this.showControls

  /** List of all previous frame rates. */
  var previousFrames = Array[Int]()

  /** Renders the FPS to the corner of the screen as an average of the last 60 frames. */
  def fps(elapsedTime: Double, canvas: Canvas) = {
    val gfx = canvas.graphicsContext2D
    val fps = (1.0 / elapsedTime).toInt
    this.setFontSize(gfx, 40)
    gfx.textAlign = TextAlignment.Left
    gfx.fill = Color(1.0, 1.0, 1.0, 1.0)
    previousFrames = fps +: previousFrames
    if (previousFrames.size > 60)
      previousFrames = previousFrames.tail
    val avg = previousFrames.sum / previousFrames.size
    gfx.fillText(avg.toString, 20, 50)
  }
  
  /** The current state of FPS visibility. */
  private var showFPS = false
  
  /** Function to toggle FPS visibility. */
  def toggleFPS() = this.showFPS = !this.showFPS
  
  /** The background image constructed in the prerender. */
  private var bg: Image = null
  
  /** Preloaded images for all projectiles. */
  private var bulletProjImage:    Image = ImageLoader("bulletproj")
  private var homingProjImage:    Image = ImageLoader("homingproj")
  private var boomerangProjImage: Image = ImageLoader("boomerangproj")

  /** The width W and height H of the current scene. Updated each frame. */
  var W: Double = 1920
  var H: Double = 1080
  
  /** The height of the main game area. */
  def mainH = 840 * (H / 1080)
  
  /** The heighto f the side area. */
  def sideH = 240 * (H / 1080)
  
  /** The grid width and height. */
  def gridW: Double = this.W / gCols
  def gridH: Double = this.mainH / gRows
  
  /** The amount of columns and rows in the game. Loaded in prerender. */
  var gCols: Int = 0
  var gRows: Int = 0  
  
  /** The resizing factors for width and height directions. */
  def resizeW = W / 1920
  def resizeH = H / 1080

  /** Function to load the game dimensions and render the background image. */
  def prerender(game: Game) = {

    // Loading a canvas to paint the background on.
    val canvas = new Canvas(1920, 1080)
    
    // Loading the canvas graphics to draw the background with.
    val cgfx = canvas.graphicsContext2D
    
    // Loading the columns and rows of the game.
    gCols = game.cols
    gRows = game.rows 
    
    // Loading a list of all the path coordinate pairs
    var paths: Array[(Int, Int)] = game.path.toArray().map(p => (p.pos.x.toInt, p.pos.y.toInt))
    
    // Creating a two-dimensional array of the locations of the paths as boolean trues, rest as falses
    val grid: Array[Array[Boolean]] = Array.ofDim[Boolean](game.cols + 2, game.rows + 2)
    
    // Constructing the grid based on the path coordinates
    for (i <- 0 until game.cols) {
      for (j <- 0 until game.rows) {
        grid(i + 1)(j + 1) = paths.contains((i, j))
      }
    }
    
    // Loading the spritesheet
    val spritesheet: Image = ImageLoader("ss_ground")
    
    // Draw each spot in the grid
    for (i <- 1 to game.cols) {
      for (j <- 1 to game.rows) {
        
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
        
        // Draws the correct part of the sprite to the correct coordinates
        cgfx.drawImage(spritesheet, sx * 60, sy * 60, 60, 60, (i - 1) * 60, (j - 1) * 60, 60, 60)
      }
    }
    // Drawing the sidebar
    cgfx.drawImage(ImageLoader("sidebar"), 0, 840)

    // Creating a new writable image
    val image = new WritableImage(1920, 1080)
    
    // Snapshotting the canvas and saving it as the background
    this.bg = canvas.snapshot(new SnapshotParameters(), image)    
  }
}

/** An exception class for rendering expections if needed. */
class RenderingException(msg: String) extends Exception(msg)





