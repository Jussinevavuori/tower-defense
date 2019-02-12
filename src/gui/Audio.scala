package gui

import java.io.FileInputStream

import scalafx.scene.media.AudioClip
import scalafx.scene.media.Media
import scalafx.scene.media.MediaPlayer
import java.io.File


// Object that can play individual sound files
object Audio {
  
  def play(filename: String, loudness: Double = 0.5) = {
    
    val filepath = "assets/sfx/" + filename
    val file = new File(filepath).toURI().toString()
    val media = new Media(file)
    val player = new MediaPlayer(media)
    player.volume = loudness
    player.play()
    
  }
  
}

// Plays the music in the background. Music can be started, stopped or muted
object Music {
  
  private val filepath = "assets/sfx/soundtrack.mp3"
  private val file = new File(this.filepath).toURI().toString()
  private val media = new Media(this.file)
  private val player = new MediaPlayer(this.media)
  player.volume = 0.12
  player.cycleCount = MediaPlayer.Indefinite
  
  def startLoop() = this.player.play()
  
  def stopLoop() = this.player.pause()
  
  def mute() = this.player.mute = !this.player.mute.value
}