package gui

import game._
import scalafx.application.JFXApp


/** The main app and window. */
object Main extends JFXApp {
  
  /** The current game running in the GUI. */
  var currentGame = GameLoader.loadNewGame()
  
  /** Function to load a new game. */
  def loadGame(g: Game) = {
    currentGame = g
    Render.prerender(this.currentGame)
  }
  
  /** Function to return if game is over. */
  def gameover = currentGame.gameover
  
  /** The main stage. */
  stage = new JFXApp.PrimaryStage {
    
    /** Window title. */
    title = "Tower Defense"
    /** Fullscreen false by default. */
    fullScreen = false
    /** Resizable. */
    resizable = true
    /** Setting the default width. */
    width = 1280
    /** Setting the default height. */
    height = 720
    
    /** Initializing with the default scene in program status. */
    scene = ProgramStatus.scene
    /** Starting the animation in the default scene. */
    ProgramStatus.start()
  }
  
  
  /** Function to change status of main using the programstatus. */
  def changeStatus(s: Int) = {
    ProgramStatus.stop()
    ProgramStatus.setStatus(s)
    this.stage.scene = ProgramStatus.scene
    ProgramStatus.start()
  }
}


















