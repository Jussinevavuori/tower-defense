package gui

/** Keeps track of the current time and elapsed time. */
object Time {

  /** The previously recorded time. */
  private var previousTime: Long = -1
  
  /** Function to update the elapsed time and previous time. */
  def updateElapsedTime(now: Long) = {
    if (previousTime < 0) previousTime = now
    elapsedTime = (now - previousTime) / 1000000000.0 
    previousTime = now
  }
  
  /** The elapsed time between last two function calls. */
  var elapsedTime = 0.0
}