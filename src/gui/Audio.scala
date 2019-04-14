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
  def play(filename: String, loudness: Double = 0.1) = {
    val clip = new AudioClip("file:assets/sfx/" + filename)
    clip.play(loudness)
  }
}