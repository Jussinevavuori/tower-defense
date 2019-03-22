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
import scalafx.event.ActionEvent
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

object Main extends JFXApp {
  
  
  // The current game running in the GUI
  var currentGame = GameLoader("data/defaultdata.xml")
  
  
  stage = new JFXApp.PrimaryStage {
    
    
    title = "Tower Defense"
    fullScreen = false
    resizable = true
    width = 1280
    height = 720
    

    scene = new Scene {
      
      // Canvas for the game and sidebar and prerendering
      val gameCanvas        = new Canvas(1920, 1080)
      val interactionCanvas = new Canvas(1920, 1080)
      val gameoverCanvas    = new Canvas(1920, 1080)
      val titleCanvas       = new Canvas(1920, 1080)
      gameCanvas.requestFocus()
      interactionCanvas.disable = true
      gameoverCanvas.disable = true
      gameoverCanvas.visible = false
      Render.prerender(gameCanvas, currentGame)
            

      // Private variables for showing the GUI and game correctly
      private var selectedTower: Option[Tower] = None
      private var previousTime: Long = -1
      private var gameover = false
      private var muted = false
      private var godmode = false
      private var showFPS = false
      private var mouseX = 0.0
      private var mouseY = 0.0
      private def gridX = this.toGridX(this.mouseX)
      private def gridY = this.toGridY(this.mouseY)
      private def toGridX(x: Double) = x / Render.gridW
      private def toGridY(y: Double) = y / Render.gridH  
      
      
      
      // ANIMATION TIMERS
      // Show title screen at startup
      val titleScreenTimer: AnimationTimer = AnimationTimer { now =>
        
        if (this.previousTime < 0) this.previousTime = now  
        val elapsedTime: Double = (now - previousTime) / 1000000000.0 
        previousTime = now  // Calculate the elapsed time in seconds
        
        Titlescreen.advance(this.titleCanvas, elapsedTime) // Advance and animate the title screen
        titleCanvas.opacity = Titlescreen.opacity
         
        var started = false  // Start the game and music and stop the titlescreen
        if (Titlescreen.fading && !started) {
          started = true
          mainTimer.start()
          Music.startLoop()
        }
        if (Titlescreen.completed) {
          titleCanvas.visible = false
          titleScreenTimer.stop()
        }
        
        resize()
      }
      titleScreenTimer.start() // Initially start with the titlescreen
      
      
      
      // Creating the rendering timer
      val mainTimer = AnimationTimer { now =>
        
        // Calculate elapsed time and update game based on it
        val elapsedTime: Double = (now - previousTime) / 1000000000.0 
        previousTime = now  // Calculate the elapsed time in seconds
        currentGame.update(elapsedTime)  // Run game
        
        // Render the game
        Animate.advance()
        Effects.advance()
        Render.renderGame(currentGame, gameCanvas, selectedTower)
        
        // Render the interaction canvas
        interactionCanvas.graphicsContext2D.clearRect(0, 0, interactionCanvas.getWidth, interactionCanvas.getHeight)
        val selectableTower = Actions.findSelectableTower(currentGame, gridX - 0.5, gridY - 0.5)
        if (selectableTower.isDefined) Render.renderSelectableTower(this.interactionCanvas, currentGame, selectableTower.get)
        if (selectedTower.isDefined)   Render.renderSelectedTower(this.interactionCanvas, selectedTower.get)
        if (currentGame.shop.active)   Render.renderActiveTower(this.interactionCanvas, currentGame, gridX, gridY)
        if (this.showFPS)              Render.fps(elapsedTime, this.interactionCanvas) 
        Render.renderShopTowers(this.interactionCanvas)
        
        // Game over on death
        if (currentGame.player.dead) {
          gameoverCanvas.disable = false
          gameoverCanvas.visible = true
          Music.stopLoop()
          if (!gameover) Audio.play("gameover.wav", 0.5)
          currentGame.enemies.foreach(_.damage(Int.MaxValue))
          gameover = true
          Render.renderGameover(gameoverCanvas)
        }
                
        resize()
      }
      
      def resize() = {
        val (resizeW, resizeH) = (scene.value.getWidth / 1920, scene.value.getHeight / 1080)
                gameCanvas.width = 1920 * resizeW;          gameCanvas.height = 1080 * resizeH
         interactionCanvas.width = 1920 * resizeW;   interactionCanvas.height = 1080 * resizeH
            gameoverCanvas.width = 1920 * resizeW;      gameoverCanvas.height = 1080 * resizeH
               titleCanvas.width = 1920 * resizeW;         titleCanvas.height = 1080 * resizeH
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
        b_shop1.x =  701 * resizeW; b_shop1.y =  887 * resizeH
        b_shop2.x =  901 * resizeW; b_shop2.y =  887 * resizeH
        b_shop3.x = 1101 * resizeW; b_shop3.y =  887 * resizeH
        b_lock1.x =  701 * resizeW; b_lock1.y =  887 * resizeH
        b_lock2.x =  901 * resizeW; b_lock2.y =  887 * resizeH
        b_lock3.x = 1101 * resizeW; b_lock3.y =  887 * resizeH
        b_nextw.x = 1729 * resizeW; b_nextw.y =  972 * resizeH
        b_fastf.x = 1600 * resizeW; b_fastf.y =  972 * resizeH
        b_music.x = 1856 * resizeW; b_music.y =   32 * resizeH
        b_upgrd.x = b_upgrdX * resizeW; b_upgrd.y = b_upgrdY * resizeH
      }

      
      
      // Creating the menubar with all the options
      val menuBar   = new MenuBar { visible = false }
      val gameMenu  = new Menu("Game");           menuBar.menus.add(gameMenu)      
      val gmNew     = new MenuItem("New game");  gameMenu.items.addAll(gmNew,     new SeparatorMenuItem)
      val gmLoad    = new MenuItem("Load game"); gameMenu.items.addAll(gmLoad,    new SeparatorMenuItem)
      val gmSave    = new MenuItem("Save");      gameMenu.items.addAll(gmSave,    new SeparatorMenuItem)
      val gmShowFPS = new MenuItem("Show FPS");  gameMenu.items.addAll(gmShowFPS, new SeparatorMenuItem)
      val gmGodmode = new MenuItem("Godmode");   gameMenu.items.addAll(gmGodmode, new SeparatorMenuItem)
      val gmExit    = new MenuItem("Exit");      gameMenu.items.addAll(gmExit,    new SeparatorMenuItem)
          
      // Creating invisible GUI buttons that light up when hovered over by a mouse
          
      // Loaded images
      val img_shop                = Render.loadImage("shop")
      val img_shopLocked          = Render.loadImage("shopLocked")
      val img_shopHighlighted     = Render.loadImage("shopHighlighted")
      val img_shopClicked         = Render.loadImage("shopClicked")
      val img_nextwave            = Render.loadImage("nextwave")
      val img_nextwaveHighlighted = Render.loadImage("nextwaveHighlighted")
      val img_nextwaveClicked     = Render.loadImage("nextwaveClicked")
      val img_upgrade             = Render.loadImage("upgrade")
      val img_upgradeHighlighted  = Render.loadImage("upgradeHighlighted")
      val img_upgradeClicked      = Render.loadImage("upgradeClicked")
      val img_fastf               = Render.loadImage("fastforward")
      val img_fastfHighlighted    = Render.loadImage("fastforwardHighlighted")
      val img_fastfClicked        = Render.loadImage("fastforwardClicked")
      val img_noteOn              = Render.loadImage("note_on")
      val img_noteOff             = Render.loadImage("note_off")
      
      // Elements
      val b_leftt = Rectangle(   0,    0,   0,   0) // For scaling purposes
      val b_right = Rectangle(1920, 1080,   0,   0) // For scaling purposes
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
      
      // Standard W and H
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
      
      // Setting properties
      var (b_upgrdX, b_upgrdY) = (0.0, 0.0)
      b_shop1.x =  701; b_shop1.y =  887; b_shop1.pickOnBounds = false
      b_shop2.x =  901; b_shop2.y =  887; b_shop2.pickOnBounds = false
      b_shop3.x = 1101; b_shop3.y =  887; b_shop3.pickOnBounds = false
      b_lock1.x =  701; b_lock1.y =  887; b_shop1.pickOnBounds = false; b_lock1.visible = false
      b_lock2.x =  901; b_lock2.y =  887; b_shop2.pickOnBounds = false; b_lock2.visible = true
      b_lock3.x = 1101; b_lock3.y =  887; b_shop3.pickOnBounds = false; b_lock3.visible = true
      b_nextw.x = 1729; b_nextw.y =  972; b_nextw.pickOnBounds = false
      b_fastf.x = 1600; b_fastf.y =  972; b_fastf.pickOnBounds = false
      b_music.x = 1856; b_music.y =   32; b_music.pickOnBounds = true
      b_upgrd.x = b_upgrdX; b_upgrd.y = b_upgrdY; b_upgrd.pickOnBounds = false; b_upgrd.visible = false
      


      // Highlight on mouse enter
      b_nextw.setOnMouseEntered(new EH[ME] { def handle(e: ME) = b_nextw.image = img_nextwaveHighlighted } )
      b_shop1.setOnMouseEntered(new EH[ME] { def handle(e: ME) = b_shop1.image = img_shopHighlighted     } )
      b_shop2.setOnMouseEntered(new EH[ME] { def handle(e: ME) = b_shop2.image = img_shopHighlighted     } )
      b_shop3.setOnMouseEntered(new EH[ME] { def handle(e: ME) = b_shop3.image = img_shopHighlighted     } )
      b_upgrd.setOnMouseEntered(new EH[ME] { def handle(e: ME) = b_upgrd.image = img_upgradeHighlighted  } )
      b_fastf.setOnMouseEntered(new EH[ME] { def handle(e: ME) = b_fastf.image = img_fastfHighlighted    } )
      
      // Return to normal on mouse exit
      b_nextw.setOnMouseExited(new EH[ME] { def handle(e: ME) = b_nextw.image = img_nextwave } )
      b_shop1.setOnMouseExited(new EH[ME] { def handle(e: ME) = b_shop1.image = img_shop     } )  
      b_shop2.setOnMouseExited(new EH[ME] { def handle(e: ME) = b_shop2.image = img_shop     } )  
      b_shop3.setOnMouseExited(new EH[ME] { def handle(e: ME) = b_shop3.image = img_shop     } )
      b_upgrd.setOnMouseExited(new EH[ME] { def handle(e: ME) = b_upgrd.image = img_upgrade  } )
      b_fastf.setOnMouseExited(new EH[ME] { def handle(e: ME) = {
        b_fastf.image = img_fastf
        Actions.toggleFastForward(currentGame, false)  // Also set fast forward functionality
      }})
      
      // Extra highlight on mouse click
      b_nextw.setOnMousePressed(new EH[ME] { def handle(e: ME) = b_nextw.image = img_nextwaveClicked } )
      b_shop1.setOnMousePressed(new EH[ME] { def handle(e: ME) = b_shop1.image = img_shopClicked     } )
      b_shop2.setOnMousePressed(new EH[ME] { def handle(e: ME) = b_shop2.image = img_shopClicked     } )
      b_shop3.setOnMousePressed(new EH[ME] { def handle(e: ME) = b_shop3.image = img_shopClicked     } )
      b_upgrd.setOnMousePressed(new EH[ME] { def handle(e: ME) = b_upgrd.image = img_upgradeClicked  } )
      b_fastf.setOnMousePressed(new EH[ME] { def handle(e: ME) = {
        b_fastf.image = img_fastfClicked
        Actions.toggleFastForward(currentGame, true)  // Also set fast forward functionality
      }})

      // Return to normal highlight on mouse released
      b_nextw.setOnMouseReleased(new EH[ME] { def handle(e: ME) = b_nextw.image = img_nextwaveHighlighted } )
      b_shop1.setOnMouseReleased(new EH[ME] { def handle(e: ME) = b_shop1.image = img_shopHighlighted     } )
      b_shop2.setOnMouseReleased(new EH[ME] { def handle(e: ME) = b_shop2.image = img_shopHighlighted     } )
      b_shop3.setOnMouseReleased(new EH[ME] { def handle(e: ME) = b_shop3.image = img_shopHighlighted     } )
      b_upgrd.setOnMouseReleased(new EH[ME] { def handle(e: ME) = b_upgrd.image = img_upgradeHighlighted  } )
      b_fastf.setOnMouseReleased(new EH[ME] { def handle(e: ME) = {
        b_fastf.image = img_fastfHighlighted
        Actions.toggleFastForward(currentGame, false)  // Also set fast forward functionality
      }})

      
      
      // Arranging the layout
      val buttons = new Group()
      val stack   = new StackPane()
      buttons.children = List(
          b_leftt, b_right, b_shop1, b_shop2, b_shop3, b_fastf,
          b_lock1, b_lock2, b_lock3, b_nextw, b_upgrd, b_music)
      stack.children   = List(
          gameCanvas, buttons, interactionCanvas,
          menuBar, titleCanvas, gameoverCanvas)
      stack.setAlignment(Pos.TopLeft)
      root = stack

      
      
      // INPUT : ACTIVE GUI BUTTONS (except fast forward)
      b_shop1.setOnMouseClicked(new EH[ME] { def handle(e: ME) = { Actions.buyCannonTower(   currentGame) } } )
      b_shop2.setOnMouseClicked(new EH[ME] { def handle(e: ME) = { Actions.buyBoomerangTower(currentGame) } } )
      b_shop3.setOnMouseClicked(new EH[ME] { def handle(e: ME) = { Actions.buyHomingTower(   currentGame) } } )
      b_lock1.setOnMouseClicked(new EH[ME] { def handle(e: ME) = { Audio.play("error.wav") } } )
      b_lock2.setOnMouseClicked(new EH[ME] { def handle(e: ME) = { Audio.play("error.wav") } } )
      b_lock3.setOnMouseClicked(new EH[ME] { def handle(e: ME) = { Audio.play("error.wav") } } )
      b_upgrd.setOnMouseClicked(new EH[ME] { def handle(e: ME) = {
        selectedTower    = Actions.upgradeTower(currentGame, selectedTower)
        b_upgrd.visible = selectedTower.isDefined && !selectedTower.get.upgrade.isEmpty
      }})
      b_nextw.setOnMouseClicked(new EH[ME] { def handle(e: ME) = {
        Actions.loadNextWave(currentGame)
        checkLocks()
      }})
      b_music.setOnMouseClicked(new EH[ME] { def handle(e: ME) = {
        b_music.setImage(if (!muted) img_noteOff else img_noteOn)
        Music.mute()
        muted = !muted
      }})
 
     
      // INPUT: MOUSE MOVED ON SCREEN
      gameCanvas.onMouseMoved = new EH[ME] { def handle(me: ME) = {
        
          mouseX = me.getSceneX  // Update mouse coordinates
          mouseY = me.getSceneY
          menuBar.visible = mouseY < 32  // Show and hide menubar
        }
      }
            
      
      // INPUT: CLICK ON MAIN CANVAS
      gameCanvas.onMouseClicked = new EH[ME] { def handle(me: ME): Unit = {
          
          val (mx, my) = (me.getSceneX, me.getSceneY)
          val (x, y) = (toGridX(mx), toGridY(my))  // Mouse coordinates
          
          if (currentGame.shop.active) Actions.purchaseTower(currentGame, x, y)  // Shop purchases
          
          else {                                                                  // Tower selection
            var (sel, sx, sy) = Actions.selectTower(currentGame, x - 0.5, y - 0.5)
            selectedTower = sel
            b_upgrdX = sx
            b_upgrdY = sy
            b_upgrd.visible = !(sx == 0.0 && sy == 0.0)
          }
        } 
      }
       
      
      // INPUT: KEY PRESSED
      this.onKeyPressed = new EH[KeyEvent] {
        def handle(ke: KeyEvent) = { ke.getCode() match {
          
            // Fullscreen
            case KeyCode.F11 => fullScreen = !fullScreen.value
     
            // On game over all keys load a new game
            case k if (gameover) => newGame()
          
            // Spacebar skips titlescreen
            case KeyCode.SPACE if (!Titlescreen.completed) => Actions.skipTitleScreen()
            
            // Shortcut to next wave
            case KeyCode.SPACE if (currentGame.enemies.isEmpty) => {
              Actions.loadNextWave(currentGame)
              checkLocks()
            }
            
            // Shortcut to fast forward
            case KeyCode.SPACE => Actions.toggleFastForward(currentGame, true)
            
            // Shortcut to tower upgrade
            case KeyCode.ENTER => selectedTower = Actions.upgradeTower(currentGame, selectedTower)
            
            case _ => 
          }
        }
      }
      
      // INPUT: KEY RELEASED
      this.onKeyReleased = new EH[KeyEvent] {
        def handle(ke: KeyEvent) = { ke.getCode() match {
                
            // Toggle fast forward off
            case KeyCode.SPACE => Actions.toggleFastForward(currentGame, false)  
            case _ =>
          }
        }
      }
      
      // INPUT: GAME OVER CLICK STARTS NEW GAME
      gameoverCanvas.onMouseClicked = new EH[ME] { def handle(e: ME) = {
        newGame()
      }}
      
      
      // INPUT: MENU BUTTONS
      gmSave.onAction    = (e: ActionEvent) => { 
        GameSaver.save(currentGame)
        Audio.play("iosfx.wav")
      }
      gmNew.onAction     = (e: ActionEvent) => { newGame() }
      gmLoad.onAction    = (e: ActionEvent) => { loadGame() }
      gmExit.onAction    = (e: ActionEvent) => { sys.exit(0) }
      gmShowFPS.onAction = (e: ActionEvent) => { this.showFPS = !this.showFPS }
      gmGodmode.onAction = (e: ActionEvent) => {
        godmode = true
        b_lock1.visible = false
        b_lock2.visible = false
        b_lock3.visible = false
        Actions.activateGodmode(currentGame) 
      }
      
      // Helper functions
      
      private def newGame() = {
        currentGame = GameLoader("data/defaultdata.xml")
        resetSettings()
      }
      private def loadGame() = {
        currentGame = GameLoader("data/savedata.xml")
        resetSettings()
      }
      private def resetSettings() = {
        Music.stopLoop()
        Music.startLoop()
        godmode = false
        gameover = false
        b_upgrd.visible = false
        b_lock1.visible = false
        b_lock2.visible = true
        b_lock3.visible = true
        Audio.play("iosfx.wav")
        gameoverCanvas.disable = true
        gameoverCanvas.visible = false
      }  
      private def checkLocks() = {
        b_lock1.visible = TowerInfo.unlockCannon    > currentGame.wave.number && !godmode
        b_lock2.visible = TowerInfo.unlockBoomerang > currentGame.wave.number && !godmode
        b_lock3.visible = TowerInfo.unlockHoming    > currentGame.wave.number && !godmode
      }
    }
  }
}