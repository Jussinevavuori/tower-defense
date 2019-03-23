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
import scalafx.event.{ ActionEvent => AE }
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
    val (resizeW, resizeH) = (this.getWidth / 1920, this.getHeight / 1080)
    gameCanvas.width = 1920 * resizeW; gameCanvas.height = 1080 * resizeH
    interactionCanvas.width = 1920 * resizeW; interactionCanvas.height = 1080 * resizeH
    gameoverCanvas.width = 1920 * resizeW; gameoverCanvas.height = 1080 * resizeH
    b_shop1.setFitWidth(b_shop1StdW * resizeW); b_shop1.setFitHeight(b_shop2StdH * resizeH)
    b_shop2.setFitWidth(b_shop2StdW * resizeW); b_shop2.setFitHeight(b_shop2StdH * resizeH)
    b_shop3.setFitWidth(b_shop3StdW * resizeW); b_shop3.setFitHeight(b_shop3StdH * resizeH)
    b_lock1.setFitWidth(b_lock1StdW * resizeW); b_lock1.setFitHeight(b_lock1StdH * resizeH)
    b_lock2.setFitWidth(b_lock2StdW * resizeW); b_lock2.setFitHeight(b_lock2StdH * resizeH)
    b_lock3.setFitWidth(b_lock3StdW * resizeW); b_lock3.setFitHeight(b_lock3StdH * resizeH)
    b_upgrd.setFitWidth(b_upgrdStdW * resizeW); b_upgrd.setFitHeight(b_upgrdStdH * resizeH)
    b_nextw.setFitWidth(b_nextwStdW * resizeW); b_nextw.setFitHeight(b_nextwStdH * resizeH)
    b_fastf.setFitWidth(b_fastfStdW * resizeW); b_fastf.setFitHeight(b_fastfStdH * resizeH)
    b_music.setFitWidth(b_musicStdW * resizeW); b_music.setFitHeight(b_musicStdH * resizeH)
    b_shop1.x = 701 * resizeW; b_shop1.y = 887 * resizeH
    b_shop2.x = 901 * resizeW; b_shop2.y = 887 * resizeH
    b_shop3.x = 1101 * resizeW; b_shop3.y = 887 * resizeH
    b_lock1.x = 701 * resizeW; b_lock1.y = 887 * resizeH
    b_lock2.x = 901 * resizeW; b_lock2.y = 887 * resizeH
    b_lock3.x = 1101 * resizeW; b_lock3.y = 887 * resizeH
    b_nextw.x = 1729 * resizeW; b_nextw.y = 972 * resizeH
    b_fastf.x = 1600 * resizeW; b_fastf.y = 972 * resizeH
    b_music.x = 1856 * resizeW; b_music.y = 32 * resizeH
    b_upgrd.x = b_upgrdX * resizeW; b_upgrd.y = b_upgrdY * resizeH
  }

  
  /*
   * CREATING MENUBAR AND BUTTONS
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
  
  
  
  /*
   * CREATING DYNAMIC GUI BUTTONE ELEMENTS
   */


  // Loaded images for GUI buttons
  val img_shop = Render.loadImage("shop")
  val img_shopLocked = Render.loadImage("shopLocked")
  val img_shopHighlighted = Render.loadImage("shopHighlighted")
  val img_shopClicked = Render.loadImage("shopClicked")
  val img_nextwave = Render.loadImage("nextwave")
  val img_nextwaveHighlighted = Render.loadImage("nextwaveHighlighted")
  val img_nextwaveClicked = Render.loadImage("nextwaveClicked")
  val img_upgrade = Render.loadImage("upgrade")
  val img_upgradeHighlighted = Render.loadImage("upgradeHighlighted")
  val img_upgradeClicked = Render.loadImage("upgradeClicked")
  val img_fastf = Render.loadImage("fastforward")
  val img_fastfHighlighted = Render.loadImage("fastforwardHighlighted")
  val img_fastfClicked = Render.loadImage("fastforwardClicked")
  val img_noteOn = Render.loadImage("note_on")
  val img_noteOff = Render.loadImage("note_off")

  // GUI buttons
  val b_leftt = Rectangle(0, 0, 0, 0) // For scaling purposes
  val b_right = Rectangle(1920, 1080, 0, 0) // For scaling purposes
  val b_shop1 = new ImageView(this.img_shop)
  val b_shop2 = new ImageView(this.img_shop)
  val b_shop3 = new ImageView(this.img_shop)
  val b_lock1 = new ImageView(this.img_shopLocked)
  val b_lock2 = new ImageView(this.img_shopLocked)
  val b_lock3 = new ImageView(this.img_shopLocked)
  val b_upgrd = new ImageView(this.img_upgrade)
  val b_nextw = new ImageView(this.img_nextwave)
  val b_fastf = new ImageView(this.img_fastf)
  val b_music = new ImageView(this.img_noteOn)

  // Standard widths and heights for all gui buttons for scaling purposes
  val b_shop1StdW = b_shop1.getImage.getWidth; val b_shop1StdH = b_shop1.getImage.getHeight
  val b_shop2StdW = b_shop2.getImage.getWidth; val b_shop2StdH = b_shop2.getImage.getHeight
  val b_shop3StdW = b_shop3.getImage.getWidth; val b_shop3StdH = b_shop3.getImage.getHeight
  val b_lock1StdW = b_lock1.getImage.getWidth; val b_lock1StdH = b_lock1.getImage.getHeight
  val b_lock2StdW = b_lock2.getImage.getWidth; val b_lock2StdH = b_lock2.getImage.getHeight
  val b_lock3StdW = b_lock3.getImage.getWidth; val b_lock3StdH = b_lock3.getImage.getHeight
  val b_upgrdStdW = b_upgrd.getImage.getWidth; val b_upgrdStdH = b_upgrd.getImage.getHeight
  val b_nextwStdW = b_nextw.getImage.getWidth; val b_nextwStdH = b_nextw.getImage.getHeight
  val b_fastfStdW = b_fastf.getImage.getWidth; val b_fastfStdH = b_fastf.getImage.getHeight
  val b_musicStdW = b_music.getImage.getWidth; val b_musicStdH = b_music.getImage.getHeight

  // Setting properties and coordinates for all gui buttons
  var (b_upgrdX, b_upgrdY) = (0.0, 0.0)
  b_shop1.x = 701; b_shop1.y = 887; b_shop1.pickOnBounds = false
  b_shop2.x = 901; b_shop2.y = 887; b_shop2.pickOnBounds = false
  b_shop3.x = 1101; b_shop3.y = 887; b_shop3.pickOnBounds = false
  b_lock1.x = 701; b_lock1.y = 887; b_shop1.pickOnBounds = false; b_lock1.visible = false
  b_lock2.x = 901; b_lock2.y = 887; b_shop2.pickOnBounds = false; b_lock2.visible = true
  b_lock3.x = 1101; b_lock3.y = 887; b_shop3.pickOnBounds = false; b_lock3.visible = true
  b_nextw.x = 1729; b_nextw.y = 972; b_nextw.pickOnBounds = false
  b_fastf.x = 1600; b_fastf.y = 972; b_fastf.pickOnBounds = false
  b_music.x = 1856; b_music.y = 32; b_music.pickOnBounds = true
  b_upgrd.x = b_upgrdX; b_upgrd.y = b_upgrdY; b_upgrd.pickOnBounds = false; b_upgrd.visible = false

  // Highlight on mouse enter
  b_nextw.setOnMouseEntered(new EH[ME] { def handle(e: ME) = b_nextw.image = img_nextwaveHighlighted })
  b_shop1.setOnMouseEntered(new EH[ME] { def handle(e: ME) = b_shop1.image = img_shopHighlighted })
  b_shop2.setOnMouseEntered(new EH[ME] { def handle(e: ME) = b_shop2.image = img_shopHighlighted })
  b_shop3.setOnMouseEntered(new EH[ME] { def handle(e: ME) = b_shop3.image = img_shopHighlighted })
  b_upgrd.setOnMouseEntered(new EH[ME] { def handle(e: ME) = b_upgrd.image = img_upgradeHighlighted })
  b_fastf.setOnMouseEntered(new EH[ME] { def handle(e: ME) = b_fastf.image = img_fastfHighlighted })

  // Return to normal on mouse exit
  b_nextw.setOnMouseExited(new EH[ME] { def handle(e: ME) = b_nextw.image = img_nextwave })
  b_shop1.setOnMouseExited(new EH[ME] { def handle(e: ME) = b_shop1.image = img_shop })
  b_shop2.setOnMouseExited(new EH[ME] { def handle(e: ME) = b_shop2.image = img_shop })
  b_shop3.setOnMouseExited(new EH[ME] { def handle(e: ME) = b_shop3.image = img_shop })
  b_upgrd.setOnMouseExited(new EH[ME] { def handle(e: ME) = b_upgrd.image = img_upgrade })
  b_fastf.setOnMouseExited(new EH[ME] {
    def handle(e: ME) = {
      b_fastf.image = img_fastf
      Actions.toggleFastForward(Main.currentGame, false) // Also set fast forward functionality
    }
  })

  // Extra highlight on mouse click
  b_nextw.setOnMousePressed(new EH[ME] { def handle(e: ME) = b_nextw.image = img_nextwaveClicked })
  b_shop1.setOnMousePressed(new EH[ME] { def handle(e: ME) = b_shop1.image = img_shopClicked })
  b_shop2.setOnMousePressed(new EH[ME] { def handle(e: ME) = b_shop2.image = img_shopClicked })
  b_shop3.setOnMousePressed(new EH[ME] { def handle(e: ME) = b_shop3.image = img_shopClicked })
  b_upgrd.setOnMousePressed(new EH[ME] { def handle(e: ME) = b_upgrd.image = img_upgradeClicked })
  b_fastf.setOnMousePressed(new EH[ME] {
    def handle(e: ME) = {
      b_fastf.image = img_fastfClicked
      Actions.toggleFastForward(Main.currentGame, true) // Also set fast forward functionality
    }
  })

  // Return to normal highlight on mouse released
  b_nextw.setOnMouseReleased(new EH[ME] { def handle(e: ME) = b_nextw.image = img_nextwaveHighlighted })
  b_shop1.setOnMouseReleased(new EH[ME] { def handle(e: ME) = b_shop1.image = img_shopHighlighted })
  b_shop2.setOnMouseReleased(new EH[ME] { def handle(e: ME) = b_shop2.image = img_shopHighlighted })
  b_shop3.setOnMouseReleased(new EH[ME] { def handle(e: ME) = b_shop3.image = img_shopHighlighted })
  b_upgrd.setOnMouseReleased(new EH[ME] { def handle(e: ME) = b_upgrd.image = img_upgradeHighlighted })
  b_fastf.setOnMouseReleased(new EH[ME] {
    def handle(e: ME) = {
      b_fastf.image = img_fastfHighlighted
      Actions.toggleFastForward(Main.currentGame, false) // Also set fast forward functionality
    }
  })

  
  /*
   * GUI BUTTON INPUT
   */
  
  
  b_shop1.setOnMouseClicked(new EH[ME] { def handle(e: ME) = { Actions.buyCannonTower(Main.currentGame, godmode) } })
  b_shop2.setOnMouseClicked(new EH[ME] { def handle(e: ME) = { Actions.buyBoomerangTower(Main.currentGame, godmode) } })
  b_shop3.setOnMouseClicked(new EH[ME] { def handle(e: ME) = { Actions.buyHomingTower(Main.currentGame, godmode) } })
  b_lock1.setOnMouseClicked(new EH[ME] { def handle(e: ME) = { Audio.play("error.wav") } })
  b_lock2.setOnMouseClicked(new EH[ME] { def handle(e: ME) = { Audio.play("error.wav") } })
  b_lock3.setOnMouseClicked(new EH[ME] { def handle(e: ME) = { Audio.play("error.wav") } })
  b_upgrd.setOnMouseClicked(new EH[ME] {
    def handle(e: ME) = {
      selectedTower = Actions.upgradeTower(Main.currentGame, selectedTower)
      b_upgrd.visible = selectedTower.isDefined && !selectedTower.get.upgrade.isEmpty
    }
  })
  b_nextw.setOnMouseClicked(new EH[ME] {
    def handle(e: ME) = {
      Actions.loadNextWave(Main.currentGame)
    }
  })
  b_music.setOnMouseClicked(new EH[ME] {
    def handle(e: ME) = {
      b_music.setImage(if (!muted) img_noteOff else img_noteOn)
      Music.mute()
      muted = !muted
    }
  })
  
  
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
        b_upgrdX = sx
        b_upgrdY = sy
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
   * INPUT: MENU BUTTONS
   */
  
  
  gmSave.onAction    = (e: AE) => Actions.save
  gmNew.onAction     = (e: AE) => Actions.newGame()
  gmLoad.onAction    = (e: AE) => Actions.loadGame()
  gmExit.onAction    = (e: AE) => sys.exit(0)
  gmShowFPS.onAction = (e: AE) => this.showFPS = !this.showFPS
  gmGodmode.onAction = (e: AE) => Actions.activateGodmode(Main.currentGame)
  gmControl.onAction = (e: AE) => Render.toggleControls
  gmMmenu.onAction   = (e: AE) => Main.changeStatus(ProgramStatus.MainMenu)

  
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
























