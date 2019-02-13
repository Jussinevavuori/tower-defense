package gui

import game._
import javafx.event.EventHandler
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.{MouseEvent => ME}
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
import scalafx.scene.layout.Background
import scalafx.scene.layout.BackgroundFill
import scalafx.scene.layout.BorderPane
import scalafx.scene.layout.StackPane
import scalafx.scene.paint.Color
import scalafx.scene.control.Button
import scalafx.scene.shape.Rectangle
import scalafx.scene.layout.Pane
import scalafx.scene.Group

object Main extends JFXApp {
  
  
  // The current game running in the GUI
  var currentGame = GameLoader("data/defaultdata.xml")
  
  
  stage = new JFXApp.PrimaryStage {
    
    
    title = "Tower Defense"
    fullScreen = true
    resizable = false

    
    scene = new Scene {
      
      
      
      // Canvas for the game and sidebar and prerendering
      val mainCanvas  = new Canvas(1920, 840)
      val sideCanvas  = new Canvas(1920, 1080)
      val titleCanvas = new Canvas(1920, 1080)
      mainCanvas.requestFocus()
      Render.prerender(mainCanvas, sideCanvas, currentGame)
  
      
      
      // Private variables for showing the GUI and game correctly
      private var showFPS = false
      private var mouseX = 0.0
      private var mouseY = 0.0
      private var selectedTower: Option[Tower] = None
      private var previousTime: Long = -1
      
      
      
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
        
        val elapsedTime: Double = (now - previousTime) / 1000000000.0 
        previousTime = now  // Calculate the elapsed time in seconds
        currentGame.update(elapsedTime)  // Run game
        
        Animate.advance()
        Effects.advance()
        Render.renderGame(currentGame, this.mainCanvas, selectedTower)
        Render.renderSide(currentGame, this.sideCanvas)
        if (currentGame.shop.active) 
          Render.renderActiveTower(this.mainCanvas, currentGame, mouseX, mouseY)
        if (this.showFPS)
          Render.fps(elapsedTime, mainCanvas) // Show FPS
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
      val b_lefttop  = Rectangle(0, 0, 0, 0) 
      val b_righttop = Rectangle(1920, 1080, 0, 0)
      val b_nextwave = Rectangle(1729, 972, 120, 83)
      val b_shop1    = Rectangle(386, 1021, 144, 32)
      val b_shop2    = Rectangle(586, 1021, 144, 32)
      val b_shop3    = Rectangle(786, 1021, 144, 32)
      b_shop1.arcHeight = 25
      b_shop1.arcWidth  = 25
      b_shop2.arcHeight = 25
      b_shop2.arcWidth  = 25
      b_shop3.arcHeight = 25
      b_shop3.arcWidth  = 25
      b_lefttop.fill  = Color(1.0, 1.0, 1.0, 0.0)
      b_righttop.fill = Color(1.0, 1.0, 1.0, 0.0)
      b_nextwave.fill = Color(1.0, 1.0, 1.0, 0.0)  
      b_shop1.fill    = Color(1.0, 1.0, 1.0, 0.0)
      b_shop2.fill    = Color(1.0, 1.0, 1.0, 0.0)
      b_shop3.fill    = Color(1.0, 1.0, 1.0, 0.0)
      b_nextwave.setOnMouseEntered(  new EventHandler[ME] { def handle(e: ME) = b_nextwave.fill = Color(1.0, 1.0, 1.0, 0.2) } )
      b_shop1.setOnMouseEntered(     new EventHandler[ME] { def handle(e: ME) = b_shop1.fill    = Color(1.0, 1.0, 1.0, 0.2) } )
      b_shop2.setOnMouseEntered(     new EventHandler[ME] { def handle(e: ME) = b_shop2.fill    = Color(1.0, 1.0, 1.0, 0.2) } )
      b_shop3.setOnMouseEntered(     new EventHandler[ME] { def handle(e: ME) = b_shop3.fill    = Color(1.0, 1.0, 1.0, 0.2) } )
      b_nextwave.setOnMouseExited(   new EventHandler[ME] { def handle(e: ME) = b_nextwave.fill = Color(1.0, 1.0, 1.0, 0.0) } )
      b_shop1.setOnMouseExited(      new EventHandler[ME] { def handle(e: ME) = b_shop1.fill    = Color(1.0, 1.0, 1.0, 0.0) } )  
      b_shop2.setOnMouseExited(      new EventHandler[ME] { def handle(e: ME) = b_shop2.fill    = Color(1.0, 1.0, 1.0, 0.0) } )  
      b_shop3.setOnMouseExited(      new EventHandler[ME] { def handle(e: ME) = b_shop3.fill    = Color(1.0, 1.0, 1.0, 0.0) } )
      b_nextwave.setOnMousePressed(  new EventHandler[ME] { def handle(e: ME) = b_nextwave.fill = Color(1.0, 1.0, 1.0, 0.5) } )
      b_shop1.setOnMousePressed(     new EventHandler[ME] { def handle(e: ME) = b_shop1.fill    = Color(1.0, 1.0, 1.0, 0.5) } )
      b_shop2.setOnMousePressed(     new EventHandler[ME] { def handle(e: ME) = b_shop2.fill    = Color(1.0, 1.0, 1.0, 0.5) } )
      b_shop3.setOnMousePressed(     new EventHandler[ME] { def handle(e: ME) = b_shop3.fill    = Color(1.0, 1.0, 1.0, 0.5) } )
      b_nextwave.setOnMouseReleased( new EventHandler[ME] { def handle(e: ME) = b_nextwave.fill = Color(1.0, 1.0, 1.0, 0.2) } )
      b_shop1.setOnMouseReleased(    new EventHandler[ME] { def handle(e: ME) = b_shop1.fill    = Color(1.0, 1.0, 1.0, 0.2) } )
      b_shop2.setOnMouseReleased(    new EventHandler[ME] { def handle(e: ME) = b_shop2.fill    = Color(1.0, 1.0, 1.0, 0.2) } )
      b_shop3.setOnMouseReleased(    new EventHandler[ME] { def handle(e: ME) = b_shop3.fill    = Color(1.0, 1.0, 1.0, 0.2) } )
      
      
      
      // Arranging the layout
      val buttons = new Group()
      val stack   = new StackPane()
      buttons.children = List(b_lefttop, b_righttop, b_nextwave, b_shop1, b_shop2, b_shop3)
      stack.children   = List(sideCanvas, mainCanvas, menuBar, buttons, titleCanvas)
      stack.setAlignment(Pos.TopLeft)
      root = stack

      
      
      // INPUT : ACTIVE GUI BUTTONS
      b_nextwave.setOnMouseClicked(new EventHandler[ME] { def handle(e: ME) = { Buttonactions.loadNextWave(  currentGame) } } )
      b_shop1.setOnMouseClicked(   new EventHandler[ME] { def handle(e: ME) = { Buttonactions.buyCannonTower(currentGame) } } )
      b_shop2.setOnMouseClicked(   new EventHandler[ME] { def handle(e: ME) = { Buttonactions.buyRapidTower( currentGame) } } )
      b_shop3.setOnMouseClicked(   new EventHandler[ME] { def handle(e: ME) = { Buttonactions.buyHomingTower(currentGame) } } )
      
      
      // INPUT: MOUSE CLICKED ON SCREEN
      this.onMouseClicked = new EventHandler[ME] { def handle(me: ME) = fullScreen = true }
 
     
      // INPUT: MOUSE MOVED ON SCREEN
      mainCanvas.onMouseMoved = new EventHandler[ME] {
        def handle(me: ME) = {
          mouseX = me.getSceneX
          mouseY = me.getSceneY
          menuBar.visible = me.getSceneY < 35
        }
      }
            
      
      // INPUT: CLICK ON MAIN CANVAS
      mainCanvas.onMouseClicked = new EventHandler[ME] {
        def handle(me: ME): Unit = {
          
          if (currentGame.shop.active) {  // Shop purchases
            if (currentGame.shop.purchase(currentGame, mouseX / Render.gridW, mouseY / Render.gridH)) {
              Audio.play("coincluster.wav")
              Audio.play("impact.wav")
            } else Audio.play("error.wav")
          }
          
          else {  // Tower selections
            val mouseVec = Vec(mouseX / Render.gridW - 0.5, mouseY / Render.gridH - 0.5)
            for (t <- currentGame.towers) {
              if (mouseVec.distance(t.pos) < 0.6) {
                selectedTower = Some(t)
                return
              }
            }
            selectedTower = None
          }
        }
      }
      
      
      // INPUT: KEY PRESSED
      this.onKeyPressed = new EventHandler[KeyEvent] {
        def handle(ke: KeyEvent) = { ke.getCode() match {
     
            // Spacebar skips titlescreen
            case KeyCode.SPACE if (!Titlescreen.completed) => {
              Titlescreen.skip()
            }
            
            // Enter upgrades selected tower
            case KeyCode.ENTER if (selectedTower.isDefined) => {
              selectedTower = currentGame.shop.upgrade(currentGame, selectedTower.get)
              if (selectedTower.isDefined) Audio.play("fanfare.wav") else Audio.play("error.wav")
            }
            case _ =>
          }
        }
      }
      
      
      // INPUT: MENU BUTTONS
      gmNew.onAction        = (e: ActionEvent) => {
        currentGame = GameLoader("data/defaultdata.xml")
        Render.prerender(mainCanvas, sideCanvas, currentGame)
        Audio.play("iosfx.wav")
      }
      gmLoad.onAction       = (e: ActionEvent) => {
        currentGame = GameLoader("data/savedata.xml")
        Render.prerender(mainCanvas, sideCanvas, currentGame)
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
      dmGodmode.onAction    = (e: ActionEvent) => {
        currentGame.player.reward(10000)
        currentGame.player.heal(1000)
        Audio.play("fanfare.wav")
      }
    }  
  }
}