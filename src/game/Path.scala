package game

/** A path describes a coordinate pair. Paths are chained in a one-directional chain,
 *  with each path having a next path, which the enemies can then use to navigate.
 */
case class Path(x: Double, y: Double, private var _next: Option[Path] = None) {
  
  /** Returns true if no path follows this path: last in chain. */
  def isLast: Boolean = this._next.isEmpty
  
  /** Returns true if a path follows this path. */
  def hasNext: Boolean = this._next.isDefined
  
  /** Assigns a path to follow this path if this path has no following path. */
  def assignNext(assigned: Path) = if (this.isLast) this._next = Some(assigned)

  /** Returns the following path wrapped in an option. */
  def next: Option[Path] = this._next
  
  /** The position of this path as a position vector. */
  private val _pos: Vec = Vec(x, y)
  
  /** Returns this path's position. */
  def pos: Vec = this._pos
  
  /** Returns the last path in this path's chain. */
  def last: Path = {
    var p = this
    while (p.hasNext) { p = p.next.get }
    return p
  }
  
  /** Generates an array of this path and all the paths following it in the chain. */
  def toArray(): Array[Path] = {
    var segments = Array[Path]()
    var current: Option[Path] = Some(this)
    while (current.isDefined) {
      segments = segments :+ new Path(current.get.pos.x, current.get.pos.y, None)
      current = current.get.next
    }
    segments.toArray
  }
  
  /** Returns a textual description of this path segment. */
  override def toString(): String = s"Path (${this._pos.x}, ${this._pos.y})"
  
}


