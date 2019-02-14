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

  
    
  /* Function that is called every frame and updates the game
   * Takes the elapsed time between frames as an argument for
   * scaling the movement
   */
  def update(elapsedTime: Double): Unit = {
        
    // Update the enemies
    for (i <- this.enemies.indices.reverse) {
      
      // Find the enemy in the buffer
      val enemy = this.enemies(i)
      
      // Upon enemy death
      if (enemy.dead) {
        
        // Reward the player with the money from the enemy
        this.player.reward(enemy.reward)
        
        // Add all the enemies that spawn upon the enemy's death to the game
        for (spawn <- enemy.death()) {
          this.enemies.append(spawn)   
        }
      
        gui.Effects.addMoneyEffect(enemy)
        
        // Finally remove the enemy from the game
        this.enemies.remove(i)
        
      // For alive enemies advance them and if they get to the end
      } else if (enemy.advance(elapsedTime)) {
        
        // Damage the player by one
        this.player.damage(1)
        gui.Audio.play("damage.wav")
        
        // Remove the enemy from the game
        this.enemies.remove(i)
      }
    }
    
    // Update towers
    for (i <- this.towers.indices.reverse) {
      
      // Find the tower in the buffer
      val tower = this.towers(i)
      
      // Remove upgraded towers
      if (tower.upgraded) {
        this.towers.remove(i)
      }
      
      // Update the towers target
      tower.updateTarget(this.enemies)
      
      // Add the shot projectiles to the game
      this.projectiles ++= tower.shoot(elapsedTime)
    }
    
    // Update and remove projectiles
    for (i <- this.projectiles.indices.reverse) {
      
      // Find the projectile in the buffer
      val projectile = this.projectiles(i)
      
      // Move the projectile
      projectile.move()
      
      // Try to hit all enemies
      projectile.hit(this.enemies)
      
      // If projectile finishes, remove it
      if (projectile.finished) {
        this.projectiles.remove(i)
      }
    }

    // Spawn in the enemies when necessary
    if (!this.wave.finished) {
      
      // Try to get a spawn from the wave
      val spawn = this.wave.spawn()
      
      // If an enemy spawned, add it to the game
      if (spawn.isDefined) {
        this.enemies += spawn.get
      }
    }
    
  }
  
  /* Loads the next wave only if current wave is finished spawning enemies, all the enemies
   * have been already killed and there is another wave to load in.
   */
  
  def loadNextWave(): Boolean = {
    if (this.wave.number < WaveLoader.maxWave && this.wave.finished && this.enemies.isEmpty) {
      this.wave = WaveLoader(this.wave.number + 1, this.path)
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


}



