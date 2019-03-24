package gui

import java.io.FileInputStream
import scalafx.scene.media.AudioClip
import scalafx.scene.media.Media
import scalafx.scene.media.MediaPlayer
import java.io.File
import scala.collection.mutable.Map


// Object that can play individual sound files
object Audio {
  
  def play(filename: String, loudness: Double = 0.5) = {
    val player = new MediaPlayer(this.loadAudio(filename))
    player.volume = loudness
    player.play()
  }
  
  def playTimes(filename: String, times: Int, loudness: Double = 0.5) = {
    val player = new MediaPlayer(this.loadAudio(filename))
    player.cycleCount = times
    player.volume = loudness
    player.play()
  }
  
  private def loadAudio(filename: String) = {
    val filepath = "assets/sfx/" + filename
    val file = new File(filepath).toURI().toString()
    new Media(file)
  }
   
  
}

// Plays the music in the background. Music can be started, stopped or muted
object Music {
  
  private var player: MediaPlayer = null
  
  def changeMusic(title: String) = {
    if (this.player != null) {
      this.player.stop()
    }
    val filepath = "assets/sfx/" + title + ".mp3"
    val file = new File(filepath).toURI().toString()
    val media = new Media(file)
    this.player = new MediaPlayer(media)
    player.volume = 0.12
    player.cycleCount = MediaPlayer.Indefinite
    this.player.play()
  }
  this.changeMusic("warriors")

  def startLoop() = this.player.play()
  
  def stopLoop() = this.player.pause()
  
  def mute() = this.player.mute = !this.player.mute.value

}