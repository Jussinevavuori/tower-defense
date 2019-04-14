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

/** InGameScene is the scene for the in game action, running the main game loop and rendering the
 *  main game elements and handling the game input.
 */
object InGameScene extends AnimationScene {
  
  /*
   * INITIALIZING
   */
  
  /** The main game canvas. */
  val gameCanvas = new Canvas(1920, 1080)
  gameCanvas.requestFocus()
  
  /** The interaction canvas on top of the game canvas for interacting with the game. */
  val interactionCanvas = new Canvas(1920, 1080) {
    disable = true
  }
  
  /** The gameover canvas on top of all canvases for when the game is over. */
  val gameoverCanvas = new Canvas(1920, 1080) {
    disable = true
    visible = false
  }
  
  /** Main animation loop. */
  var animation = AnimationTimer { now =>

    Time.updateElapsedTime(now)
    
    // Updating of the game is now done in a concurrent thread from the GameRunner object
    // Main.currentGame.update(Time.elapsedTime)

    resize()
    
    // Render the game
    Animate.advance()
    Effects.advance()
    Render.renderGame(Main.currentGame, gameCanvas, selectedTower)
    Render.fade(Time.elapsedTime)

    // Render the interaction canvas
    interactionCanvas.graphicsContext2D.clearRect(0, 0, interactionCanvas.getWidth, interactionCanvas.getHeight)
    val selectableTower = Actions.findSelectableTower(Main.currentGame, gridX - 0.5, gridY - 0.5)
    if (selectableTower.isDefined) Render.renderSelectableTower(this.interactionCanvas, Main.currentGame, selectableTower.get)
    if (selectedTower.isDefined) Render.renderSelectedTower(this.interactionCanvas, selectedTower.get)
    if (Main.currentGame.shop.active) Render.renderActiveTower(this.interactionCanvas, Main.currentGame, gridX, gridY)
    Render.renderShopTowers(this.interactionCanvas)

    // Game over on death
    if (Main.currentGame.player.dead) {
      gameoverCanvas.disable = false
      gameoverCanvas.visible = true
      Music.stopLoop()
      Main.currentGame.enemies.foreach(_.damage(Int.MaxValue))
      Render.renderGameover(gameoverCanvas)
      if (!Main.gameover) {
        Audio.play("gameover.wav", 0.5)
      }
    }
  }
  
  
  /** Loadup function. */
  override def loadUp() = {
    b_music.update()
    Main.gamerunner = new Thread(GameRunner)
    Main.gamerunner.start()
  }
  
  /** Shutdown function. */
  override def shutDown() = {
    GameRunner.terminate()
  }

  /** Set to true upon entering godmode. */
  var godmode = false
  
  /** The currently selected tower. */
  private var selectedTower: Option[Tower] = None
 
  /** Function to convert a screen coordinate to a game grid coordinate. */
  private def toGridX(x: Double) = x / Render.gridW
  private def toGridY(y: Double) = y / Render.gridH
  
  /** The mouse X and Y coordinates, updated each time mouse is moved. */
  private var mouseX = 0.0
  private var mouseY = 0.0
  
  /** The mouse X and Y coordinates as game grid coordinates. */
  private def gridX = this.toGridX(this.mouseX)
  private def gridY = this.toGridY(this.mouseY)
  
  /*
   * CREATING MENUBAR AND MENU BUTTONS
   */
  
  /** Menu option to save the game. */
  val mSave = new MenuItem("Save") {
    onAction = (e: AE) => {
      Actions.save()
    }
  }
  
  /** Menu option to show controls. */
  val mControl = new MenuItem("Controls") {
    onAction = (e: AE) => {
      Render.toggleControls()
    }
  }
  
  /** Menu option to show FPS. */
  val mShowFPS = new MenuItem("Show FPS") {
    onAction = (e: AE) => {
      Render.toggleFPS()
    }
  }
  
  /** Menu option to activate godmode. */
  val mGodmode = new MenuItem("Godmode") {
    onAction = (e: AE) => {
      Actions.activateGodmode(Main.currentGame)
    }
  }
  
  /** Menu option to return to main menu. */
  val mMainmenu = new MenuItem("Main menu") {
    onAction = (e: AE) => {
      if (Main.currentGame.wave.number > 1) Actions.save
      Music.changeMusic("warriors")
      Main.changeStatus(ProgramStatus.MainMenu)
    }
  }
  
  /** Menu option to close window. */
  val mExit = new MenuItem("Exit") {
    onAction = (e: AE) => {
      sys.exit(0)
    }
  }

  /** A new menu for all the menu options. */
  val menu = new Menu("Menu") { items =
    List(mSave, sep, mControl, sep, mShowFPS, sep, mGodmode, sep, mMainmenu, sep, mExit)
  }
  
  /** A menubar for the menu. */
  val menuBar = new MenuBar {
    menus = List(menu)
    visible = false
  }
  
  /*
   * BUTTONS
   */

  /** Two invisible rectangles for scaling purposes. */
  val scl1 = Rectangle(0, 0, 0, 0)
  val scl2 = Rectangle(1920, 1080, 0, 0)
  
  /** The cannon tower shop button. */
  val b_shop1 = new MovableImageButton(ImageLoader("shopButton"), 701, 887) {
    override def onClick() = {
      Actions.buyCannonTower(Main.currentGame, godmode)
    }
  }
  
  /** The boomerang tower shop button. */
  val b_shop2 = new MovableImageButton(ImageLoader("shopButton"), 901, 887) {
    override def onClick() = {
      Actions.buyBoomerTower(Main.currentGame, godmode)
    }
  }
  
  /** The homing tower shop button. */
  val b_shop3 = new MovableImageButton(ImageLoader("shopButton"), 1101, 887) {
    override def onClick() = {
      Actions.buyHomingTower(Main.currentGame, godmode)
    }
  }
  
  /** The upgrade button. */
  val b_upgrd = new MovableImageButton(ImageLoader("upgradeButton"), 0.0, 0.0) {
    this.visible = false
    override def onClick() = {
      selectedTower = Actions.upgradeTower(Main.currentGame, selectedTower)
      this.visible = selectedTower.isDefined && !selectedTower.get.upgrade.isEmpty
    }
  }
  
  /** The next wave button. */
  val b_nextw = new MovableImageButton(ImageLoader("nextwaveButton"), 1729, 972) {
    override def onClick() = {
      Actions.loadNextWave(Main.currentGame)
    }
  }
  
  /** The fast forward button. */
  val b_fastf = new MovableImageButton(ImageLoader("fastforwardButton"), 1600, 972) {
    override def onClick()   = {
      Actions.toggleFastForward(Main.currentGame, true)
    }
    override def onRelease() = {
      Actions.toggleFastForward(Main.currentGame, false)
    }
    override def onExit()    = {
      Actions.toggleFastForward(Main.currentGame, false)
    }
  }
  
  /** The cannon tower lock. */
  val b_lock1 = new MovableImageButton(ImageLoader("shopLocked"), 701, 887) {
    override val interactive = false
    this.visible = false
    override def onClick() = Audio.play("error.wav")
  }
  
  /** The boomerang tower lock. */
  val b_lock2 = new MovableImageButton(ImageLoader("shopLocked"), 901, 887) {
    override val interactive = false
    this.visible = true
    override def onClick() = Audio.play("error.wav")    
  }
  
  /** The homing tower lock. */
  val b_lock3 = new MovableImageButton(ImageLoader("shopLocked"), 1101, 887) {
    override val interactive = false
    this.visible = true
    override def onClick() = Audio.play("error.wav")
  }
  
  /** Button to toggle the music. */
  val b_music = Music.button()
  
  /** A list of all resizable elements. */
  val resizeList = List[ImageButton](b_shop1, b_shop2, b_shop3, b_upgrd, b_nextw, b_fastf, b_lock1, b_lock2, b_lock3, b_music)
  
  /** A new group for all the buttons. */
  val buttons = new Group() { children = resizeList ++ List(scl1, scl2) }
  
  /* 
   * INPUT
   */
  
  /** Mouse moved. */
  gameCanvas.onMouseMoved = new EH[ME] {
    def handle(me: ME) = {
      
      /** Update mouse coordinates. */
      mouseX = me.getSceneX
      mouseY = me.getSceneY
      
      /** Update menubar visibility. */
      menuBar.visible = mouseY < 32
    }
  }

  /** Mouse click. */
  gameCanvas.onMouseClicked = new EH[ME] {
    def handle(me: ME): Unit = {

      /** Calculate game grid coordinates. */
      val (x, y) = (toGridX(me.getSceneX), toGridY(me.getSceneY))
      
      /** Handle tower purchases. */
      if (Main.currentGame.shop.active) Actions.purchaseTower(Main.currentGame, x, y) // Shop purchases
  
      /** Handle tower slections. */
      else {
        var (sel, sx, sy) = Actions.selectTower(Main.currentGame, x - 0.5, y - 0.5)
        selectedTower = sel
        b_upgrd.moveTo(sx, sy)
        b_upgrd.visible = !(sx == 0.0 && sy == 0.0)
      }
    }
  }

  /** Key pressed. */  
  this.onKeyPressed = new EH[KeyEvent] {
    def handle(ke: KeyEvent) = {
      ke.getCode() match {

        /** F11 toggles fullscreen. */
        case KeyCode.F11    => Main.stage.fullScreen = !Main.stage.fullScreen.value

        /** 1, 2 and 3 buy towers. */
        case KeyCode.DIGIT1 => Actions.buyCannonTower(Main.currentGame, godmode)
        case KeyCode.DIGIT2 => Actions.buyBoomerTower(Main.currentGame, godmode)
        case KeyCode.DIGIT3 => Actions.buyHomingTower(Main.currentGame, godmode)

        /** All keys return to main menu upon game over. */
        case k if (Main.gameover) => {
          Main.changeStatus(ProgramStatus.MainMenu)
          Music.changeMusic("warriors")
          Actions.resetSettings()
        }

        /** Space loads next wave. */
        case KeyCode.SPACE if (Main.currentGame.enemies.isEmpty) => Actions.loadNextWave(Main.currentGame)

        /** Space fast forwards. */
        case KeyCode.SPACE => Actions.toggleFastForward(Main.currentGame, true)

        /** Enter upgrades selected tower. */
        case KeyCode.ENTER => selectedTower = Actions.upgradeTower(Main.currentGame, selectedTower)
        
        /** Escape returns to main menu. */
        case KeyCode.ESCAPE => Main.changeStatus(ProgramStatus.MainMenu)
        
        case _ =>
      }
    }
  }
  
  /** Key released. */
  this.onKeyReleased = new EH[KeyEvent] {
    def handle(ke: KeyEvent) = {
      ke.getCode() match {

        // Toggle fast forward off
        case KeyCode.SPACE => Actions.toggleFastForward(Main.currentGame, false)
        
        case _ =>
      }
    }
  }
  
  /*
   * ARRANGING THE LAYOUT
   */
  
  /** Creating the stack. */
  root = new StackPane() {
    children = List(gameCanvas, buttons, interactionCanvas, menuBar, gameoverCanvas)
    alignment = Pos.TopLeft
  }
  
  // Function to resize all dynamic elements on resize
  def resize() = {
    val W = this.getWidth
    val H = this.getHeight
    this.resizeList.foreach(_.resize(W, H))
  }
  


}
























