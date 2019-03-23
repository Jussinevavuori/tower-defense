package gui

import game._
import scalafx.application.JFXApp


/* 
 * The main app, which creates the window using the correct scene from
 * Program status.
 */

object Main extends JFXApp {
  
  // The current game running in the GUI
  var currentGame = GameLoader("data/defaultdata.xml")
  def load(s: String) = this.currentGame = GameLoader(s)
  def gameover = currentGame.gameover
  
  
  // The stage itself
  stage = new JFXApp.PrimaryStage {
    
    title = "Tower Defense"
    fullScreen = false
    resizable = true
    width = 1280
    height = 720
    
    scene = ProgramStatus.scene
    ProgramStatus.start()
  }
  
  
  // Function to change status and scene and handle animations
  def changeStatus(s: Int) = {
    ProgramStatus.stop()
    ProgramStatus.setStatus(s)
    this.stage.scene = ProgramStatus.scene
    ProgramStatus.start()
  }
}


















