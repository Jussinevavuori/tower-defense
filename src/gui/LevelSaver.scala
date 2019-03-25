package gui

import game._
import scala.xml._
import java.io.File

object LevelSaver {
  
  /*
   * Resets all custom levels and leaves none left.
   */
  def resetCustomLevels() = {
    try {
      XML.save("data/customdata.xml", <empty></empty>, "UTF-8", true, null)
    } catch {
      case e: Throwable => println("Something went wrong when trying to reset customdata.xml")
    }
    println(s"${"-"*40}\nRESET ALL CUSTOM LEVELS\n${"-"*40}\n")
  }
  
  /*
   * Saves a custom level based on its path to the data by appending it.
   */
  def saveCustomLevel(p: Path) = {
    
    val levels: NodeSeq = try {
      XML.loadFile(new File("data/customdata.xml")) \ "game"
    } catch {
      case e: Throwable => { e.printStackTrace(); println("Error loading customdata.xml"); NodeSeq.Empty }
    }
    val path:   Elem = {
      <path>{p.toArray().map(p => {
        this.createXMLElement("segment", Array("x" -> p.pos.x.toInt.toString(), "y" -> p.pos.y.toInt.toString()))
      })}</path>
    }
    val num:    Elem = <num>{(levels \ "num").length}</num>
    val cols:   Elem = <cols>{Main.currentGame.cols}</cols>
    val rows:   Elem = <rows>{Main.currentGame.rows}</rows>
    val wave:   Elem = <wave>{0}</wave>
    val player: Elem = <player><health>{100}</health><money>{500}</money></player>
    val towers: Elem = <towers></towers>
    val level:  Elem = <game>{num}{cols}{rows}{wave}{player}{path}</game>
    val data:   Elem = <data>{levels}{level}</data>

    try {
      XML.save("data/customdata.xml", data, "UTF-8", true, null)
    } catch {
      case e: Throwable => println("Something went wrong when trying to save customdata.xml to file")
    }
    
    val prettyPrinter = new PrettyPrinter(80, 4)
    println(s"${"-"*40}\nSAVED LEVELS\n${"-"*40}\n")
    println(prettyPrinter.format(data))
  }
  
  
  /*
   * Deletes a chose custom level based
   */
  def deleteCustomLevel() = {
    
  }
  
  /*
   * Loads the list of available levels
   */
  def loadLevelList() = {
    try {
      XML.loadFile(new File("data/customdata.xml")) \\ "game" \ "num"
    } catch {
      case e: Throwable => { e.printStackTrace(); println("Error loading customdata.xml"); NodeSeq.Empty }
    }
  }
  
  
  
  // Creates a complex XML element from a list of attribute-value pairs
  private def createXMLElement(name: String, attributes: Seq[(String, String)]): Elem = {
    XML.loadString(s"<${name} ${attributes.map(a=>s"${a._1}=${"\""+a._2+"\""}").mkString(" ")}/>")
  }
}
