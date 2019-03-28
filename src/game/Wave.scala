package game

import scala.collection.mutable.Queue


/** A wave is a container for enemies. Waves are loaded using the waveloader object.
 *  Waves contain all the wave's enemies and return them one by one at chosen intervals
 *  for steady spawning of enemies.
 */
class Wave(val number: Int, val enemies: Queue[Enemy], private val prizeSum: Int) {
  
  /** The wave's timer, which tracks the amount of passed game loops. */
  private var timer = 0
  
  /** The frequency constant for how often an enemy should be spawned in. */
  private val frequency = 30
  
  /** Returns true when all the enemies in this wave have been spawned in. */
  def finished = this.enemies.isEmpty
  
  /** A flag to keep track of whether this wave's prize has been granted yet. */
  private var prizeGranted = false

  /** Function that returns the prize money but only once. */
  def prize: Int = {
    val sum = if (prizeGranted) 0 else this.prizeSum
    this.prizeGranted = true
    sum
  }
  
  /** Function that returns an enemy if any from the wave's enemies each frequency frames. */
  def spawn(): Option[Enemy] = {    
    this.timer += 1
    if (!this.finished && (timer % frequency == 0)) {
      Some(this.enemies.dequeue())
    } else None
  }
  
  /** Returns a textual description of this wave. */
  override def toString() = s"Wave ${this.number} (${this.enemies.length} enemies left)"
}