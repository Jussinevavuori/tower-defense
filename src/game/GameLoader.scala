package game

import java.io._

import xml._
import scala.collection.mutable.Buffer

object GameLoader extends App {
  
  this.loadGame("data/defaultdata.xml")
  
  /* Load a game by calling the GameLoader object directly. 
   */
  
  def apply(filepath: String): Game = this.loadGame(filepath)
  
  /* Loads the current game saved in the gamedata.xml and
   * returns it.
   */
  
  def loadGame(filepath: String): Game = {
    
    try {
        
      // Loading the XML file
      val xml: Elem = XML.loadFile(new File(filepath))
      
      
      // Game properties
      val (rows, cols) = this.loadGameDimensions(xml)
      val player = this.loadPlayer(xml)
      val wave = this.loadWave(xml)
      val path = this.loadPath(xml)
      val towers = this.loadTowers(xml)
      
      
      // Debug messages
      println(
          s"GameLoader.loadGame('$filepath')\n" +
          s"XML: $filepath succesfully loaded\n" +
          s"Created player: ${player.toString}\n" +
          s"Created towers: ${towers.mkString(", ")}" + 
          s"Starting from wave: ${wave.toString}\n" +
          s"Created path starting from: ${path.toString}\n" +
          s"Using scala version ${util.Properties.versionString} \n\n"
      )
      
      // Finally return the created game
      new Game(rows, cols, path, wave, player, towers)
      
    } catch {
      
      // Catch all throwables
      case e: Throwable => {
        println(s"Error loading $filepath")
        e.printStackTrace()
      }
      
      // In error cases returns a new, fresh 1x1 game with a singular
      // invisible path
      val tempPath = new Path(0, 0, None)
      new Game(1, 1, tempPath)
    }
  }

  
  
  
  
  // Functions for loading gamedata from an xml and returning game objects
  
  // Returns the rows and cols of the game (the dimensions)
  def loadGameDimensions(xml: Elem): (Int, Int) = {
    ((xml \\ "game" \ "rows").text.toInt, (xml \\ "game" \ "cols").text.toInt)
  }

  // Returns the player
  def loadPlayer(xml: Elem): Player = {
    new Player(
      (xml \\ "game" \\ "player" \ "health").text.toInt,
      (xml \\ "game" \\ "player" \ "money" ).text.toInt
    )
  }
  
  // Returns the wave number
  def loadWave(xml: Elem): Int = {
    (xml \\ "game" \ "wave").text.toInt
  }
  
  // Returns the fully created path
  def loadPath(xml: Elem): Path = {
    
    // Loading the path segment list from the xml
    val list = (xml \\ "game" \\ "path")
    
    // Loading tuples from the list in reverse order
    // so they can be chained easier
    val pairs = (list \ "segment").map(s => {
      ((s \@ "x").toInt, (s \@ "y").toInt)}).toList.reverse
    
    // If pairs is empty, the path is invalid and the file is corrupted
    if (pairs.isEmpty) {
      throw new CorruptedSavedataException("No path found in savedata")
    }
    
    // The previously created path segment, for the next path
    // segment to link to
    var previous: Option[Path] = None
    
    // Creating each path segment one by one to the correct coordinates
    // and linking it to the previous path segment
    for (i <- 0 until pairs.length) {
      
      val pair = (pairs(i)._1.toInt, pairs(i)._2.toInt)
      
      previous = Some(new Path(pair._1, pair._2, previous))
    }
    
    // Choosing the initial path to be passed to the game
    previous.get
      
  }
  
  // Returns all the towers in a buffer
  def loadTowers(xml: Elem): Buffer[Tower] = {
    
    // Loading the tower list from the xml
    val list = (xml \\ "game" \\ "towers")
    
    (list \ "tower").map(t => {
      val (x, y, id) = ((t \@ "x").toInt, (t \@ "y").toInt, (t \@ "id"))
      id match {
        case "basic" => new CannonTower1(x, y)
        case "laser" => new RapidTower1(x, y)
        case "homing" => new HomingTower1(x, y)
        case _ => throw new CorruptedSavedataException("Unrecognized tower id")
      }
    }).toBuffer
  }
  
}






// Exception classes
class CorruptedSavedataException(msg: String)    extends Exception(msg)









