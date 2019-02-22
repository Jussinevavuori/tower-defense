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

object Render {

  /* Renders a given tower defense game to the given canvas every time it is called.
   */
  def renderGame(game: Game, canvas: Canvas, selectedTower: Option[Tower]): Unit = {

    if (this.mainBg == null) throw new RenderingException(
      "Main background is null. Prerender must be performed before rendering")

    // Loading the graphics of the canvas and drawing background
    val gfx = canvas.graphicsContext2D
    gfx.drawImage(this.mainBg, 0, 0)

    // Translating all other elements by half a grid square
    gfx.translate(0.5 * this.gridW, 0.5 * this.gridH)

    // Render all the dynamic elements
    this.renderEnemies(gfx, game.enemies)
    this.renderProjectiles(gfx, game.projectiles)
    this.renderEffects(gfx)
    
    // Translating back
    gfx.translate(-0.5 * this.gridW, -0.5 * this.gridH)

    // Animating all the sprites
    this.renderTowers(gfx, game.towers)

  }

  /* Renders a given tower defense game's sidebar to the bottom
   * of a fullscreen canvas when called
   */
  def renderSide(game: Game, canvas: Canvas): Unit = {

    if (this.sideBg == null) throw new RenderingException(
      "Side background is null. Prerender must be performed before rendering")

    // Loading the graphics of the canvas and drawing background
    val gfx = canvas.graphicsContext2D
    gfx.drawImage(this.sideBg, 0, 0)
    
    // Translate to bottom of main canvas
    gfx.translate(0, this.mainH)
    
    // Draw player and wave information
    gfx.fill = Color(1.0, 1.0, 1.0, 1.0)
    gfx.font = this.bigFont
    gfx.textAlign = TextAlignment.Center
    gfx.fillText(s"${game.wave.number}", 1789, 110)
    gfx.font = this.mediumFont
    gfx.fillText(s"${game.player.health.toInt} HP", 192, 182)    
    gfx.fillText(s"${"$"} ${game.player.money}", 192, 99)
    
    // Shop prices
    gfx.textAlign = TextAlignment.Center
    gfx.font = this.smallFont
    gfx.fillText("$ " + new    CannonTower1(0, 0).price.toString,  760, 204)
    gfx.fillText("$ " + new BoomerangTower1(0, 0).price.toString,  960, 204)
    gfx.fillText("$ " + new    HomingTower1(0, 0).price.toString, 1160, 204)
    
    // Translate back
    gfx.translate(0, -this.mainH)
  }

  
  
  
  // Renders the active tower in the towershop that is being purchased
  def renderActiveTower(canvas: Canvas, game: Game, mx: Double, my: Double): Unit = {

    val gfx = canvas.graphicsContext2D
    if (game.shop.active) {
      game.shop.activeTower.get.typeid match {
        case "c1" => Animate("cannondog1", mx, my, this.gridW, this.gridH, gfx)
        case "b1" => Animate("koala1",     mx, my, this.gridW, this.gridH, gfx)
        case "h1" => Animate("mage",       mx, my, this.gridW, this.gridH, gfx)
        case _    => Animate("cannondog1", mx, my, this.gridW, this.gridH, gfx)
      }
    }
  }
  
  
  
  // Renders a selectable tower when mouse is hovering over it  
  def renderSelectableTower(canvas: Canvas, game: Game, tower: Tower): Unit = {
    
    val gfx = canvas.graphicsContext2D
    gfx.fill = Color(1.0, 1.0, 1.0, 0.2)
    val (x, y) = this.canvasCoords(tower.pos.x + 0.5, tower.pos.y + 0.5)
    val (rx, ry) = (0.7 * this.gridW, 0.7 * this.gridH)
    gfx.fillOval(x - rx, y - ry, 2 * rx, 2 * ry)
  }
  
  
  // Renders the selected tower  
  def renderSelectedTower(canvas: Canvas, tower: Tower): Unit = {
    
    val gfx = canvas.graphicsContext2D
    gfx.translate(this.gridW / 2, this.gridH / 2)
    gfx.fill   = Color(1.0, 1.0, 1.0, 0.07)
    gfx.stroke = Color(1.0, 1.0, 1.0, 0.70)
    gfx.lineWidth = 10
    val (x, y) = this.canvasCoords(tower.pos.x, tower.pos.y)
    val rx = tower.radius * gridW
    val ry = tower.radius * gridH
    gfx.fillOval(  x - rx, y - ry, 2 * rx, 2 * ry)
    gfx.strokeOval(x - rx, y - ry, 2 * rx, 2 * ry)
    if (tower.upgrade.isDefined) {
      gfx.font = this.smallFont
      gfx.fill = Color(1.0, 1.0, 1.0, 1.0)
      gfx.textAlign = TextAlignment.Center
      gfx.fillText("$" + tower.upgrade.get.price.toString(), x, y - 46)
    }
    gfx.translate(-this.gridW / 2, -this.gridH / 2)
  }
  
  
  // Renders towers in the shop
  def renderShopTowers(canvas: Canvas): Unit = {
    
    val gfx = canvas.graphicsContext2D
    Animate("cannondog1",  730, 916, 60, 60, gfx)
    Animate("koala1",      930, 916, 60, 60, gfx)
    Animate("mage",       1130, 916, 60, 60, gfx)    
  }

  
  
  
  
  /* Prerenders the background and saves it as a png to the disk, so it doesn't
   * have to be rendered each frame. Must be called with both the main and
   * side canvas and the game when loading the came. Loads the variables declared
   * below
   */

  // The screenshots from preload to be drawn on each frame
  private var mainBg: Image = null
  private var sideBg: Image = null
  
  // Other preloaded graphics
  private var bulletProjImage: Image = this.loadImage("bulletproj")
  private var homingProjImage: Image = this.loadImage("homingproj")
  private var boomerangProjImage: Image = this.loadImage("boomerangproj")

  // Shortcuts for width and height of the canvases and grid, calculated at prerender
  var mainW: Double = 0.0
  var mainH: Double = 0.0
  var sideW: Double = 0.0
  var sideH: Double = 0.0
  var gridW: Double = 0.0
  var gridH: Double = 0.0
  
  // The preloaded fonts
  var smallFont:  Font = null
  var mediumFont: Font = null
  var bigFont:    Font = null

  def prerender(main: Canvas, side: Canvas, game: Game) = {

    // Loading the graphics of the canvases
    val mainGfx = main.graphicsContext2D
    val sideGfx = side.graphicsContext2D

    // Shortcuts for width and height of the canvases and grid
    this.mainW = main.width.value
    this.mainH = main.height.value
    this.sideW = side.width.value
    this.sideH = side.height.value
    this.gridW = mainW / game.cols
    this.gridH = mainH / game.rows
    
    // Creating a two dimensional array containing all the path locations
    // and drawing the background based on the grid
    var paths: Array[(Int, Int)] =
      game.path.toArray().map(p => (p.pos.x.toInt, p.pos.y.toInt))
    val grid: Array[Array[Boolean]] = 
      Array.ofDim[Boolean](game.cols + 2, game.rows + 2)
    
    for { 
      i <- 0 until game.cols
      j <- 0 until game.rows 
    } {
      grid(i + 1)(j + 1) = paths.contains((i, j))
    }
    
    for {
      i <- 1 to game.cols
      j <- 1 to game.rows
    } {
      val (dx, dy) = canvasCoords(i - 1, j - 1)
      val spritesheet: Image = this.loadImage("ss_ground")
      val (sx, sy): (Int, Int) = {
        if (grid(i)(j))                            (4, 1)
        else if (grid(i - 1)(j) && grid(i)(j + 1)) (6, 2)
        else if (grid(i + 1)(j) && grid(i)(j + 1)) (8, 2)
        else if (grid(i + 1)(j) && grid(i)(j - 1)) (8, 0)
        else if (grid(i - 1)(j) && grid(i)(j - 1)) (6, 0)
        else if (grid(i)(j + 1))                   (4, 0)
        else if (grid(i + 1)(j))                   (3, 1)
        else if (grid(i)(j - 1))                   (4, 2)
        else if (grid(i - 1)(j))                   (5, 1)
        else if (grid(i - 1)(j + 1))               (5, 0)
        else if (grid(i + 1)(j + 1))               (3, 0)
        else if (grid(i + 1)(j - 1))               (3, 2)
        else if (grid(i - 1)(j - 1))               (5, 2)
        else                                       (1, 1)
      }
      val (sw, sh) = (60, 60)
      mainGfx.drawImage(spritesheet, sx * sw, sy * sh, sw, sh, dx, dy, this.gridW, this.gridH)
    }

    // Rendering the sidebar graphics to side canvas
    val sidebarImage = this.loadImage("sidebar")
    sideGfx.drawImage(sidebarImage, 0, this.mainH)

    // Creating two new writable images and snapshotting the canvases
    val writableMain = new WritableImage(mainW.toInt, mainH.toInt)
    val writableSide = new WritableImage(sideW.toInt, sideH.toInt)
    val snapshotMain = main.snapshot(new SnapshotParameters(), writableMain)
    val snapshotSide = side.snapshot(new SnapshotParameters(), writableSide)

    // Load the images to the bg variables
    this.mainBg = snapshotMain
    this.sideBg = snapshotSide
    
    // Load font
    val smallFontStream  = new FileInputStream(new File("assets/font/gamegirl.ttf"))
    val mediumFontStream = new FileInputStream(new File("assets/font/gamegirl.ttf"))
    val bigFontStream    = new FileInputStream(new File("assets/font/gamegirl.ttf"))
    this.smallFont  = Font.loadFont(smallFontStream, 20)
    this.mediumFont = Font.loadFont(mediumFontStream, 30)
    this.bigFont    = Font.loadFont(bigFontStream, 40)
    smallFontStream.close()
    mediumFontStream.close()
    bigFontStream.close()
  }

  
  
  // Renders the towers
  private def renderTowers(gfx: GraphicsContext, towers: Buffer[Tower]) = {
    for (t <- towers.sortBy(_.pos.y)) {
      val (x, y) = this.canvasCoords(t.pos.x, t.pos.y)
      t.typeid match {
        case "c1" => Animate("cannondog1", x, y, this.gridW, this.gridH, gfx)
        case "c2" => Animate("cannondog2", x, y, this.gridW, this.gridH, gfx)
        case "c3" => Animate("cannondog3", x, y, this.gridW, this.gridH, gfx)
        case "c4" => Animate("cannondog2", x, y, this.gridW, this.gridH, gfx)
        
        case "b1" => Animate("koala1", x, y, this.gridW, this.gridH, gfx)
        case "b2" => Animate("koala2", x, y, this.gridW, this.gridH, gfx)
        case "b3" => Animate("koala2", x, y, this.gridW, this.gridH, gfx)
        case "b4" => Animate("koala2", x, y, this.gridW, this.gridH, gfx)
        
        case "h1" => Animate("mage", x, y, this.gridW, this.gridH, gfx)
        case "h2" => Animate("mage", x, y, this.gridW, this.gridH, gfx)
        case "h3" => Animate("mage", x, y, this.gridW, this.gridH, gfx)
        case "h4" => Animate("mage", x, y, this.gridW, this.gridH, gfx)
        
        case _    => Animate("cannondog1", x, y, this.gridW, this.gridH, gfx)
      }
    }
  }
  
  
  

  // Renders the projectiles  
  private def renderProjectiles(gfx: GraphicsContext, projectiles: Buffer[Projectile]) = {
    gfx.fill = Color(1.0, 1.0, 1.0, 1.0)
    for (p <- projectiles) {
      val (x, y) = this.canvasCoords(p.pos.x, p.pos.y)
      
      if (p.isInstanceOf[HomingProjectile]) {
        val angle = p.asInstanceOf[HomingProjectile].dir() 
        gfx.translate(x, y)
        gfx.rotate(angle)
        gfx.drawImage(this.homingProjImage, -8, -8, 16, 16)
        gfx.rotate(-angle)
        gfx.translate(-x, -y)
      }
      else if (p.isInstanceOf[BulletProjectile]) {
        gfx.drawImage(this.bulletProjImage, x - 8, y - 8, 16, 16)
      }
      else if (p.isInstanceOf[BoomerangProjectile]) {
        val angle = p.asInstanceOf[BoomerangProjectile].angle
        gfx.translate(x, y)
        gfx.rotate(angle)
        gfx.drawImage(this.boomerangProjImage, -12, -12, 24, 24)
        gfx.rotate(-angle)
        gfx.translate(-x, -y)
      }
    }
  }


  
  
  // Renders the enemies as purple circles of the correct size.
  private def renderEnemies(gfx: GraphicsContext, enemies: Buffer[Enemy]) = {

    for (e <- enemies) {

      import scala.math.max

      gfx.fill = e.typeid match { // Fill based on the color and remaining health
        case "n1" => Color(0.0, 0.0, max(0.0, { e.health / e.maxHealth }), 1.0)
        case "n2" => Color(0.0, max(0.0, { e.health / e.maxHealth }), 0.0, 1.0)
        case "n3" => Color(max(0.0, { e.health / e.maxHealth }), 0.0, 0.0, 1.0)
        case _    => Color(0.0, 0.0, 0.0, 0.5)
      }

      val (x, y) = this.canvasCoords(e.pos.x, e.pos.y)
      val (rx, ry) = (e.size * this.gridW, e.size * this.gridH)
      gfx.fillOval(x - rx, y - ry, 2 * rx, 2 * ry)
    }
  }
  
  
  
  
  // Renders the ongoing special effects in the effects object
  def renderEffects(gfx: GraphicsContext) = {  
    gfx.font = this.smallFont
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
        Animate.animate("towerup", x, y, 16, 16, gfx, 5, t.age)
      }
      else if (eff.isInstanceOf[gui.Effects.ExplosionEffect]) {
        val e = eff.asInstanceOf[gui.Effects.ExplosionEffect]
        val (x, y) = this.canvasCoords(e.x, e.y)
        Animate.animate("explosion", x - 30, y - 30, 60, 60, gfx, 3, e.age)
      }
    }
  }
  

  
  
  // Converts any grid position to a coordinate position on the
  // canvas, given a game and a canvas and a pair of coordinates.
  private def canvasCoords(x: Double, y: Double): (Double, Double) = {
    (x * this.gridW, y * this.gridH)
  }

  
  

  // Renders the FPS in the top corner of the canvas
  var previousFrames = Buffer[Int]()
  def fps(elapsedTime: Double, canvas: Canvas) = {
    val gfx = canvas.graphicsContext2D
    gfx.textAlign = TextAlignment.Left
    gfx.font = this.bigFont
    val fps = (1.0 / elapsedTime).toInt
    previousFrames.append(fps)
    if (previousFrames.size > 60) {
      previousFrames = previousFrames.tail
    }
    val avg = previousFrames.sum / previousFrames.size
    gfx.fill = Color(1.0, 1.0, 1.0, 1.0)
    gfx.fillText(avg.toString, 20, 50)
  }
  
  
  
  
  // Loads an image from "assets/sprites" 
  def loadImage(filename: String): Image = {
    val filepath = "assets/gfx/" + filename + ".png"
    val inputStream = new FileInputStream(filepath)
    val image = new Image(inputStream)
    inputStream.close()
    image
  }  
  

  // Loads a random variation of a filename
  def loadRandomImage(filename: String, chances: Array[Int]): Image = {
    val randomInt = scala.util.Random.nextInt(chances.sum) + 1
    var filepath = ""
    for (i <- 1 to chances.length) {
      val lowerbound = chances.take(i - 1).sum
      val upperbound = chances.take(i).sum
      if (randomInt <= upperbound && randomInt > lowerbound) {
        filepath = s"assets/gfx/$filename${i-1}.png"
      }
    }
    val inputStream = new FileInputStream(filepath)
    val image = new Image(inputStream)
    inputStream.close()
    image
  }

}


// An exception class for rendering expections if needed
class RenderingException(msg: String) extends Exception(msg)





