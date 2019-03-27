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
    for (loop <- 0 until framesPerUpdate) {
                
      var rem = 0  // Keeps track of how many items per category removed
      
      // Update enemies
      val enemyIndexIterator = enemies.zipWithIndex.iterator
      enemyIndexIterator.foreach{case (enemy, i) => {
        
        // Remove dead enemies, reward player, play effect, spawn enemies
        if (enemy.dead) {                        
          this.player.reward(enemy.reward)                 
          gui.Effects.addMoneyEffect(enemy)      
          this.enemies.remove(i - rem)
          rem += 1
          for (spawn <- enemy.death()) {         
            this.enemies.append(spawn)           
          }          
        }
        
        // Move enemies, upon reaching end damage player and remove enemy
        else if (enemy.advance(elapsedTime)) {
          this.player.damage(1)               
          gui.Audio.play("damage2.wav")       
          this.enemies.remove(i)              
        }
      }}
    
      // Update tower targets, shoot, remove upgraded
      rem = 0
      val towerIndexIterator = towers.zipWithIndex.iterator
      towerIndexIterator.foreach{case (tower, i) => {
        if (tower.upgraded) {
          this.towers.remove(i - rem)
          rem += 1
        }
        tower.updateTarget(this.enemies.reverse.iterator)              
        this.projectiles ++= tower.shoot(elapsedTime) 
      }}
      
      // Update projectiles, move, try to hit all enemies and remove finished
      rem = 0
      val projIndexIterator = projectiles.zipWithIndex.iterator
      projIndexIterator.foreach{case (proj, i) => {
        proj.move()           
        proj.hit(this.enemies.iterator)
        if (proj.finished) {  
          this.projectiles.remove(i - rem)
          rem += 1
        }
      }}
  
      // Spawn in the enemies each wave
      if (!this.wave.finished) {      
        val spawn = this.wave.spawn() 
        if (spawn.isDefined)          
          this.enemies += spawn.get
      }
      
      // After killing all enemies, grant prize
      if (this.enemies.isEmpty && this.wave.finished) {
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
    x >= 0.0 && y >= 0.0 && x <= this.cols && y <= this.rows &&
    this.path.toArray().map(_.pos).forall(p => p.distance(Vec(x, y)) > 1.0)
  }
  
  /* Toggles fast forward (game loop runs more loops per frame)
   */
  
  def framesPerUpdate = if (fastForward) 3 else 1
  var fastForward = false
  def toggleFastForward(setting: Boolean) = {
    fastForward = setting
  }


}



