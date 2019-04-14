package game

import scala.collection.mutable.Buffer
import scala.util.Random.nextInt

/** A game object describes an instance of a tower defense game and holds all the
 *  necessary game elements and functions for updating the game state.
 *  
 *  A game is initialized with the dimensions of the game and a path. Alternatively
 *  an initial wave number (default 0) can be passed in, as well as a readily made
 *  player or a list of towers.
 */
class Game( val rows: Int, val cols: Int, val path: Path, initWave: Int = 0, 
            val player: Player = new Player(),
            var towers: Buffer[Tower] = Buffer[Tower](),
            var props: Buffer[Prop] = Buffer[Prop]()) {
  
  
  /** The enemies in the current game. */
  var enemies: Buffer[Enemy] = Buffer[Enemy]()

  /** The projectiles in the current game. */
  var projectiles = Buffer[Projectile]()

  /** The current wave in the game. */
  var wave: Wave = WaveLoader.loadWave(initWave, this.path)  
  
  /** The current game's towershop. */
  val shop = new TowerShop()
  
  /** The towers as they were at the start of each round for saving. */
  var saveTowers = this.towers
  
  /** The money as it was at the start of each round for saving. */
  var saveMoney  = this.player.money
  
  /** Returns true when the player dies and the game is over. */
  def gameover = this.player.dead
  
  /** Function to tupdate sorted towers. */
  def sortTowers() = this.towers = this.towers.sortBy(_.pos.y)

  /** Initially updating the towers. */
  sortTowers()
  
  /** Updates the gamestate each frame with the given elapsed time in seconds. */
  def update(elapsedTime: Double): Unit = {
    
    this.synchronized {
          
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
            this.player.damage(enemy.strength)
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
          tower.updateTarget(this.enemies.iterator)
          this.projectiles ++= tower.shoot(elapsedTime)
        }}
        
        // Update projectiles, move, try to hit all enemies and remove finished
        rem = 0
        val projIndexIterator = projectiles.zipWithIndex.iterator
        projIndexIterator.foreach{case (proj, i) => {
          proj.move(elapsedTime)
          proj.hit(this.enemies.iterator)
          if (proj.finished) {
            this.projectiles.remove(i - rem)
            rem += 1
          }
        }}
    
        // Spawn in the enemies each wave
        if (!this.wave.finished) {
          val spawn = this.wave.spawn(elapsedTime)
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
  }
  
  /** If current wave is completed and killed, and another wave exists, loads the next wave. */
  def loadNextWave(): Boolean = {
    val ready = this.wave.number < WaveLoader.maxWave && this.wave.finished && this.enemies.isEmpty
    if (ready) {
      this.wave = WaveLoader(this.wave.number + 1, this.path)
      this.saveMoney = this.player.money
      this.saveTowers = this.towers
    }
    return ready
  }
  
  /** For a given pair of coordinates, returns whether a tower can be placed in the spot. */
  def isValidSpot(x: Double, y: Double) = {
    x >= 0.0 && y >= 0.0 && x <= this.cols && y <= this.rows &&
    this.path.toArray().map(_.pos).forall(p => p.distance(Vec(x, y)) > 1.0)
  }
  
  /** The frames per update: while fast forwarding loop 3 times each frame, else loop once. */
  def framesPerUpdate = if (fastForward) 3 else 1
  
  /** Private variable to keep track of whether the game is being fast forwarded. */
  private var fastForward = false
  
  /** Function to toggle the fast forward to the given setting. */
  def toggleFastForward(setting: Boolean) = fastForward = setting

}



