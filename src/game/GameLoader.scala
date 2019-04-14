package game

import java.io._
import xml._
import scala.collection.mutable.Buffer

/** The GameLoader object has useful functions for loading games from XML files. */
object GameLoader {
    
  /** Loads a new game and returns it. */
  def loadNewGame(): Game = this.loadGame("data/defaultdata.xml")
  
  /** Loads the previoulsy saved game and returns it. */
  def loadSavedGame(): Game = this.loadGame("data/savedata.xml")
  
  /** LoadGame attempts to create and return a game based on the given filepath. */
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
      val props        = this.loadProps(xml)
      
      // Returning the created and loaded game
      new Game(rows, cols, path, wave, player, towers, props)
      
    } catch {
      
      case e: Throwable => { println(s"Error loading $filepath"); e.printStackTrace() }
      
      new Game(1, 1, new Path(0, 0, None))  // In error cases return a broken temp game
    }
  }
  
  /** Loads a custom game from the saved custom levels. */
  def loadCustomGame(level: Int): Game = {
    
    try {
      
      // Selecting the chosen level from customdata
      val n = (XML.loadFile(new File("data/customdata.xml")) \\ "data" \ "game")
        .find(l => (l \ "num").text.toInt == level).get
      val xml = Elem(null, n.label, n.attributes, n.scope, false, n.child:_*)
      
      // Loading the game properties
      val (rows, cols) = this.loadGameDimensions(xml)
      val player       = this.loadPlayer(xml)
      val wave         = this.loadWave(xml)
      val path         = this.loadPath(xml)
      val towers       = this.loadTowers(xml)
      val props        = this.loadProps(xml)
            
      // Returning the created and loaded game
      new Game(rows, cols, path, wave, player, towers, props)
      
    } catch {
      
      case e: Throwable => { println(s"Error loading customdata.xml"); e.printStackTrace() }
      
      new Game(1, 1, new Path(0, 0, None))  // In error cases return a broken temp game
    }
  }
  
  
  /** Helper function to load the columns and rows in that order from a game data file. */
  private def loadGameDimensions(xml: Elem): (Int, Int) = {
    ((xml \\ "game" \ "rows").text.toInt, (xml \\ "game" \ "cols").text.toInt)
  }

  /** Helper function to load a player from a game data file. */
  private def loadPlayer(xml: Elem): Player = {
    new Player((xml \\ "game" \\ "player" \ "health").text.toInt,
      (xml \\ "game" \\ "player" \ "money" ).text.toInt)
  }
  
  /** Helper function to load the wave number from a game data file. */
  private def loadWave(xml: Elem): Int = (xml \\ "game" \ "wave").text.toInt
  
  /** Helper function to load and create a full path from a game data file. */
  private def loadPath(xml: Elem): Path = {
    
    // Loading coordinate tuples in reverse order
    val pairs = ((xml \\ "game" \\ "path") \ "segment").map(s => {
      ((s \@ "x").toInt, (s \@ "y").toInt)}).toList.reverse
    
    checkSavedata(!pairs.isEmpty, "No path found in savedata")
    
    // Linking the path chain
    var previous: Option[Path] = None
    for (i <- 0 until pairs.length) {
      previous = Some(new Path(pairs(i)._1.toInt, pairs(i)._2.toInt, previous))
    }
    previous.get 
  }
  
  /** Helper function to load all towers to a buffer from a game data file. */
  private def loadTowers(xml: Elem): Buffer[Tower] = {
    ((xml \\ "game" \\ "towers") \ "tower").map(t => {
      val (x, y, id) = ((t \@ "x").toDouble, (t \@ "y").toDouble, (t \@ "id"))
      id match {
        case "c1" => new CannonTower1(x, y)
        case "c2" => new CannonTower2(x, y)
        case "c3" => new CannonTower3(x, y)
        case "b1" => new BoomerTower1(x, y)
        case "b2" => new BoomerTower2(x, y)
        case "h1" => new HomingTower1(x, y)
        case "h2" => new HomingTower2(x, y)
        case _ => throw new CorruptedSavedataException("Unrecognized tower id")
      }
    }).toBuffer
  }
  
  /** Helper function to load all props to a buffer from a game data file. */
  private def loadProps(xml: Elem): Buffer[Prop] = {
    ((xml \\ "game" \\ "props") \ "prop").map(p => {
      new Prop((p \@ "x").toDouble, (p \@ "y").toDouble, (p \@ "id"))
    }).toBuffer
  }
  
  /** Helper function to throw a corrupted savedata error with a given message upon a condition failing. */
  private def checkSavedata(condition: Boolean, msg: String = "") = {
    if (!condition) throw new CorruptedSavedataException(msg)
  }
}

/** An exception class for savedata. */
class CorruptedSavedataException(msg: String) extends Exception(msg)









