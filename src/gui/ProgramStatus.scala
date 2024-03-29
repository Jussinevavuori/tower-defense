package gui

/** Program status keeps track of which scene is active and has methods for
 *  interacting with the scenes.
 */
object ProgramStatus {
  
  /** The current status. */
  private var status: Int = ProgramStatus.MainMenu
  
  /** Function to change the status. */
  def setStatus(s: Int) = {
    require(s >= 0 && s < scenes.size, s"Program status $s does not exist")
    this.status = s
  }
  
  /** Calling ProgramStatus returns the current status as an integer. */
  def apply() = this.status
  
  /** Returns the current scene based on the current status. */
  def scene = this.scenes(this.status)
  
  /** List of scenes. */
  final val scenes = Array[AnimationScene](
    MainMenuScene,
    InGameScene,
    LevelEditorScene,
    LoadGameScene
  )
  
  /** Toggling fullscreen. */
  def toggleFullscreen() = {
    this.scenes.foreach(s => ())
  }
  
  /** Function to start a scene, by default the current scene. */
  def start(s: Int = this.status) = {
    this.scenes(s).loadUp()
    this.scenes(s).start()
    val W = this.scenes(s).getWidth
    val H = this.scenes(s).getHeight
  }
  
  /** Function to initially resize a scene.  */
  def resize(s: Int, maximized: Boolean) = {
    Main.stage.maximized = maximized
    val W = this.scene.getWidth
    val H = this.scene.getHeight
    Main.stage.setWidth( W + {if (maximized) 0 else 16})
    Main.stage.setHeight(H + {if (maximized) 0 else 39})
    this.scenes(s).resize(W, H)
  }

  /** Function to stop a scene, by default the current scene. */
  def stop(s: Int = this.status) = {
    this.scenes(s).shutDown()
    this.scenes(s).stop()
  }
  
  /** The indices (status integers) of each scene. */
  final val MainMenu    = this.scenes.indexOf(MainMenuScene)
  final val InGame      = this.scenes.indexOf(InGameScene)
  final val LevelEditor = this.scenes.indexOf(LevelEditorScene)
  final val LoadGame    = this.scenes.indexOf(LoadGameScene)
}