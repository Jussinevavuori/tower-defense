package game

import scala.collection.mutable.Buffer
import scala.xml.XML
import scala.xml.Elem
import java.io.File

/* Waveloader has only one function, loadWave(), which takes a single
 * integer as a parameter and the initial path for creating the enemies
 * in the wave and loads that wave from the waves.xml file.
 * 
 * In error cases (invalid parameter integer or io exceptions etc.)
 * the function returns an empty wave with the number -1
 */


object WaveLoader {
  
  def apply(wave: Int, path: Path): Wave = this.loadWave(wave, path)
  
  def loadWave(wave: Int, path: Path): Wave = {
    
    var finalWave = new Wave(-1, Buffer[Enemy](), 0)
  
    try {
    
      // Loading the waves.xml file
      val xml: Elem = XML.loadFile(new File("data/waves.xml"))
      
      // Load list of waves
      val waves = (xml \\ "waves" \ "wave")
      
      // Check that given wave is valid
      if (wave >= waves.length || wave < 0) {
        throw new IllegalArgumentException(
            "Given wave does not exist: number too large")
      } else if (wave < 0) {
        throw new IllegalArgumentException(
            "Wave number has to be a non-negative integer")
      }
      
      // Load the data for the requested wave
      val wavedata = waves(wave)
      
      // Calculate starting parameters for all enemies
      val (x, y, target) = (path.pos.x, path.pos.y, path.next)
      
      // Create an empty buffer for the enemies
      val enemies = Buffer[Enemy]()
      
      // Load the enemy data as an array of (count, id) tuples
      val enemyData = (wavedata \\ "enemies" \ "enemy").map(enemy => {
        (enemy \@ "count").toInt -> (enemy \@ "id")
      }).toArray
      
      // Load the prize
      val prize = (wavedata \@ "prize").toInt
      
      // For all data points, add 'count' amount of times the enemy
      // specified in the 'id' to the enemies
      enemyData.foreach(data => {
        for (i <- 0 until data._1) {
          enemies += {
            data._2 match {
              case "n1" => new EnemyN1(x, y, target)
              case "n2" => new EnemyN2(x, y, target)
              case "n3" => new EnemyN3(x, y, target)
              case "n4" => new EnemyN4(x, y, target)
//              case "t1" => new EnemyT1(x, y, target)
//              case "t2" => new EnemyT2(x, y, target)
//              case "t3" => new EnemyT3(x, y, target)
//              case "t4" => new EnemyT4(x, y, target)
              case _ => throw new CorruptedWavedataException(
                s"Unrecognized enemy id in wavedata: '${data._2}'")
            }
          }
        }        
      })
      
      scala.util.Random.shuffle(enemies)
      finalWave = new Wave(wave, scala.util.Random.shuffle(enemies), prize)
      
    } catch {
      
      case i: IllegalArgumentException => {
        println(i.getMessage)
        i.printStackTrace()
      }
      case c: CorruptedWavedataException => {
        println(c.getMessage)
        c.printStackTrace()
      }
       
    }  
    
    // Return the constructed wave
    finalWave
    
  }
  
  val maxWave = {
    try {
      val xml = XML.loadFile(new File("data/waves.xml"))
      (xml \\ "waves" \ "wave").length - 1
    } catch {
      case e: Throwable => println("Error occured during calculating maxWave")
      -1
    }
  }
  
}


// Exception classes
class CorruptedWavedataException(msg: String) extends Exception(msg)






