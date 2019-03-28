package gui

import java.io.FileInputStream
import scalafx.scene.media.AudioClip
import scalafx.scene.media.Media
import scalafx.scene.media.MediaPlayer
import java.io.File
import scala.collection.mutable.Map



/** Object that can play a music track on loop. */
object Music {  
  
  /** The current player. */
  private var player: MediaPlayer = null
  
  /** Initialize music as 'warriors'. */
  this.changeMusic("warriors")

  /** Function to start the loop. */
  def startLoop() = this.player.play()
  
  /** Function to stop the loop. */
  def stopLoop() = this.player.pause()
  
  /** Returns true if the music has been muted. */
  def muted = this.player.mute.value
  
  /** Function that toggles mute. */
  def mute() = this.player.mute = !this.player.mute.value

  /** A function to change the music to the given track. */
  def changeMusic(title: String) = {
    var muted = if (this.player != null) {
      this.player.stop()
      this.player.mute.value
    } else false
    val filepath = "assets/sfx/" + title + ".mp3"
    val file = new File(filepath).toURI().toString()
    val media = new Media(file)
    this.player = new MediaPlayer(media)
    player.volume = 0.12
    player.cycleCount = MediaPlayer.Indefinite
    this.player.play()
    this.player.mute = muted
  }
  
  /** The on image for the music button. */
  val onImage  = ImageLoader("note_on")
  
  /** The off image for the music button. */
  val offImage = ImageLoader("note_off")
  
  /** A button to toggle music. */
  def button(x: Double = 1856, y: Double = 48) = {
    new MovableImageButton( { if (Music.muted) offImage else onImage }, x, y) {
      this.pickOnBounds = false
      override def onClick() = {
        Music.mute()
        this.image = { if (Music.muted) offImage else onImage }
      }
      def update() = this.image = { if (Music.muted) offImage else onImage }
    }
  }
}



