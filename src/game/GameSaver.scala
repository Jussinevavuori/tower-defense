package game

import scala.xml.XML
import scala.xml.Elem
import scala.xml.PrettyPrinter

/* The GameSaver saves the given game to savedata.xml,
 * from where it can be loaded the next time the game
 * is booted.
 */

object GameSaver extends App {
  
  def apply(game: Game) = this.save(game)
  
  
  /* Saves the game.
   */
  
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
    
    val savedata: Elem = 
      <game>{cols}{rows}{wave}{player}{towers}{path}</game>
    
      
    // Prettyprinted formatting of savedata as debug message
    val prettyPrinter = new PrettyPrinter(80, 4)
    println(prettyPrinter.format(savedata))
    
    // Saving to file
    try {
      XML.save("data/savedata.xml", savedata, "UTF-8", true, null)
    } catch {
      case e: Throwable => println("Something went wrong when trying to save savedata.xml to file")
    }
  }
  
  // Creates XML nodes with only attributes
  def createXMLElement(name: String, attributes: Array[(String, String)]): Elem = {
    XML.loadString(s"<${name} ${attributes.map(a=>s"${a._1}=${"\""+a._2+"\""}").mkString(" ")}/>")
  }
  
}



