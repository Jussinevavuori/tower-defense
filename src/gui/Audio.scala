package gui

import java.io.FileInputStream
import scalafx.scene.media.AudioClip
import scalafx.scene.media.Media
import scalafx.scene.media.MediaPlayer
import java.io.File
import scala.collection.mutable.Map


/** Audio can play individual .wav files in assets/sfx. */
object Audio {
  
  /** Plays the chosen .wav audiofile at the given loudness. */
  def play(filename: String, loudness: Double = 0.3) = {
    val player = new MediaPlayer(this.audiofiles(filename))
    player.volume = loudness
    player.play()
  }
  
  /** Plays the chosen .wav audiofile at the given loudness the chosen amount of times. */
  def playTimes(filename: String, times: Int, loudness: Double = 0.3) = {
    val player = new MediaPlayer(this.audiofiles(filename))
    player.cycleCount = times
    player.volume = loudness
    player.play()
  }
  
  /** All the existing audio clips in a map, so they do not have to be loaded each time. */
  private val audiofiles = Map[String, Media](
      "coin.wav"        -> this.loadAudio("coin.wav"       ),
      "coincluster.wav" -> this.loadAudio("coincluster.wav"),
      "damage.wav"      -> this.loadAudio("damage.wav"     ),
      "damage2.wav"     -> this.loadAudio("damage2.wav"    ),
      "error.wav"       -> this.loadAudio("error.wav"      ),
      "explosion.wav"   -> this.loadAudio("explosion.wav"  ),
      "fanfare.wav"     -> this.loadAudio("fanfare.wav"    ),
      "gameover.wav"    -> this.loadAudio("gameover.wav"   ),
      "hit.wav"         -> this.loadAudio("hit.wav"        ),
      "impact.wav"      -> this.loadAudio("impact.wav"     ),
      "iosfx.wav"       -> this.loadAudio("iosfx.wav"      ),
      "menu.wav"        -> this.loadAudio("menu.wav"       ),
      "newwave.wav"     -> this.loadAudio("newwave.wav"    ),
      "shot1.wav"       -> this.loadAudio("shot1.wav"      ),
      "shot2.wav"       -> this.loadAudio("shot2.wav"      ),
      "shot3.wav"       -> this.loadAudio("shot3.wav"      ),
      "throw.wav"       -> this.loadAudio("throw.wav"      ),
      "titlescreen.wav" -> this.loadAudio("titlescreen.wav")
  )
  
  /** Function to load a chosen audio clip. */
  private def loadAudio(filename: String) = {
    val filepath = "assets/sfx/" + filename
    val file = new File(filepath).toURI().toString()
    new Media(file)
  }
  
}