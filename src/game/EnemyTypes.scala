package game

import scala.util.Random.nextGaussian
import scala.collection.Iterator


/** All enemy types that extend the class enemy together with their respective attributes.
 *  The vary function in each death is created to apply variance to the game and minimize
 *  enemy overlap.
 */


/** Normal level 1 enemy: undivisible, smallest and weakest normal unit */
class EnemyN1(x: Double, y: Double, trg: Option[Path]) extends Enemy(x, y, trg) {
  
  def maxhp  = 10.0
  val typeid = "n1"
  val speed  = 0.02
  val size   = 0.30
  val reward = 3
  val strength = 1
  def death  = Iterator.empty
}


/** Normal level 2 enemy: stronger, divides into 2 level 1 normal enemies */
class EnemyN2(x: Double, y: Double, trg: Option[Path]) extends Enemy(x, y, trg) {
  
  def maxhp  = 18.0
  val typeid = "n2"
  val speed  = 0.03
  val size   = 0.35
  val reward = 2
  val strength = 2
  def death  = Iterator[Enemy](
    new EnemyN1(Vary(this.pos.x) by 0.05, Vary(this.pos.y) by 0.05, this.target),
    new EnemyN1(Vary(this.pos.x) by 0.05, Vary(this.pos.y) by 0.05, this.target)
  )
}


/** Normal level 3 enemy: stronger, divides into 2 level 2 normal enemies */
class EnemyN3(x: Double, y: Double, trg: Option[Path]) extends Enemy(x, y, trg) {
  
  def maxhp  = 26.0
  val typeid = "n3"
  val speed  = 0.04
  val size   = 0.40
  val reward = 1
  val strength = 4
  def death  = Iterator[Enemy](
    new EnemyN2(Vary(this.pos.x) by 0.05, Vary(this.pos.y) by 0.05, this.target),
    new EnemyN2(Vary(this.pos.x) by 0.05, Vary(this.pos.y) by 0.05, this.target)
  )
}


/** Normal level 4 enemy: stronger, divides into 2 level 3 normal enemies */
class EnemyN4(x: Double, y: Double, trg: Option[Path]) extends Enemy(x, y, trg) {
  
  def maxhp  = 34.0
  val typeid = "n4"
  val speed  = 0.05
  val size   = 0.45
  val reward = 1
  val strength = 8
  def death  = Iterator[Enemy](
    new EnemyN3(Vary(this.pos.x) by 0.05, Vary(this.pos.y) by 0.05, this.target),
    new EnemyN3(Vary(this.pos.x) by 0.05, Vary(this.pos.y) by 0.05, this.target)
  )
}


/** Object that allows the syntax Vary(x) by b to return a gaussian random number between x-b and x+b. */
object Vary {
  def apply(b: Double) = new Variance(b)
  class Variance(b: Double) { def by(v: Double) = b + 2 * v * (nextGaussian() - 0.5) }
}

