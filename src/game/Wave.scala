package game

import scala.collection.mutable.Buffer


/* A wave is a container for enemies. The game holds a single wave at
 * a time. A wave holds a list of enemies, which are to be spawned into
 * the game on that wave.
 * 
 * The wave has a spawn method, which returns the enemies one by one every
 * 'Wave.frequency' frames, so that the enemies can be spawned into the game
 * at a steady rate instead of spawning them in all at once.
 * 
 */

class Wave(val number: Int, val enemies: Buffer[Enemy]) {
  
  private var timer = 0        // How many frames have passed
  private val frequency = 30   // Frequency of enemy spawns in frames
  private var index = 0        // Index of next enemy to spawn
  
  // True when all enemies have been spawned
  def finished = this.index >= this.enemies.length

  
  /* Function that each 'frequency' frames returns the next
   * enemy in the wave wrapped in an option. Every other frame
   * returns a None.
   */
  
  def spawn(): Option[Enemy] = {
    
    this.timer += 1
    
    if (!this.finished && (timer % frequency == 0)) {
      this.index += 1
      Some(this.enemies(this.index - 1))
    } else {
      None
    }
  }
  
  override def toString() = s"Wave ${this.number} (${this.enemies.length} enemies)"
  
  
}