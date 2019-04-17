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
  
  
  /*
   * MAIN RENDERING
   */
  
  
  /** Render game renders the given game fully to the selected canvas, with a given selected tower. */
  def renderGame(game: Game, canvas: Canvas, selectedTower: Option[Tower]): Unit = {
    
    // Require that the background is not null: Prerendering must be done before
    this.renderRequire(this.bgs.nonEmpty, "Background is null")
    
    // The current wave's background
    val bg = this.bgs((game.wave.number / this.seasonfreq) % this.bgs.size)
    
    // The previous wave's background
    val prevbg = this.bgs((((game.wave.number - 1) max 0) / this.seasonfreq) % this.bgs.size)
    
    // The graphics of the canvas
    val gfx = canvas.graphicsContext2D

    // Recalculate W and H
    this.W = Main.stage.scene.value.getWidth
    this.H = Main.stage.scene.value.getHeight
    
    // Draw background
    gfx.drawImage(bg, 0, 0, W, H)
    
    // Fade previous background
    if (this.prevAlpha > 0.0) {
      gfx.setGlobalAlpha(this.prevAlpha)
      gfx.drawImage(prevbg, 0, 0, W, H)
      gfx.setGlobalAlpha(1.0)
    }
    
    // Draw all game elements
    gfx.translate(0.5 * this.gridW, 0.5 * this.gridH)
    this.renderProjectiles(gfx, game.projectiles)
    this.renderEnemies(gfx, game.enemies)
    this.renderEffects(gfx)
    gfx.translate(-0.5 * this.gridW, -0.5 * this.gridH)
    this.renderTowers(gfx, game.towers)
    
    // Finally redraw bottom bar and its info ontop of game elements
    gfx.drawImage(bg, 0, 840, 1920, 240, 0, this.mainH, W, this.sideH)
    
    // Draw player HP and money
    this.setFontSize(gfx, 30)
    gfx.fillText(s"${game.player.health} HP",    192 * resizeW, 1024 * resizeH)
    gfx.fillText(s"${"$"} ${game.player.money}", 192 * resizeW,  941 * resizeH)
    
    // Draw tower prices
    this.setFontSize(gfx, 20)
    gfx.textAlign = TextAlignment.Center  // Shop prices
    gfx.fillText(s"${"$"} ${TowerInfo.priceCannon.toString}",  760 * resizeW, 1044 * resizeH)
    gfx.fillText(s"${"$"} ${TowerInfo.priceBoomer.toString}",  960 * resizeW, 1044 * resizeH)
    gfx.fillText(s"${"$"} ${TowerInfo.priceHoming.toString}", 1160 * resizeW, 1044 * resizeH)
    
    // Draw wave number
    this.setFontSize(gfx, 40)
    gfx.fill = Color(1.0, 1.0, 1.0, 1.0)  // Draw game info
    gfx.textAlign = TextAlignment.Center
    gfx.fillText(s"${game.wave.number}", 1789 * resizeW, 950 * resizeH)
    
    // Show controls if necessary
    if (this.showControls) this.renderControls(gfx)
    
    // Show FPS if necessary
    if (this.showFPS) this.fps(Time.elapsedTime, canvas)
  }
  
 
  /*
   * HELPER FUNCTIONS
   */
  
  
  /** Function to set the font in the given graphics to the given size of gamegirl.ttf. */
  def setFontSize(gfx: GraphicsContext, size: Double) = {
    gfx.setFont(Font.loadFont("file:assets/font/gamegirl.ttf", (size * (resizeW min resizeH)).toInt))
  }
  
  /** Converts any grid position to a coordinate position on the canvas. */
  def canvasCoords(x: Double, y: Double): (Double, Double) = {
    (x * this.gridW, y * this.gridH)
  }
  
  /** Converts a radius to elliptical radii. */
  def radius(r: Double): (Double, Double) = {
    (r * this.gridW, r * this.gridH)
  }
  
  
  /*
   * FADING FUNCTIONALITY
   */
  
  
  /** Fading functionality for changing the background. */
  private var fadeCountdown = 0.0
  private val fadeTime = 5.0
  private var latestFadeWave = -1
  private def prevAlpha = (fadeCountdown / fadeTime) max 0
  def fade(elapsedTime: Double) = this.fadeCountdown -= elapsedTime
  def startFade() = {
    val wave = Main.currentGame.wave.number
    if (wave != latestFadeWave && fadeCountdown <= 0) {
      this.latestFadeWave = wave
      this.fadeCountdown = this.fadeTime
    }
  }
  
  
  /*
   * GAME ELEMENT RENDERING
   */
  
  
  /** Renders the towers. */
  private def renderTowers(gfx: GraphicsContext, towers: Buffer[Tower]) = {
    val towerIterator = towers.clone.iterator
    while (towerIterator.hasNext) {
      val t = towerIterator.next
      val (x, y) = this.canvasCoords(t.pos.x, t.pos.y)
      t match {
        case t0: CannonTower1 => Animate("cannondog1", x, y, gfx)
        case t1: CannonTower2 => Animate("cannondog2", x, y, gfx)
        case t2: CannonTower3 => Animate("cannondog3", x, y, gfx)
        case t3: BoomerTower1 => Animate("koala1", x, y, gfx)
        case t4: BoomerTower2 => Animate("koala2", x, y, gfx)
        case t5: HomingTower1 => Animate("panda1", x, y, gfx)
        case t6: HomingTower2 => Animate("panda2", x, y, gfx)
        case _ => throw new RenderingException("Invalid tower type")
      }
    }
  }
  
  /** Preloaded images for all projectiles. */
  private var bulletProjImage:    Image = ImageLoader("bulletproj")
  private var homingProjImage:    Image = ImageLoader("homingproj")
  private var boomerangProjImage: Image = ImageLoader("boomerangproj")
  
  /** Renders the projectiles. */
  private def renderProjectiles(gfx: GraphicsContext, projectiles: Buffer[Projectile]) = {
    gfx.fill = Color(1.0, 1.0, 1.0, 1.0)
    val proj = projectiles.clone.iterator
    while (proj.hasNext) {
      val p = proj.next
      if (p != null) {
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
  }
  
  /** Renders the enemies as coloured circles of the correct size with HP bars. */
  private def renderEnemies(gfx: GraphicsContext, enemies: Buffer[Enemy]) = {
    val enem = enemies.clone().iterator
    while (enem.hasNext) {
      val e = enem.next
      if (e != null) {
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
  }
  
  
  
  
  
  /*
   * OTHER GUI RENDERING FUNCTIONS
   */
  
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

  /** Renders the active tower in the towershop that is being purchased */
  def renderActiveTower(canvas: Canvas, game: Game, gridX: Double, gridY: Double) = {
    val gfx = canvas.graphicsContext2D
    val (x, y)   = this.canvasCoords(gridX, gridY)
    val (cx, cy) = this.canvasCoords(gridX + 0.5, gridY + 0.5)
    if (game.shop.active) {
      val tower = game.shop.activeTower.get
      tower match {
        case c: CannonTower1 => Animate("cannondog1", x, y, gfx)
        case b: BoomerTower1 => Animate("koala1",     x, y, gfx)
        case h: HomingTower1 => Animate("panda1",     x, y, gfx)
        case _ => throw new RenderingException("Invalid tower type")
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
      val b = InGameScene.b_upgrd
      this.setFontSize(gfx, 20)
      val tx = resizeW * (b.x + 28)
      val ty = resizeH * (b.y + 2)
      gfx.fill = Color(1.0, 1.0, 1.0, 1.0)
      gfx.textAlign = TextAlignment.Center
      gfx.fillText("$" + tower.upgrade.get.price.toString(), tx, ty)
    }
    gfx.translate(-this.gridW / 2, -this.gridH / 2)
  }
  
  
  /*
   * RENDERING EFFECTS
   */
  
  
  
  /** Renders the ongoing special effects in the effects object. */
  def renderEffects(gfx: GraphicsContext) = {  
    this.setFontSize(gfx, 20)
    gfx.fill = Color.White
    for (eff <- Effects.effects) {
      eff match {
        case m: gui.Effects.MoneyEffect => {
          val (x, y) = this.canvasCoords(m.x, m.y)
          gfx.fillText(m.text, x, y)
        }
        case t: gui.Effects.TowerupEffect => {
          val (x, y) = this.canvasCoords(t.x, t.y)
          Animate.animate("towerup", x, y, gfx, 5, t.age)
        }
        case e: gui.Effects.ExplosionEffect => {
          val (x, y) = this.canvasCoords(e.x, e.y)
          Animate.animate("explosion", x - 30, y - 30, gfx, 3, e.age)
        }
        case _ => throw new RenderingException("Invalid effect type")
      }
    }
  }
  
  
  /*
   * CONTROLS
   */
  
  
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
  
  /** The control functionality and control list. */
  private var showControls = false
  private var controlsTimer = 0
  def toggleControls() = this.showControls = !this.showControls
  private val controls = Array[String](
    "[Spacebar]: Fast forward",
    "[Spacebar]: Next Wave",
    "[Enter]:    Upgrade selected tower",
    "[1]:        Buy cannon dog",
    "[2]:        Buy boomerang koala",
    "[3]:        Buy tank panda",
    "[F11]:      Fullscreen",
    "[Esc]:      Return to main menu"
  )
  
  
  /*
   * FPS
   */
  
  
  /** FPS functionality variables and functions. */
  var previousFrames = Array[Int]()
  private var showFPS = false
  def toggleFPS() = this.showFPS = !this.showFPS
  
  /** Renders the FPS to the corner of the screen as an average of the last 60 frames. */
  def fps(elapsedTime: Double, canvas: Canvas) = {
    val gfx = canvas.graphicsContext2D
    val fps = (1.0 / elapsedTime).toInt
    this.setFontSize(gfx, 40)
    gfx.textAlign = TextAlignment.Left
    gfx.fill = Color(1.0, 1.0, 1.0, 1.0)
    previousFrames = fps +: previousFrames
    if (previousFrames.size > 60) previousFrames = previousFrames.tail
    val avg = previousFrames.sum / previousFrames.size
    gfx.fillText(avg.toString, 20, 50)
  }
  
  
  /*
   * VARIABLES
   */
  
  
  /** The background image constructed in the prerender. */
  private var bgs = Buffer[Image]()

  /** The width W and height H of the current scene. Updated each frame. */
  var W: Double = 1920
  var H: Double = 1080
  
  /** The height of the main game area and sidebar area. */
  def mainH = 840 * (H / 1080)
  def sideH = 240 * (H / 1080)
  
  /** The amount of columns and rows in the game. Loaded in prerender. */
  var gCols: Int = 0
  var gRows: Int = 0  
  
  /** The grid width and height. */
  def gridW: Double = this.W / gCols
  def gridH: Double = this.mainH / gRows
  
  /** The resizing factors for width and height directions. */
  def resizeW = W / 1920
  def resizeH = H / 1080
  
  /** Frequency of seasons changing in waves. */
  val seasonfreq = 3
  
  
  /*
   * PRERENDERING
   */
  

  /** Function to load the game dimensions and render the background image. */
  def prerender(game: Game) = {

    // Clear the old backgrounds
    this.bgs = Buffer[Image]()
    
    // Loading a canvas to paint the background on for all the alternatives
    val canvas1 = new Canvas(1920, 1080)
    val canvas2 = new Canvas(1920, 1080)
    val canvas3 = new Canvas(1920, 1080)
    val canvas4 = new Canvas(1920, 1080)
    
    // Loading the canvas graphics to draw the background with for all the alternatives
    val gfx1 = canvas1.graphicsContext2D
    val gfx2 = canvas2.graphicsContext2D
    val gfx3 = canvas3.graphicsContext2D
    val gfx4 = canvas4.graphicsContext2D
    
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
    
    // Loading the spritesheets for all the alternatives
    val ss1: Image = ImageLoader("ss_groundGrass")
    val ss2: Image = ImageLoader("ss_groundFall")
    val ss3: Image = ImageLoader("ss_groundSnow")
    val ss4: Image = ImageLoader("ss_groundSpring")
    
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
          else                    (8, 1)  //  |Grass
        }
        
        // Draws the correct part of the sprite to the correct coordinates
        gfx1.drawImage(ss1, sx*60, sy*60, 60, 60, (i-1)*60, (j-1)*60, 60, 60)
        gfx2.drawImage(ss2, sx*60, sy*60, 60, 60, (i-1)*60, (j-1)*60, 60, 60)
        gfx3.drawImage(ss3, sx*60, sy*60, 60, 60, (i-1)*60, (j-1)*60, 60, 60)
        gfx4.drawImage(ss4, sx*60, sy*60, 60, 60, (i-1)*60, (j-1)*60, 60, 60)
      }
    }
    
    // Drawing the props
    for (p <- game.props) {
      var (i, j, w, h) = p.id match {
        case "bush" => (0, 3, 1, 1)
        case "tree" => (1, 3, 2, 2)
      }
      i *= 60; j *= 60; w *= 60; h *= 60
      gfx1.drawImage(ss1, i, j, w, h, p.x * 60, p.y * 60, 1.5 * w, 1.5 * h)
      gfx2.drawImage(ss2, i, j, w, h, p.x * 60, p.y * 60, 1.5 * w, 1.5 * h)
      gfx3.drawImage(ss3, i, j, w, h, p.x * 60, p.y * 60, 1.5 * w, 1.5 * h)
      gfx4.drawImage(ss4, i, j, w, h, p.x * 60, p.y * 60, 1.5 * w, 1.5 * h)
    }
    
    // Drawing the sidebar
    val sidebar = ImageLoader("sidebar")
    gfx1.drawImage(sidebar, 0, 840)
    gfx2.drawImage(sidebar, 0, 840)
    gfx3.drawImage(sidebar, 0, 840)
    gfx4.drawImage(sidebar, 0, 840)

    // Creating a new writable image
    val image1 = new WritableImage(1920, 1080)
    val image2 = new WritableImage(1920, 1080)
    val image3 = new WritableImage(1920, 1080)
    val image4 = new WritableImage(1920, 1080)
    
    // Snapshotting the canvas and saving it as the background
    this.bgs += canvas1.snapshot(new SnapshotParameters(), image1)
    this.bgs += canvas2.snapshot(new SnapshotParameters(), image2)
    this.bgs += canvas3.snapshot(new SnapshotParameters(), image3)
    this.bgs += canvas4.snapshot(new SnapshotParameters(), image4)
  }
  
  /** Function to throw a rendering exception if the given case fails. */
  def renderRequire(requirement: => Boolean, msg: String) {
    if (!requirement) throw new RenderingException(msg)
  }
}

/** An exception class for rendering expections if needed. */
class RenderingException(msg: String) extends Exception(msg)



