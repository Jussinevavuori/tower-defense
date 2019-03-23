package gui

/*
 * Program status keeps track of which status the program is in, for example
 * main menu or ingame or level editor. The program status has functions to
 * change the status, start and stop animations on the statuses and it also
 * knows which scene to display for each status.
 */


object ProgramStatus {
  
  private var status: Int = ProgramStatus.MainMenu
  def setStatus(s: Int) = {
    require(s >= 0 && s < scenes.size, s"Program status $s does not exist")
    this.status = s
  }
  
  def apply() = this.status
  
  def scene = this.scenes(this.status)
  
  final val scenes = Array(
    MainMenuScene,
    InGameScene,
    LevelEditorScene
  )
  def start(s: Int = this.status) =
    if (this.scenes(s).isInstanceOf[AnimationScene]) {
        this.scenes(s).asInstanceOf[AnimationScene].start()
  }
  def stop(s: Int = this.status) = {
    if (this.scenes(s).isInstanceOf[AnimationScene])
        this.scenes(s).asInstanceOf[AnimationScene].stop()
  }
  
  final val MainMenu    = 0
  final val InGame      = 1
  final val LevelEditor = 2
  
}