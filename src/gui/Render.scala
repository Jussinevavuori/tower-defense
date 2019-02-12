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

object Render {

  /* Renders a given tower defense game to the given canvas every
   * time it is called.
   *
   * @param		The game to be rendered
   * @param		The canvas for the game to be rendered on
   *
   * @return	Unit
   */

  def renderGame(game: Game, canvas: Canvas): Unit = {

    if (this.mainBg == null) throw new RenderingException(
      "Main background is null. Prerender must be performed before rendering")

    // Loading the graphics of the canvas and drawing background
    val gfx = canvas.graphicsContext2D
    gfx.drawImage(this.mainBg, 0, 0)

    // Translating all other elements by half a grid square
    gfx.translate(0.5 * this.gridW, 0.5 * this.gridH)

    // Render all the dynamic elements
    this.renderTowerRadii(gfx, game.towers)
    this.renderEnemies(gfx, game.enemies)
    this.renderProjectiles(gfx, game.projectiles)

    // Translating back
    gfx.translate(-0.5 * this.gridW, -0.5 * this.gridH)

    // Animating all the sprites
    this.renderTowers(gfx, game.towers)

  }

  /* Renders a given tower defense game's sidebar to the bottom
   * of a fullscreen canvas when called
   *
   * @param		The game, whose sidebar is to be rendered
   * @param		The canvas for the game to be rendered on
   *
   * @return	Unit
   */

  def renderSide(game: Game, canvas: Canvas): Unit = {

    if (this.sideBg == null) throw new RenderingException(
      "Side background is null. Prerender must be performed before rendering")

    // Loading the graphics of the canvas and drawing background
    val gfx = canvas.graphicsContext2D
    gfx.drawImage(this.sideBg, 0, 0)

    // Translate to bottom of main canvas
    gfx.translate(0, this.mainH)

    // Draw information
    gfx.fill = Color(1.0, 1.0, 1.0, 1.0)
    gfx.fillText(s"${game.player.health.toInt}/100", 73, 97)
    gfx.fillText(s"${game.player.money}", 73, 177)
    //gfx.fillText(s"Wave ${game.wave.number}/${WaveLoader.maxWave}", 20, 150)

    // Translate back
    gfx.translate(0, -this.mainH)
  }

  /* Renders the active tower in the towershop
   */

  def renderActiveTower(canvas: Canvas, game: Game, mx: Double, my: Double): Unit = {

    val gfx = canvas.graphicsContext2D
    if (game.shop.active) {
      Animate("cannondog", mx, my, this.gridW, this.gridH, gfx)
    }

  }

  /* Prerenders the background and saves it as a png to the disk, so it doesn't
   * have to be rendered each frame. Must be called with both the main and
   * side canvas and the game when loading the came.
   */

  private var mainBg: Image = null
  private var sideBg: Image = null

  // Shortcuts for width and height of the canvases and grid, calculated at prerender
  var mainW: Double = 0.0
  var mainH: Double = 0.0
  var sideW: Double = 0.0
  var sideH: Double = 0.0
  var gridW: Double = 0.0
  var gridH: Double = 0.0

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

    // Rendering the gridded background
    for {
      i <- 0 until game.cols
      j <- 0 until game.rows
    } {
      val (x, y) = canvasCoords(i, j)
      mainGfx.fill = { (i + j) % 2 } match { // Choose correct alternating fill
        case 0 => Color(0.15, 0.15, 0.15, 1.0)
        case _ => Color(0.20, 0.20, 0.20, 1.0)
      }
      mainGfx.fillRect(x, y, this.gridW, this.gridH)
    }

    // Translating to center of squares
    mainGfx.translate(0.5 * this.gridW, 0.5 * this.gridH)

    // Rendering the path
    mainGfx.fill = Color(1.0, 0.0, 0.0, 1.0) // Red color for path markers
    val (rx, ry) = (0.1 * this.gridW, 0.1 * this.gridH) // The oval radii
    var path: Option[Path] = Some(game.path) // The current path starting with the initial game path
    while (path.isDefined) { // Going through the path chain and drawing a circle for each one
      val (x, y) = this.canvasCoords(path.get.pos.x, path.get.pos.y)
      mainGfx.fillOval(x - rx, y - ry, 2 * rx, 2 * ry) // Drawing the circle for tha path segment
      path = path.get.next // Loading the next path as the current path
    }

    // Translating back
    mainGfx.translate(-0.5 * this.gridW, -0.5 * this.gridH)

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
    val fontStream = new FileInputStream(new File("assets/font/gamegirl.ttf"))
    val gamegirlFont = Font.loadFont(fontStream, 40)
    fontStream.close()
    mainGfx.font = gamegirlFont 
    sideGfx.font = gamegirlFont 
  }

  /* Renders the towers as blue circles
   */

  private def renderTowers(gfx: GraphicsContext, towers: Buffer[Tower]) = {
    for (t <- towers) {
      val (x, y) = this.canvasCoords(t.pos.x, t.pos.y)
      Animate("cannondog", x, y, this.gridW, this.gridH, gfx)
    }
  }
  
  /* Renders the radii of the towers
   */
  
  private def renderTowerRadii(gfx: GraphicsContext, towers: Buffer[Tower]) = {
    gfx.fill   = Color(0.0, 0.8, 0.2, 0.10)
    gfx.stroke = Color(0.0, 0.8, 0.2, 0.20)
    for (t <- towers) {
      val (x, y) = this.canvasCoords(t.pos.x, t.pos.y)
      val r = t.radius
      gfx.fillOval(x - r, y - r, 2 * r, 2 * r)
      gfx.strokeOval(x - r, y - r, 2 * r, 2 * r)
    }

  }

  /* Renders the towers targets with white lines pointing from the
   * tower to the towers target if the tower has a target.
   */

  private def renderTowerTargets(gfx: GraphicsContext, towers: Buffer[Tower]) = {
    gfx.lineWidth = 3.0
    gfx.stroke = Color(1.0, 1.0, 1.0, 1.0)
    for (t <- towers) {
      if (t.target.isDefined && t.hasShot) {
        val trgP = t.target.get.pos // Target pos
        val towP = t.pos // Tower pos
        val (trgX, trgY) = this.canvasCoords(trgP.x, trgP.y)
        val (towX, towY) = this.canvasCoords(towP.x, towP.y)
        gfx.strokeLine(trgX, trgY, towX, towY)
      }
    }
  }
  
  /* Renders the projectiles
   */
  
  private def renderProjectiles(gfx: GraphicsContext, projectiles: Buffer[Projectile]) = {
    gfx.fill = Color(1.0, 1.0, 1.0, 1.0)
    for (p <- projectiles) {
      val (x, y) = this.canvasCoords(p.pos.x, p.pos.y)
      gfx.fillOval(x - 3, y - 3, 6, 6)
    }
  }

  /* Renders the enemies as purple circles of the correct size.
   */

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

  /* Converts any grid position to a coordinate position on the
   * canvas, given a game and a canvas and a pair of coordinates.
   */

  private def canvasCoords(x: Double, y: Double): (Double, Double) = {
    (x * this.gridW, y * this.gridH)
  }

  /* Renders the FPS in the top corner of the canvas
   */

  var previousFrames = Buffer[Int]()
  def fps(elapsedTime: Double, canvas: Canvas) = {
    val gfx = canvas.graphicsContext2D
    val fps = (1.0 / elapsedTime).toInt
    previousFrames.append(fps)
    if (previousFrames.size > 60) {
      previousFrames = previousFrames.tail
    }
    val avg = previousFrames.sum / previousFrames.size
    gfx.fill = Color(1.0, 1.0, 1.0, 1.0)
    gfx.fillText(avg.toString, 20, 50)
  }
  
  /* Loads an image from "assets/sprites" 
   */
  
  def loadImage(filename: String): Image = {
    val filepath = "assets/sprites/" + filename + ".png"
    val inputStream = new FileInputStream(filepath)
    val image = new Image(inputStream)
    inputStream.close()
    image
  }

}

// An exception class for rendering expections if needed
class RenderingException(msg: String) extends Exception(msg)





