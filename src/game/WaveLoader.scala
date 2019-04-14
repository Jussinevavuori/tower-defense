package game

import scala.xml.XML
import scala.xml.Elem
import java.io.File
import scala.collection.mutable.Queue

/** WaveLoader has access to waves.xml and can load and create waves using that data. */
object WaveLoader {
  
  /** Shortcut to loading a given wave. */
  def apply(wave: Int, path: Path): Wave = this.loadWave(wave, path)
  
  /** Loads a given wave (creating the enemies at the start of the path.) */
  def loadWave(wave: Int, path: Path): Wave = {
    
    // Initialize the wave and modify it
    var finalWave = new Wave(-1, Queue[Enemy](), 0)
  
    try {
    
      // Loading the waves.xml file.
      val xml: Elem = XML.loadFile(new File("data/waves.xml"))
      
      // Load list of waves.
      val waves = (xml \\ "waves" \ "wave")
      
      // Check that given wave is valid.
      if (wave >= waves.length || wave < 0) {
        throw new IllegalArgumentException("Given wave does not exist: number too large")
      } else if (wave < 0) {
        throw new IllegalArgumentException("Wave number has to be a non-negative integer")
      }
      
      // Load the data for the requested wave.
      val wavedata = waves(wave)
      
      // Calculate starting parameters for all enemies.
      val (x, y, target) = (path.pos.x, path.pos.y, path.next)
      
      // Create an empty buffer for the enemies.
      var enemies = Queue[Enemy]()
      
      // Load the enemy data as an array of (count, id) tuples.
      val enemyData = (wavedata \\ "enemies" \ "enemy")
        .map(enemy => {(enemy \@ "count").trim().toInt -> (enemy \@ "id")}).toArray
      
      // Load the prize
      val prize = (wavedata \@ "prize").toInt
      
      // For all data points, add the enemy to the queue 'count' times.
      enemyData.foreach(data => {
        for (i <- 0 until data._1) {
          enemies.enqueue {
            data._2 match {
              case "n1" => new EnemyN1(x, y, target)
              case "n2" => new EnemyN2(x, y, target)
              case "n3" => new EnemyN3(x, y, target)
              case "n4" => new EnemyN4(x, y, target)
              case _ => throw new CorruptedWavedataException(s"Unrecognized enemy id in wavedata: '${data._2}'")
            }
          }
        }        
      })
      
      // Shuffle the enemies for randomness.
      scala.util.Random.shuffle(enemies)
      
      // Modifying the wave.
      finalWave = new Wave(wave, scala.util.Random.shuffle(enemies), prize)
      
    } catch {
      
      case i: IllegalArgumentException   => { println(i.getMessage); i.printStackTrace() }
      case c: CorruptedWavedataException => { println(c.getMessage); c.printStackTrace() }
       
    }  
    
    // Return the constructed wave
    finalWave
  }
  
  /** Initialize this object by counting the amount of waves from the wavedata. */
  val maxWave = {
    try {
      (XML.loadFile(new File("data/waves.xml")) \\ "waves" \ "wave").length - 1
    } catch {
      case e: Throwable => println("Error occured during calculating maxWave")
      -1
    }
  }
}

/** Exception class for wavedata. */
class CorruptedWavedataException(msg: String) extends Exception(msg)






