package gui


/** New thread to run the game concurrently with the GUI. */

object GameRunner extends Runnable {
  
  /** Records the last recorded time for calculating the elapsed time each frame. */
  var latestTime: Long = 0L
  
  /** Flag for when the game is running. */
  var running = true

  /** Function to terminate execution. */
  def terminate() = this.running = false
  
  /** Function to run the game. */
  def run() {
    
    /** Initialize running as true. */
    this.running = true
    
    /** Run the game as long as the InGameScene tells so. */
    while (this.running) {
      
      /** Calculate elapsed time and update latest time. */
      val currentTime = System.nanoTime()
      val elapsedTime = (currentTime - latestTime) / 1e9
      latestTime = currentTime
      
      /** Perform updating of game. */
      Main.currentGame.update(elapsedTime)
    }
    
  }
  
}