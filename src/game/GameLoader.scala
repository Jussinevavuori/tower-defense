package game

import java.io._

import xml._
import scala.collection.mutable.Buffer

object GameLoader {
  
  
  
  // Load a game by calling the GameLoader object directly. 
  def apply(filepath: String): Game = this.loadGame(filepath)
  
  
  
  // Loads the current game saved in the gamedata.xml and returns it.
  def loadGame(filepath: String): Game = {
    
    try {
        
      // Loading the XML file
      val xml: Elem = XML.loadFile(new File(filepath))

      // Game properties
      val (rows, cols) = this.loadGameDimensions(xml)
      val player       = this.loadPlayer(xml)
      val wave         = this.loadWave(xml)
      val path         = this.loadPath(xml)
      val towers       = this.loadTowers(xml)
      
      new Game(rows, cols, path, wave, player, towers)
      
    } catch {
      case e: Throwable => {  // Catch all throwables
        println(s"Error loading $filepath")
        e.printStackTrace()
      }
      
      // In error cases return a broken temp game
      new Game(1, 1, new Path(0, 0, None))
    }
  }
  
  
  // Functions for loading gamedata from an xml and returning game objects
  
  // Returns the rows and cols of the game (the dimensions)
  private def loadGameDimensions(xml: Elem): (Int, Int) = {
    ((xml \\ "game" \ "rows").text.toInt, (xml \\ "game" \ "cols").text.toInt)
  }

  // Returns the player
  private def loadPlayer(xml: Elem): Player = {
    new Player(
      (xml \\ "game" \\ "player" \ "health").text.toInt,
      (xml \\ "game" \\ "player" \ "money" ).text.toInt
    )
  }
  
  // Returns the wave number
  private def loadWave(xml: Elem): Int = (xml \\ "game" \ "wave").text.toInt
  
  // Returns the fully created path
  private def loadPath(xml: Elem): Path = {
    
    // Loading coordinate tuples in reverse order
    val pairs = ((xml \\ "game" \\ "path") \ "segment").map(s => {
      ((s \@ "x").toInt, (s \@ "y").toInt)}).toList.reverse
    
    checkSavedata(!pairs.isEmpty, "No path found in savedata")
    
    // Linking the path chain
    var previous: Option[Path] = None
    for (i <- 0 until pairs.length) {
      val pair = (pairs(i)._1.toInt, pairs(i)._2.toInt)
      previous = Some(new Path(pair._1, pair._2, previous))
    }
    previous.get 
  }
  
  // Returns all the towers in a buffer
  private def loadTowers(xml: Elem): Buffer[Tower] = {
    ((xml \\ "game" \\ "towers") \ "tower").map(t => {
      val (x, y, id) = ((t \@ "x").toDouble, (t \@ "y").toDouble, (t \@ "id"))
      id match {
        case "c1" => new CannonTower1(x, y)
        case "c2" => new CannonTower2(x, y)
        case "c3" => new CannonTower3(x, y)
        case "b1" => new BoomerangTower1(x, y)
        case "b2" => new BoomerangTower2(x, y)
        case "h1" => new HomingTower1(x, y)
        case "h2" => new HomingTower2(x, y)
        case _ => throw new CorruptedSavedataException("Unrecognized tower id")
      }
    }).toBuffer
  }
  
  
  private def checkSavedata(condition: Boolean, msg: String = "") = {
    if (!condition) throw new CorruptedSavedataException(msg)
  }
}






// Exception classes
class CorruptedSavedataException(msg: String) extends Exception(msg)









