package game

import scala.xml.XML
import scala.xml.Elem
import scala.xml.PrettyPrinter

/** The object gamesaver can save a given game to savedata.xml overwriting the previous saved game. */
object GameSaver extends App {
  
  /** Shortcut to GameSaver save function. */
  def apply(game: Game) = this.save(game)
  
  
  /** Function to save a given game to savedata.xml. */
  def save(game: Game) = {
    
    // Create xml elements for all the gamedata
    val cols: Elem = <cols>{game.cols}</cols>
    val rows: Elem = <rows>{game.rows}</rows>
    val wave: Elem = <wave>{game.wave.number}</wave>
    val player: Elem =
      <player><health>{game.player.health}</health><money>{game.saveMoney}</money></player>
    val path: Elem =
      <path>{game.path.toArray().map({p =>
        this.createXMLElement("segment", Array("x" -> p.pos.x.toInt.toString(), "y" -> p.pos.y.toInt.toString()))
      })}</path>
    val towers: Elem =
      <towers>{game.saveTowers.map({t =>
        this.createXMLElement("tower", Array(
            "x" -> t.pos.x.toString(), "y" -> t.pos.y.toString(),
            "id" -> t.typeid))
      })}</towers>
    
    // Combine saved data into a single game element
    val savedata: Elem = 
      <game>{cols}{rows}{wave}{player}{towers}{path}</game>
    
    // Saving to file
    try {
      XML.save("data/savedata.xml", savedata, "UTF-8", true, null)
    } catch {
      case e: Throwable => println("Something went wrong when trying to save savedata.xml to file")
    }
  }
  
  /** Creates more complicated XML nodes with a list of attributes. */
  def createXMLElement(name: String, attributes: Array[(String, String)]): Elem = {
    XML.loadString(s"<${name} ${attributes.map(a=>s"${a._1}=${"\""+a._2+"\""}").mkString(" ")}/>")
  }
  
}



