package gui

import game._
import scalafx.Includes.eventClosureWrapperWithParam
import scalafx.Includes.jfxActionEvent2sfx
import javafx.event.{ EventHandler => EH }
import javafx.scene.input.{ MouseEvent => ME }
import scalafx.event.{ ActionEvent => AE }
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import scalafx.animation.AnimationTimer
import scalafx.scene.control.Menu
import scalafx.scene.control.MenuBar
import scalafx.scene.control.MenuItem
import scalafx.scene.control.SeparatorMenuItem
import scalafx.scene.canvas.Canvas
import scalafx.scene.image.ImageView
import scalafx.scene.layout.StackPane
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.Group
import scalafx.geometry.Pos

object InGameScene extends AnimationScene {
  
  
  /*
   * INITIALIZING VARIABLES AND ELEMETS
   */

  
  // Initializing canvases
  val gameCanvas = new Canvas(1920, 1080)
  val interactionCanvas = new Canvas(1920, 1080)
  val gameoverCanvas = new Canvas(1920, 1080)
  gameCanvas.requestFocus()
  interactionCanvas.disable = true
  gameoverCanvas.disable = true
  gameoverCanvas.visible = false
  Render.prerender(gameCanvas, Main.currentGame)

  // Initializing useful variables and helper functions
  var godmode = false
  private var selectedTower: Option[Tower] = None
  private var muted = false
  private var showFPS = false
  private var mouseX = 0.0
  private var mouseY = 0.0
  private def gridX = this.toGridX(this.mouseX)
  private def gridY = this.toGridY(this.mouseY)
  private def toGridX(x: Double) = x / Render.gridW
  private def toGridY(y: Double) = y / Render.gridH
  
  
  /*
   * RESIZABILITY
   */

  
  // Function to resize all dynamic elements on resize
  def resize() = {
    val W = this.getWidth
    val H = this.getHeight
    gameCanvas.width         = W
    gameCanvas.height        = H
    interactionCanvas.width  = W 
    interactionCanvas.height = H
    gameoverCanvas.width     = W
    gameoverCanvas.height    = H
    b_shop1.resize(W, H)
    b_shop2.resize(W, H)
    b_shop3.resize(W, H)
    b_upgrd.resize(W, H)
    b_nextw.resize(W, H)
    b_fastf.resize(W, H)
    b_lock1.resize(W, H)
    b_lock2.resize(W, H)
    b_lock3.resize(W, H)
    b_music.resize(W, H)
  }

  
  /*
   * CREATING MENUBAR AND MENU BUTTONS
   */
  
  
  // Creating the menubar with all the options
  val menuBar   = new MenuBar { visible = false }
  val gameMenu  = new Menu("Game"); menuBar.menus.add(gameMenu)
  val gmNew     = new MenuItem("New game");  gameMenu.items.addAll(gmNew,     new SeparatorMenuItem)
  val gmLoad    = new MenuItem("Load game"); gameMenu.items.addAll(gmLoad,    new SeparatorMenuItem)
  val gmSave    = new MenuItem("Save");      gameMenu.items.addAll(gmSave,    new SeparatorMenuItem)
  val gmControl = new MenuItem("Controls");  gameMenu.items.addAll(gmControl, new SeparatorMenuItem)
  val gmShowFPS = new MenuItem("Show FPS");  gameMenu.items.addAll(gmShowFPS, new SeparatorMenuItem)
  val gmGodmode = new MenuItem("Godmode");   gameMenu.items.addAll(gmGodmode, new SeparatorMenuItem)
  val gmMmenu   = new MenuItem("Main menu"); gameMenu.items.addAll(gmMmenu,   new SeparatorMenuItem)
  val gmExit    = new MenuItem("Exit");      gameMenu.items.addAll(gmExit)
  gmSave.onAction    = (e: AE) => Actions.save
  gmNew.onAction     = (e: AE) => Actions.newGame()
  gmLoad.onAction    = (e: AE) => Actions.loadGame()
  gmExit.onAction    = (e: AE) => sys.exit(0)
  gmShowFPS.onAction = (e: AE) => this.showFPS = !this.showFPS
  gmGodmode.onAction = (e: AE) => Actions.activateGodmode(Main.currentGame)
  gmControl.onAction = (e: AE) => Render.toggleControls
  gmMmenu.onAction   = (e: AE) => Main.changeStatus(ProgramStatus.MainMenu)
  
  
  
  /*
   * CREATING DYNAMIC GUI BUTTON ELEMENTS
   */


  val b_leftt = Rectangle(0, 0, 0, 0)       // For scaling purposes
  val b_right = Rectangle(1920, 1080, 0, 0) // For scaling purposes
  
  val b_shop1 = new MovableDynamicHoverButton("shopButton", 701, 887) {
    override def onClick() = Actions.buyCannonTower(Main.currentGame, godmode)
  }
  val b_shop2 = new MovableDynamicHoverButton("shopButton", 901, 887) {
    override def onClick() = Actions.buyBoomerangTower(Main.currentGame, godmode)
  }
  val b_shop3 = new MovableDynamicHoverButton("shopButton", 1101, 887) {
    override def onClick() = Actions.buyHomingTower(Main.currentGame, godmode)
  }
  val b_upgrd = new MovableDynamicHoverButton("upgradeButton", 0.0, 0.0) {
    this.visible = false
    override def onClick() = {
      selectedTower = Actions.upgradeTower(Main.currentGame, selectedTower)
      this.visible = selectedTower.isDefined && !selectedTower.get.upgrade.isEmpty
    }
  }
  val b_nextw = new MovableDynamicHoverButton("nextwaveButton", 1729, 972) {
    override def onClick() = Actions.loadNextWave(Main.currentGame)
  }
  val b_fastf = new MovableDynamicHoverButton("fastforwardButton", 1600, 972) {
    override def onClick()   = Actions.toggleFastForward(Main.currentGame, true)
    override def onRelease() = Actions.toggleFastForward(Main.currentGame, false)
    override def onExit()    = Actions.toggleFastForward(Main.currentGame, false)
  }
  val b_music = new MovableDynamicButton(Render.loadImage("note_on"), 1856, 32) {
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
  val b_lock1 = new MovableDynamicButton(Render.loadImage("shopLocked"), 701, 887) {
    this.visible = false
    override def onClick() = Audio.play("error.wav")
  }
  val b_lock2 = new MovableDynamicButton(Render.loadImage("shopLocked"), 901, 887) {
    this.visible = true
    override def onClick() = Audio.play("error.wav")    
  }
  val b_lock3 = new MovableDynamicButton(Render.loadImage("shopLocked"), 1101, 887) {
    this.visible = true
    override def onClick() = Audio.play("error.wav")
  }
  
  
  
  /* 
   * MOUSE INPUT
   */

  
  
  // Mouse moved
  gameCanvas.onMouseMoved = new EH[ME] {
    def handle(me: ME) = {
      mouseX = me.getSceneX // Update mouse coordinates
      mouseY = me.getSceneY
      menuBar.visible = mouseY < 32 // Show and hide menubar
    }
  }

  // Click on main canvas
  gameCanvas.onMouseClicked = new EH[ME] {
    def handle(me: ME): Unit = {

      // Shop purchases
      val (x, y) = (toGridX(me.getSceneX), toGridY(me.getSceneY)) // Mouse coordinates
      if (Main.currentGame.shop.active) Actions.purchaseTower(Main.currentGame, x, y) // Shop purchases
  
      // Tower selections
      else {
        var (sel, sx, sy) = Actions.selectTower(Main.currentGame, x - 0.5, y - 0.5)
        selectedTower = sel
        b_upgrd.moveTo(sx, sy)
        b_upgrd.visible = !(sx == 0.0 && sy == 0.0)
      }
    }
  }
  
  // Gameover click starts new game
  gameoverCanvas.onMouseClicked = new EH[ME] {
    def handle(e: ME) = {
      Actions.newGame()
    }
  }


  /*
   * KEYBOARD INPUT
   */
  
  
  this.onKeyPressed = new EH[KeyEvent] {
    def handle(ke: KeyEvent) = {
      ke.getCode() match {

        // Fullscreen
        case KeyCode.F11    => Main.stage.fullScreen = !Main.stage.fullScreen.value

        // Switch
        case KeyCode.F1     => Main.changeStatus(0)
        case KeyCode.F2     => Main.changeStatus(1)
        case KeyCode.F3     => Main.changeStatus(2)

        // Buy towers
        case KeyCode.DIGIT1 => Actions.buyCannonTower(Main.currentGame, godmode)
        case KeyCode.DIGIT2 => Actions.buyBoomerangTower(Main.currentGame, godmode)
        case KeyCode.DIGIT3 => Actions.buyHomingTower(Main.currentGame, godmode)

        // On game over all keys load a new game
        case k if (Main.gameover) => Actions.newGame()

        // Shortcut to next wave
        case KeyCode.SPACE if (Main.currentGame.enemies.isEmpty) => {
          Actions.loadNextWave(Main.currentGame)
        }

        // Shortcut to fast forward
        case KeyCode.SPACE => Actions.toggleFastForward(Main.currentGame, true)

        // Shortcut to tower upgrade
        case KeyCode.ENTER => selectedTower = Actions.upgradeTower(Main.currentGame, selectedTower)

        case _ =>
      }
    }
  }
  this.onKeyReleased = new EH[KeyEvent] {
    def handle(ke: KeyEvent) = {
      ke.getCode() match {

        // Toggle fast forward off
        case KeyCode.SPACE => Actions.toggleFastForward(Main.currentGame, false)
        case _             =>
      }
    }
  }



  /*
   * ARRANGING THE LAYOUT
   */

  
  val buttons = new Group()
  val stack = new StackPane()
  buttons.children = List(
    b_leftt, b_right, b_shop1, b_shop2, b_shop3, b_fastf,
    b_lock1, b_lock2, b_lock3, b_nextw, b_upgrd, b_music)
  stack.children = List(
    gameCanvas, buttons, interactionCanvas, menuBar, gameoverCanvas)
  stack.setAlignment(Pos.TopLeft)
  root = stack
  
  
  
  /*
   * GAME ANIMATION LOOP
   */
  

  var animation = AnimationTimer { now =>

    Time.updateElapsedTime(now)

    // Render the game
    Animate.advance()
    Effects.advance()
    Render.renderGame(Main.currentGame, gameCanvas, selectedTower)

    // Render the interaction canvas
    interactionCanvas.graphicsContext2D.clearRect(0, 0, interactionCanvas.getWidth, interactionCanvas.getHeight)
    val selectableTower = Actions.findSelectableTower(Main.currentGame, gridX - 0.5, gridY - 0.5)
    if (selectableTower.isDefined) Render.renderSelectableTower(this.interactionCanvas, Main.currentGame, selectableTower.get)
    if (selectedTower.isDefined) Render.renderSelectedTower(this.interactionCanvas, selectedTower.get)
    if (Main.currentGame.shop.active) Render.renderActiveTower(this.interactionCanvas, Main.currentGame, gridX, gridY)
    if (this.showFPS) Render.fps(Time.elapsedTime, this.interactionCanvas)
    Render.renderShopTowers(this.interactionCanvas)

    // Game over on death
    if (Main.currentGame.player.dead) {
      gameoverCanvas.disable = false
      gameoverCanvas.visible = true
      Music.stopLoop()
      if (!Main.gameover) Audio.play("gameover.wav", 0.5)
      Main.currentGame.enemies.foreach(_.damage(Int.MaxValue))
      Render.renderGameover(gameoverCanvas)
    }

    resize()
  }

}
























