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
  
  /** Function to start a scene, by default the current scene. */
  def start(s: Int = this.status) = {
    this.scenes(s).loadUp()
    this.scenes(s).start()
  }

  /** Function to stop a scene, by default the current scene. */
  def stop(s: Int = this.status) = this.scenes(s).stop()
  
  /** The indices (status integers) of each scene. */
  final val MainMenu    = this.scenes.indexOf(MainMenuScene)
  final val InGame      = this.scenes.indexOf(InGameScene)
  final val LevelEditor = this.scenes.indexOf(LevelEditorScene)
  final val LoadGame    = this.scenes.indexOf(LoadGameScene)
}