package gui

import java.io.FileInputStream
import scalafx.scene.image.Image

/** Object that loads images from assets/gfx. */
object ImageLoader {
  
  def apply(filename: String): Image = {
    
    val filepath = "assets/gfx/" + filename + ".png"
    val inputStream = new FileInputStream(filepath)
    val image = new Image(inputStream)
    inputStream.close()
    image
  }  
  
}