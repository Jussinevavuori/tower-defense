package game


/** Vec class implements simple vectors and contains only the necessary vector operations
 *  for the game.
 */
case class Vec(var x: Double, var y: Double) {
  
  /** Adds two vectors together and returns the sum vector. */
  def +(that: Vec): Vec = Vec(this.x + that.x, this.y + that.y)
  
  /** Subtracts another vector from this vector and returns the sum vector. */
  def -(that: Vec): Vec = Vec(this.x - that.x, this.y - that.y)
  
  /** Changes this vector to be the sum of this and another vector. */
  def +=(that: Vec): Unit = {
    val sum = this + that
    this.x = sum.x
    this.y = sum.y
  }
  
  /** Returns this vector's size/magnitude/length. */
  def size: Double = math.sqrt(this.x * this.x + this.y * this.y)
  
  /** Adjusts this vector's length to the given magnitude without changing the direction. */
  def scaleTo(mag: Double): Unit = {
    if (this.size != 0) {
      val factor = mag / this.size
      this.x *= factor
      this.y *= factor
    } else {  
      throw new java.lang.ArithmeticException("Unable to scale zero vector")
    }
  }
  
  /** Moves a (position) vector by changing it's coordinates to that of another vector's. */
  def moveTo(that: Vec): Unit = {
    this.x = that.x
    this.y = that.y
  }
  
  /** Returns the distance from this (position) vector to another (position) vector. */
  def distance(that: Vec): Double = {
    val dx = that.x - this.x
    val dy = that.y - this.y
    math.sqrt(dx * dx + dy * dy)
  }
  
  /** Returns the distance squared from this (position) vector to another (position) vector. */
  def distanceSqrd(that: Vec): Double = {
    val dx = that.x - this.x
    val dy = that.y - this.y
    dx * dx + dy * dy
  }
  
}

