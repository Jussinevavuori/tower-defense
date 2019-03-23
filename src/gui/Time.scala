package gui

/*
 * Time knows the previous recorded time and the elapsed time between frames
 * as long as updateElapsedTime is continuously called from within animation
 * loops.
 */

object Time {

  private var previousTime: Long = -1
  def updateElapsedTime(now: Long) = {
    if (previousTime < 0) previousTime = now
    elapsedTime = (now - previousTime) / 1000000000.0 
    previousTime = now
  }
  var elapsedTime = 0.0
}