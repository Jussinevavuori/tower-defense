package gui

import game._
import javafx.event.EventHandler
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
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

object Main extends JFXApp {
  
  var currentGame = GameLoader("data/defaultdata.xml")
  
  stage = new JFXApp.PrimaryStage {
    
    title = "Tower Defense"
    
    fullScreen = true
    
    scene = new Scene {
      
      // Canvas for the game and sidebar and prerendering
      val mainCanvas = new Canvas(1920, 840)
      val sideCanvas = new Canvas(1920, 1080)
      val titleCanvas = new Canvas(1920, 1080)
      mainCanvas.requestFocus()
      Render.prerender(mainCanvas, sideCanvas, currentGame)
      
      // The canvas cannot be resized
      resizable = false
  
      var showFPS = false
  
      var mouseX = 0.0
      var mouseY = 0.0
      
      // The previous time stamp for calculation of elapsed time
      var previousTime: Long = -1
      
      // Show title screen at startup
      
      val titleScreenTimer: AnimationTimer = AnimationTimer { now =>
        
        // Calculate the elapsed time in seconds and reset previous
        if (this.previousTime < 0) this.previousTime = now
        val elapsedTime: Double = (now - previousTime) / 1000000000.0 
        previousTime = now
        
        Titlescreen.advance(this.titleCanvas, elapsedTime)
        
        // When completed hide title canvas and start game
        titleCanvas.opacity = Titlescreen.opacity
        var started = false
        if (Titlescreen.fading && !started) {
          mainTimer.start()
          Music.startLoop()
        }
        if (Titlescreen.completed) {
          titleCanvas.visible = false
          titleScreenTimer.stop()
          mainTimer.start()
        }
      }
      titleScreenTimer.start()
      
      // Creating the animation timer
      val mainTimer = AnimationTimer { now =>
        
        // Calculate the elapsed time in seconds and reset previous
        val elapsedTime: Double = (now - previousTime) / 1000000000.0 
        previousTime = now
                
        // Each frame update game, render and animate
        currentGame.update(elapsedTime)
        Render.renderGame(currentGame, this.mainCanvas)
        Render.renderSide(currentGame, this.sideCanvas)
        Animate.advance()
        if (currentGame.shop.active) {
          Render.renderActiveTower(this.mainCanvas, currentGame, mouseX, mouseY)
        }
        
        // Render the FPS
        if (this.showFPS) Render.fps(elapsedTime, mainCanvas)
        
      }

      
      // Creating the menubar
      val menuBar   = new MenuBar
      val gameMenu  = new Menu("Game")
      val debugMenu = new Menu("Debug")
      menuBar.menus   = List(gameMenu, debugMenu)
      
      
      // Creating the buttons for the menus
      val gmNew        = new MenuItem("New game")
      val gmLoad       = new MenuItem("Load game")
      val gmExit       = new MenuItem("Exit")
      val dmShowFPS    = new MenuItem("Show FPS")
      val dmSave       = new MenuItem("Save")
      val dmGodmode    = new MenuItem("Godmode")
      val dmBuyBasic   = new MenuItem("Buy basic tower ($500)")
      val dmBuyLaser   = new MenuItem("Buy laser tower ($800)")
      val dmBuyHoming  = new MenuItem("Buy homing tower ($1000)")
      val dmMute       = new MenuItem("Mute music")
      val dmStartMusic = new MenuItem("Start music")
      val dmStopMusic  = new MenuItem("Stop music")
      
      // Assigning the menu items to the menus
      gameMenu.items  = List(
          gmNew,  new SeparatorMenuItem,
          gmLoad, new SeparatorMenuItem,
          gmExit)
      debugMenu.items = List(
          dmShowFPS,    new SeparatorMenuItem,
          dmSave,       new SeparatorMenuItem,
          dmGodmode,    new SeparatorMenuItem,
          dmBuyBasic,   new SeparatorMenuItem,
          dmBuyLaser,   new SeparatorMenuItem,
          dmBuyHoming,  new SeparatorMenuItem,
          dmMute,       new SeparatorMenuItem,
          dmStartMusic, new SeparatorMenuItem,
          dmStopMusic)
      
      
      // Assigning the menu button actions
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
      dmGodmode.onAction    = (e: ActionEvent) => {
        currentGame.player.reward(10000000)
        currentGame.player.heal(100000)
        Audio.play("fanfare.wav")
      }
      gmExit.onAction       = (e: ActionEvent) => {
        sys.exit(0)
      }
      dmShowFPS.onAction    = (e: ActionEvent) => {
        this.showFPS = !this.showFPS
      }
      dmSave.onAction       = (e: ActionEvent) => {
        GameSaver.save(currentGame)
        Audio.play("iosfx.wav")
      }
      dmBuyBasic.onAction   = (e: ActionEvent) => {
        currentGame.shop.choose("basic", currentGame)
        if (currentGame.shop.active) 
          Audio.play("coin.wav")
        else
          Audio.play("error.wav")
      }
      dmBuyLaser.onAction   = (e: ActionEvent) => {
        currentGame.shop.choose("laser", currentGame)
        if (currentGame.shop.active) 
          Audio.play("coin.wav")
        else
          Audio.play("error.wav")      
      }
      dmBuyHoming.onAction  = (e: ActionEvent) => {
        currentGame.shop.choose("homing", currentGame)
        if (currentGame.shop.active) 
          Audio.play("coin.wav")
        else
          Audio.play("error.wav")
      }
      dmMute.onAction       = (e: ActionEvent) => {
        Music.mute()
      }
      dmStartMusic.onAction = (e: ActionEvent) => {
        Music.startLoop()
      }
      dmStopMusic.onAction  = (e: ActionEvent) => {
        Music.stopLoop()
      }
      
      // Creating the panes and organizing them
      val stack = new StackPane()
      stack.setAlignment(Pos.TopCenter)
      stack.children = List(sideCanvas, mainCanvas, menuBar, titleCanvas)
      menuBar.visible = false
      root = stack

      
      // Handling input
      
      // Clicking returns to fullscreen
      this.onMouseClicked = new EventHandler[MouseEvent] {
        def handle(me: MouseEvent) = fullScreen = true
      }

      // MenuBar only visible when mouse is hovering above 
      mainCanvas.setOnMouseMoved(new EventHandler[MouseEvent] {
        def handle(me: MouseEvent) = {
          mouseX = me.getSceneX
          mouseY = me.getSceneY
          menuBar.visible = me.getSceneY < 40
        }
      })
      
      // Purchase on click
      mainCanvas.setOnMouseClicked(new EventHandler[MouseEvent] {
        def handle(me: MouseEvent) = {
          if (currentGame.shop.active) {
            currentGame.shop.purchase(currentGame, mouseX / Render.gridW, mouseY / Render.gridH)
            Audio.play("impact.wav")
          }
        }
      })
      
      // Key presses
      this.onKeyPressed = new EventHandler[KeyEvent] {
        def handle(ke: KeyEvent) = {
          ke.getCode() match {
            case KeyCode.SPACE => {
              currentGame.loadNextWave()
              Audio.play("newwave.wav")
            }
            case _ =>
          }
        }
      }
      
    }
  
  }
  
}