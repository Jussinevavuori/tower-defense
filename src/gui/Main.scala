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
    fullScreen = true
    resizable = false

    
    scene = new Scene {
      
      
      
      // Canvas for the game and sidebar and prerendering
      val mainCanvas        = new Canvas(1920, 840)
      val sideCanvas        = new Canvas(1920, 1080)
      val interactionCanvas = new Canvas(1920, 1080)
      val titleCanvas       = new Canvas(1920, 1080)
      mainCanvas.requestFocus()
      interactionCanvas.disable = true
      Render.prerender(mainCanvas, sideCanvas, currentGame)
  
      // Custom cursor
      //this.cursor = new ImageCursor( Render.loadImage("cursor") )
      
      // Private variables for showing the GUI and game correctly
      private var selectedTower: Option[Tower] = None
      private var previousTime: Long = -1
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
      }
      titleScreenTimer.start() // Initially start with the titlescreen
      
      // Creating the rendering timer
      val mainTimer = AnimationTimer { now =>
        
        // Calculate elapsed time and update game based on it
        val elapsedTime: Double = (now - previousTime) / 1000000000.0 
        previousTime = now  // Calculate the elapsed time in seconds
        currentGame.update(elapsedTime)  // Run game
        
        // Clear the interaction canvas
        interactionCanvas.graphicsContext2D.clearRect(0, 0, interactionCanvas.getWidth, interactionCanvas.getHeight)
        
        // Render all the basics
        Animate.advance()
        Effects.advance()
        Render.renderGame(currentGame, this.mainCanvas, selectedTower)
        Render.renderSide(currentGame, this.sideCanvas)
        
        // Render the interaction canvas
        val selectableTower = Actions.findSelectableTower(currentGame, gridX - 0.5, gridY - 0.5)
        if (selectableTower.isDefined) Render.renderSelectableTower(this.interactionCanvas, currentGame, selectableTower.get)
        if (selectedTower.isDefined)   Render.renderSelectedTower(this.interactionCanvas, selectedTower.get)
        if (currentGame.shop.active)   Render.renderActiveTower(this.interactionCanvas, currentGame, mouseX, mouseY)
        if (this.showFPS)              Render.fps(elapsedTime, this.interactionCanvas) 
        Render.renderShopTowers(this.interactionCanvas)
      }

      
      
      // Creating the menubar
      val menuBar   = new MenuBar { visible = false }
      val gameMenu  = new Menu("Game")
      val debugMenu = new Menu("Debug")
      menuBar.menus   = List(gameMenu, debugMenu)
      
      
      
      // Creating the buttons for the menus
      val gmNew        = new MenuItem("New game")
      val gmLoad       = new MenuItem("Load game")
      val gmExit       = new MenuItem("Exit")
      val gmSave       = new MenuItem("Save")
      val dmShowFPS    = new MenuItem("Show FPS")
      val dmGodmode    = new MenuItem("Godmode")
      val dmMute       = new MenuItem("Mute music")
      val dmStartMusic = new MenuItem("Start music")
      val dmStopMusic  = new MenuItem("Stop music")
      
      
      
      // Assigning the menu items to the menus
      gameMenu.items  = List(
          gmNew,  new SeparatorMenuItem,
          gmLoad, new SeparatorMenuItem,
          gmSave, new SeparatorMenuItem,
          gmExit)
      debugMenu.items = List(
          dmShowFPS,    new SeparatorMenuItem,
          dmGodmode,    new SeparatorMenuItem,
          dmMute,       new SeparatorMenuItem,
          dmStartMusic, new SeparatorMenuItem,
          dmStopMusic)
          
          
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
      
      // Elements
      val b_lefttop  = Rectangle(   0,    0,   0,   0) // For scaling purposes
      val b_righttop = Rectangle(1920, 1080,   0,   0) // For scaling purposes
      val b_shop1    = new ImageView( this.img_shop)
      val b_shop2    = new ImageView( this.img_shop)
      val b_shop3    = new ImageView( this.img_shop)
      val b_lock1    = new ImageView( this.img_shopLocked)
      val b_lock2    = new ImageView( this.img_shopLocked)
      val b_lock3    = new ImageView( this.img_shopLocked)
      val b_upgrade  = new ImageView( this.img_upgrade)
      val b_nextwave = new ImageView( this.img_nextwave)
      
      // Setting properties
      b_shop1.x    =  701; b_shop1.y    =  887; b_shop1.pickOnBounds    = false
      b_shop2.x    =  901; b_shop2.y    =  887; b_shop2.pickOnBounds    = false
      b_shop3.x    = 1101; b_shop3.y    =  887; b_shop3.pickOnBounds    = false
      b_lock1.x    =  701; b_lock1.y    =  887; b_shop1.pickOnBounds    = false; b_lock1.visible = false
      b_lock2.x    =  901; b_lock2.y    =  887; b_shop2.pickOnBounds    = false; b_lock2.visible = true
      b_lock3.x    = 1101; b_lock3.y    =  887; b_shop3.pickOnBounds    = false; b_lock3.visible = true
      b_upgrade.x  =   10; b_upgrade.y  =   10; b_upgrade.pickOnBounds  = false; b_upgrade.visible = false
      b_nextwave.x = 1729; b_nextwave.y =  972; b_nextwave.pickOnBounds = false

      // Highlight on mouse enter
      b_nextwave.setOnMouseEntered(  new EH[ME] { def handle(e: ME) = b_nextwave.image = img_nextwaveHighlighted } )
      b_shop1.setOnMouseEntered(     new EH[ME] { def handle(e: ME) = b_shop1.image    = img_shopHighlighted } )
      b_shop2.setOnMouseEntered(     new EH[ME] { def handle(e: ME) = b_shop2.image    = img_shopHighlighted } )
      b_shop3.setOnMouseEntered(     new EH[ME] { def handle(e: ME) = b_shop3.image    = img_shopHighlighted } )
      b_upgrade.setOnMouseEntered(   new EH[ME] { def handle(e: ME) = b_upgrade.image  = img_upgradeHighlighted } )
      
      // Return to normal on mouse exit
      b_nextwave.setOnMouseExited(   new EH[ME] { def handle(e: ME) = b_nextwave.image = img_nextwave } )
      b_shop1.setOnMouseExited(      new EH[ME] { def handle(e: ME) = b_shop1.image    = img_shop    } )  
      b_shop2.setOnMouseExited(      new EH[ME] { def handle(e: ME) = b_shop2.image    = img_shop    } )  
      b_shop3.setOnMouseExited(      new EH[ME] { def handle(e: ME) = b_shop3.image    = img_shop    } )
      b_upgrade.setOnMouseExited(    new EH[ME] { def handle(e: ME) = b_upgrade.image  = img_upgrade } )
      
      // Extra highlight on mouse click
      b_nextwave.setOnMousePressed(  new EH[ME] { def handle(e: ME) = b_nextwave.image = img_nextwaveClicked } )
      b_shop1.setOnMousePressed(     new EH[ME] { def handle(e: ME) = b_shop1.image    = img_shopClicked } )
      b_shop2.setOnMousePressed(     new EH[ME] { def handle(e: ME) = b_shop2.image    = img_shopClicked } )
      b_shop3.setOnMousePressed(     new EH[ME] { def handle(e: ME) = b_shop3.image    = img_shopClicked } )
      b_upgrade.setOnMousePressed(   new EH[ME] { def handle(e: ME) = b_upgrade.image  = img_upgradeClicked } )
      
      // Return to normal highlight on mouse released
      b_nextwave.setOnMouseReleased( new EH[ME] { def handle(e: ME) = b_nextwave.image = img_nextwaveHighlighted } )
      b_shop1.setOnMouseReleased(    new EH[ME] { def handle(e: ME) = b_shop1.image    = img_shopHighlighted } )
      b_shop2.setOnMouseReleased(    new EH[ME] { def handle(e: ME) = b_shop2.image    = img_shopHighlighted } )
      b_shop3.setOnMouseReleased(    new EH[ME] { def handle(e: ME) = b_shop3.image    = img_shopHighlighted } )
      b_upgrade.setOnMouseReleased(  new EH[ME] { def handle(e: ME) = b_upgrade.image  = img_upgradeHighlighted } )
      
      
      
      // Arranging the layout
      val buttons = new Group()
      val stack   = new StackPane()
      buttons.children = List(
          b_lefttop, b_righttop,
          b_shop1, b_shop2, b_shop3,
          b_lock1, b_lock2, b_lock3,
          b_nextwave, b_upgrade)
      stack.children   = List(sideCanvas, mainCanvas, buttons, interactionCanvas, menuBar, titleCanvas)
      stack.setAlignment(Pos.TopLeft)
      root = stack

      
      
      // INPUT : ACTIVE GUI BUTTONS
      b_shop1.setOnMouseClicked(   new EH[ME] { def handle(e: ME) = { Actions.buyCannonTower(   currentGame) } } )
      b_shop2.setOnMouseClicked(   new EH[ME] { def handle(e: ME) = { Actions.buyBoomerangTower(currentGame) } } )
      b_shop3.setOnMouseClicked(   new EH[ME] { def handle(e: ME) = { Actions.buyHomingTower(   currentGame) } } )
      b_lock1.setOnMouseClicked(   new EH[ME] { def handle(e: ME) = { Audio.play("error.wav") } } )
      b_lock2.setOnMouseClicked(   new EH[ME] { def handle(e: ME) = { Audio.play("error.wav") } } )
      b_lock3.setOnMouseClicked(   new EH[ME] { def handle(e: ME) = { Audio.play("error.wav") } } )
      b_upgrade.setOnMouseClicked( new EH[ME] { def handle(e: ME) = {
        selectedTower    = Actions.upgradeTower(currentGame, selectedTower)
        b_upgrade.visible = selectedTower.isDefined && !selectedTower.get.upgrade.isEmpty
      }})
      b_nextwave.setOnMouseClicked(new EH[ME] { def handle(e: ME) = {
        Actions.loadNextWave(currentGame)
        b_lock1.visible = (new CannonTower1    (0, 0).unlock) > currentGame.wave.number
        b_lock2.visible = (new BoomerangTower1 (0, 0).unlock) > currentGame.wave.number
        b_lock3.visible = (new HomingTower1    (0, 0).unlock) > currentGame.wave.number
      }})
      
      
      // INPUT: MOUSE CLICKED ON SCREEN
      this.onMouseClicked = new EH[ME] { def handle(me: ME) = fullScreen = true }
 
     
      // INPUT: MOUSE MOVED ON SCREEN
      mainCanvas.onMouseMoved = new EH[ME] {
        def handle(me: ME) = {
          
          // Update mouse coordinates
          mouseX = me.getSceneX
          mouseY = me.getSceneY
          // Show and hide menubar
          menuBar.visible = me.getSceneY < 35
        }
      }
            
      
      // INPUT: CLICK ON MAIN CANVAS
      mainCanvas.onMouseClicked = new EH[ME] {
        def handle(me: ME): Unit = {
          
          // Mouse coordinates
          val (x, y) = (toGridX(me.getSceneX), toGridY(me.getSceneY))
          
          // Shop purchases
          if (currentGame.shop.active) Actions.purchaseTower(currentGame, x, y)  
          
          // Tower selections
          else {
            val selection     = Actions.selectTower(currentGame, x - 0.5, y - 0.5)
            selectedTower     = selection._1
            b_upgrade.x       = selection._2
            b_upgrade.y       = selection._3
            b_upgrade.visible = !(selection._2 == 0.0 && selection._3 == 0.0)
          }
        } 
      }
       
      
      // INPUT: KEY PRESSED
      this.onKeyPressed = new EH[KeyEvent] {
        def handle(ke: KeyEvent) = { ke.getCode() match {
     
            // Spacebar skips titlescreen
            case KeyCode.SPACE if (!Titlescreen.completed) => Actions.skipTitleScreen()
            
            // Shortcut to next wave
            case KeyCode.SPACE => {
              Actions.loadNextWave(currentGame)
              b_lock1.visible = (new CannonTower1    (0, 0).unlock) > currentGame.wave.number
              b_lock2.visible = (new BoomerangTower1 (0, 0).unlock) > currentGame.wave.number
              b_lock3.visible = (new HomingTower1    (0, 0).unlock) > currentGame.wave.number
            }
            
            // Shortcut to tower upgrade
            case KeyCode.ENTER => selectedTower = Actions.upgradeTower(currentGame, selectedTower)
            
            case _ =>
          }
        }
      }
      
      
      // INPUT: MENU BUTTONS
      gmNew.onAction        = (e: ActionEvent) => {
        currentGame = GameLoader("data/defaultdata.xml")
        Render.prerender(mainCanvas, sideCanvas, currentGame)
        b_upgrade.visible = false
        b_lock1.visible = false
        b_lock2.visible = true
        b_lock3.visible = true
        Audio.play("iosfx.wav")
      }
      gmLoad.onAction       = (e: ActionEvent) => { 
        currentGame = GameLoader("data/savedata.xml")
        Render.prerender(mainCanvas, sideCanvas, currentGame)
        b_upgrade.visible = false
        b_lock1.visible = false
        b_lock2.visible = true
        b_lock3.visible = true
        Audio.play("iosfx.wav")
      }
      gmSave.onAction       = (e: ActionEvent) => { 
        GameSaver.save(currentGame)
        Audio.play("iosfx.wav")
      }
      gmExit.onAction       = (e: ActionEvent) => { sys.exit(0) }
      dmShowFPS.onAction    = (e: ActionEvent) => { this.showFPS = !this.showFPS }
      dmMute.onAction       = (e: ActionEvent) => { Music.mute() }
      dmStartMusic.onAction = (e: ActionEvent) => { Music.startLoop() }
      dmStopMusic.onAction  = (e: ActionEvent) => { Music.stopLoop() }
      dmGodmode.onAction    = (e: ActionEvent) => { Actions.activateGodmode(currentGame) }
    }  
  }
}