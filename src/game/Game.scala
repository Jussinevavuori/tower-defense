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
      val enemy = this.enemies(i)  // Current enemy
      if (enemy.dead) { // If enemy is dead
        this.player.reward(enemy.reward)  // Reward player with correct amount of money
        for (spawn <- enemy.death())  // Add spawned enemies from the dead enemy to the game
          this.enemies.append(spawn)   
        gui.Effects.addMoneyEffect(enemy)
        this.enemies.remove(i) // Remove enemy
      } else if (enemy.advance(elapsedTime)) {  // Advance the enemy.
        this.player.damage(1)  // If enemy reaches the goal damage the player
        this.enemies.remove(i)  // and remove the enemy
      }
    }
    
   // Update each tower's target and shoot and upgrade
    for (i <- 0 until this.towers.length reverse) {
      if (towers(i).upgraded) {
        this.towers.remove(i)
      }
      val tower = this.towers(i)
      tower.updateTarget(this.enemies)
      this.projectiles ++= tower.shoot(elapsedTime)
    }
    
    // Update and remove projectiles
    for (i <- this.projectiles.indices.reverse) {
      val p = this.projectiles(i)
      p.move()
      p.hit(this.enemies)
      if (p.finished) {
        this.projectiles.remove(i)
      }
    }

    // Spawn in the enemies when necessary
    if (!this.wave.finished) {
      val spawn = this.wave.spawn()
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



