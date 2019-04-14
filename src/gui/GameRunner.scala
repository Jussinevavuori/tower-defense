package gui


/** New thread to run the game concurrently with the GUI. */

object GameRunner extends Runnable {
  
  /** Records the last recorded time for calculating the elapsed time each frame. */
  var latestTime: Long = 0L
  
  /** Function to run the game. */
  def run() {
    
    /** Run the game as long as this thread is running. */
    while (true) {
      
      /** Calculate elapsed time and update latest time. */
      val currentTime = System.nanoTime()
      val elapsedTime = (currentTime - latestTime) / 1e9
      latestTime = currentTime
      
      /** Perform updating of game. */
      Main.currentGame.update(elapsedTime)
    }
    
  }
  
}