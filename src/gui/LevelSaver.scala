package gui

import game._
import scala.xml._
import java.io.File

/** Level saver can take custom levels created in the level editor and save them to customdata.xml. */
object LevelSaver {
  
  /** Resets all custom levels. */
  def resetCustomLevels() = {
    try {
      XML.save("data/customdata.xml", <empty></empty>, "UTF-8", true, null)
    } catch {
      case e: Throwable => println("Something went wrong when trying to reset customdata.xml")
    }
  }
  
  /** Takes a path and saves it as a custom level. */
  def saveCustomLevel(p: Path) = {
    
    // Loading the current levels
    val levels: NodeSeq = try {
      XML.loadFile(new File("data/customdata.xml")) \ "game"
    } catch {
      case e: Throwable => { e.printStackTrace(); println("Error loading customdata.xml"); NodeSeq.Empty }
    }
    
    // Creating the new elements
    val path:   Elem = <path>{p.toArray()
      .map(p=>createXMLElement("segment",Array("x" ->p.pos.x.toInt.toString(),"y"->p.pos.y.toInt.toString())))}</path>
    val num:    Elem = <num>{(levels \ "num").length}</num>
    val cols:   Elem = <cols>{Main.currentGame.cols}</cols>
    val rows:   Elem = <rows>{Main.currentGame.rows}</rows>
    val wave:   Elem = <wave>{0}</wave>
    val player: Elem = <player><health>{100}</health><money>{500}</money></player>
    val towers: Elem = <towers></towers>
      
    // All the elements combined to a level
    val level:  Elem = <game>{num}{cols}{rows}{wave}{player}{path}</game>
    
    // Combining previous levels and this level
    val data:   Elem = <data>{levels}{level}</data>

    // Saving the levels
    try {
      XML.save("data/customdata.xml", data, "UTF-8", true, null)
    } catch {
      case e: Throwable => println("Something went wrong when trying to save customdata.xml to file")
    }
  }
  
  /** TODO Function to delete custom levels. */
  def deleteCustomLevel() = {
    
  }
  
  /** Function that loads list of all levels in customadata. */
  def loadLevelList() = {
    try {
      XML.loadFile(new File("data/customdata.xml")) \\ "game" \ "num"
    } catch {
      case e: Throwable => { e.printStackTrace(); println("Error loading customdata.xml"); NodeSeq.Empty }
    }
  }
  
  /** Creates a complex XML element from a list of attribute-value pairs. */
  private def createXMLElement(name: String, attributes: Seq[(String, String)]): Elem = {
    XML.loadString(s"<${name} ${attributes.map(a=>s"${a._1}=${"\""+a._2+"\""}").mkString(" ")}/>")
  }
}




