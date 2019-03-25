package game

import scala.collection.mutable.Buffer

/* A path describes a piece of the path that the enemies take.
 * The path objects are what the enemies use for navigating the
 * map. Each map contains a position and a reference to the next
 * path object, if there is any.
 * 
 * Path objects create a linked structure, that can be navigated
 * from the first path to the last path, but not backwards.
 */

case class Path(_x: Double, _y: Double,
                private var _next: Option[Path] = None) {
  
  /* Returns true if this is the last path in its
   * chain, that is if it doesn't have a next path
   * assigned to it.
   */
  
  def isLast: Boolean = this._next.isEmpty
  
  
  /* Returns true if this path is followed by another
   * path.
   */
  
  def hasNext: Boolean = this._next.isDefined
  
  
  /* Returns the last path in the chain
   */
  def last: Path = {
    var p = this
    while (p.hasNext) { p = p.next.get }
    p
  }
  
  
  /* Assigns a path to be the next path for this one.
   * A path that already has another path assigned to it
   * cannot be reassigned.
   */
  
  def assignNext(assigned: Path) = if (this.isLast) this._next = Some(assigned)

  
  /* Returns the next path for this path so that it
   * cannot be modified.
   */
  
  def next: Option[Path] = this._next
  
  
  /* The position of this vector as a private variable so that
   * it cannot be modified.
   */
  
  val _pos: Vec = Vec(_x, _y)
  
  
  /* Returns the position of this path, so that it cannot
   * be modified.
   */
  
  def pos: Vec = this._pos
  
  
  /* Returns a textual description of the path segment
   */
  
  override def toString(): String = s"Path (${this._pos.x}, ${this._pos.y})"
  
  
  /* Generates an array of path segments, all followed by none
   * from this path segment and the segments chained to it
   */
  
  def toArray(): Array[Path] = {
    var segments = Buffer[Path]()
    var current: Option[Path] = Some(this)
    while (current.isDefined) {
      segments += new Path(current.get.pos.x, current.get.pos.y, None)
      current = current.get._next
    }
    segments.toArray
  }
  
}


