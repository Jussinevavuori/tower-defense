package gui

import game._
import scalafx.application.JFXApp
import javafx.event.{ EventHandler => EH }
import javafx.stage.{ WindowEvent => WE }


/** The main app and window. */
object Main extends JFXApp {
  
  /** The current game running in the GUI. */
  var currentGame = GameLoader.loadNewGame()
  
  /** Game runner thread for updating the game concurrently. */
  var gameThread = new Thread(GameRunner)
  
  /** Function to load a new game. */
  def loadGame(g: Game) = {
    currentGame = g
    Render.prerender(this.currentGame)
  }
  
  /** Function to return if game is over. */
  def gameover = currentGame.gameover
  
  /** The main stage. */
  stage = new JFXApp.PrimaryStage {
    
    /** Default properties. */
    title = "Tower Defense"
    resizable = true
    fullScreen = false
    width = 1280
    height = 720
    
    /** Initializing with the default scene in program status. */
    scene = ProgramStatus.scene
    
    /** Starting the animation in the default scene. */
    ProgramStatus.start()
    
    /** On shutdown terminate concurrent thread and save game. */
    this.onCloseRequest = new EH[WE] {
      def handle(w: WE) = {
        GameRunner.terminate()
        if (ProgramStatus() == ProgramStatus.InGame)
          if (Main.currentGame.wave.number > 1)
            Actions.save
      }
    }
  }
  
  
  /** Function to change status of main using the programstatus. */
  def changeStatus(s: Int) = {
    ProgramStatus.stop()
    ProgramStatus.setStatus(s)
    this.stage.scene = ProgramStatus.scene
    ProgramStatus.start()
  }
}


















