package game


/* Vec is a class, which represents a two-dimensional vector,
 * and has the necessary vector operations for the game.
 */

case class Vec(var x: Double, var y: Double) {
  
  /* Function to add two vectors together
   * 
   * @param	 Another vector
   * @return The sum vector
   */
  
  def +(that: Vec): Vec = Vec(this.x + that.x, this.y + that.y)
  
  
  /* Function to substract another vector from
   * this vector.
   * 
   * @param	 Another vector
   * @return The sum vector
   */
  
  def -(that: Vec): Vec = Vec(this.x - that.x, this.y - that.y)
  
  
  /* Function to add another vector into this vector. Alters
   * this vector, without altering the other vector.
   * 
   * @param	 Another vector
   * @return Unit
   */
  
  def +=(that: Vec): Unit = {
    val sum = this + that
    this.x = sum.x
    this.y = sum.y
  }
  
  
  /* Returns the size (aka the length or the magnitude) of this
   * vector.
   * 
   * @return The size of this vector
   */
  
  def size: Double = math.sqrt(this.x * this.x + this.y * this.y)
  
  
  /* Function to scale a vector to a given size. Alters this
   * vector.
   * 
   * @param	 The magnitude this vector will be scaled to
   * @return Unit
   */
  
  def scaleTo(mag: Double): Unit = {
    if (this.size != 0) {
      val factor = mag / this.size
      this.x *= factor
      this.y *= factor
    } else {  
      throw new java.lang.ArithmeticException("Unable to scale zero vector")
    }
  }
  
  
  /* Function mainly for position vectors. Changes the position vector's
   * coordinates to that of the given vector.
   *
   * @param	 The vector whose values are copied to this vector
   * @return Unit
   */
  
  def moveTo(that: Vec): Unit = {
    this.x = that.x
    this.y = that.y
  }
  
  
  /* Finds and returns the distance to another position vector.
   * 
   * @param	 Another position vector
   * @return The distance
   */
  
  def distance(that: Vec): Double = {
    val dx = that.x - this.x
    val dy = that.y - this.y
    math.sqrt(dx * dx + dy * dy)
  }
  
  
  /* Finds and returns the distance squared for a lighter calculation.
   * 
   * @param  Another position vector
   * @return The distance, squared
   */
  
  def distanceSqrd(that: Vec): Double = {
    val dx = that.x - this.x
    val dy = that.y - this.y
    dx * dx + dy * dy
  }
  
}

