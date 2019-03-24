package game

import scala.collection.mutable.Buffer
import scalafx.scene.paint.Color

/* A game object depicts an instance of a game. A game can be 
 * constructed from giving it the correct arguments, which can
 * be created from a file using the GameLoader.
 * 
 * By default, unless otherwise specified i.e. by GameLoader
 * - Player is always a fresh default player
 * - Player starts off with 0 towers
 * - Player starts at wave 0
 * 
 */

class Game( val rows: Int, val cols: Int,  // The size of the gamefield
            val path: Path,                // A path must always be assigned
            initWave: Int = 0,              // By default wave zero
            val player: Player = new Player(),           // By default a new player
            val towers: Buffer[Tower] = Buffer[Tower]()  // By default no towers
          ) {
  
  
  // Each game contains enemies
  var enemies: Buffer[Enemy] = Buffer[Enemy]()
  
  // The current wave
  var wave: Wave = WaveLoader.loadWave(initWave, this.path)  
  
  // Each game has a towershop
  val shop = new TowerShop()
  
  // Buffer of projectiles
  var projectiles = Buffer[Projectile]()
  
  // Amount of money and projectiles at the start of the round for saving
  var saveTowers = this.towers
  var saveMoney  = this.player.money
  
  
  // True when game is over
  def gameover = this.player.dead

  
    
  /* Function that is called every frame and updates the game
   * Takes the elapsed time between frames as an argument for
   * scaling the movement
   */
  def update(elapsedTime: Double): Unit = {
        
    // Loop five times for fast forward
    for (loop <- 0 until {if (this.fastForward) 5 else 1}) {
                
      for (i <- this.enemies.indices.reverse) {  // Update the enemies
        
        val enemy = this.enemies(i)              // Find the enemy in the buffer
                                                 
        if (enemy.dead) {                        // Upon enemy death
                                                           
          this.player.reward(enemy.reward)       // Reward the player with the money from the enemy
          for (spawn <- enemy.death()) {         // Add all the enemies that spawn upon the enemy's death to the game
            this.enemies.append(spawn)           
          }                                      
          gui.Effects.addMoneyEffect(enemy)      // Play money effect
          this.enemies.remove(i)                 // Finally remove the enemy from the game

        }
        
        else if (enemy.advance(elapsedTime)) {   // Else, move and if enemy reaches end
          
          this.player.damage(1)                  // Damage the player by one
          gui.Audio.play("damage2.wav")           
          this.enemies.remove(i)                 // Remove the enemy from the game
        }
      }
      
      
      for (i <- this.towers.indices.reverse) {         // Update towers
        
        val tower = this.towers(i)                     // Find the tower in the buffer
        if (tower.upgraded)                            // Remove upgraded towers
          this.towers.remove(i)
        tower.updateTarget(this.enemies)               // Update the towers target
        this.projectiles ++= tower.shoot(elapsedTime)  // Add the shot projectiles to the game
      }
      
      
      for (i <- this.projectiles.indices.reverse) {  // Update and remove projectiles
        
        val projectile = this.projectiles(i)         // Find the projectile in the buffer       
        projectile.move()                            // Move the projectile
        projectile.hit(this.enemies)                 // Try to hit all enemies
        if (projectile.finished)                     // If projectile finishes, remove it
          this.projectiles.remove(i)
      }
  
      
      if (!this.wave.finished) {       // Spawn in the enemies each wave

        val spawn = this.wave.spawn()  // Try to get a spawn from the wave
        if (spawn.isDefined)           // If an enemy spawned, add it to the game
          this.enemies += spawn.get
      }
      
      if (this.enemies.isEmpty && this.wave.finished) {  // After killing all enemies grant prize

        val reward = this.wave.prize
        this.player.reward(reward)
        if (reward > 0) gui.Audio.play("fanfare.wav")
      }
    }
  }
  
  /* Loads the next wave only if current wave is finished spawning enemies, all the enemies
   * have been already killed and there is another wave to load in.
   */
  
  def loadNextWave(): Boolean = {
    if (this.wave.number < WaveLoader.maxWave && this.wave.finished && this.enemies.isEmpty) {
      this.wave = WaveLoader(this.wave.number + 1, this.path)
      this.saveMoney = this.player.money
      this.saveTowers = this.towers
      true
    } else {
      false
    }
  }
  
  
  /* Sees if a valid spot on the grid is valid, that is it is within the game bounds and 
   * not overlapping with the path too much
   */
  
  def isValidSpot(x: Double, y: Double) = {
    val v = Vec(x, y)
    x >= 0.0 && y >= 0.0 && x <= this.cols && y <= this.rows &&
    this.path.toArray().map(_.pos).forall(p => p.distance(v) > 1.0)
  }
  
  /* Toggles fast forward (game loop runs more loops per frame)
   */
  
  var fastForward = false
  def toggleFastForward(setting: Boolean) = {
    fastForward = setting
  }


}



