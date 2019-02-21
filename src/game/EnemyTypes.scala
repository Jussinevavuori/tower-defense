package game

import scala.util.Random.nextDouble

/** An enemy type is created followingly
  * 
  * {{{
  * 	
  * class EnemyType(_x: Double, _y: Double, _target: Option[Path])
  * 	extends Enemy("name", health, speed, size, reward, _x, _y, _target) {
  * 
  * 	def death: Array[Enemy]
  * 
  * }
  * 
  * }}}
  * 
  * ...where:
  * 
  * - The _x, _y and _target parameters specify the place, where the
  * 	enemy type is created and the target is its first target.
  * - Name is a string which describes the name of the enemy type
  *	- Health is a double which describes how much health enemies of that type
  *		initially have
  *	- Size is a double, which describes (in grid units) how big of a radius
  *		the enemy has
  * - Death is a method, which returns the enemies, that spawn, when this
  * 	enemy dies. 
  * - Reward is amount of money rewarded to player upon killing the enemy
  */


// Normal first: smallest normal unit, does not spawn any units
class EnemyN1(_x: Double, _y: Double, _target: Option[Path])
  extends Enemy("n1", 10.0, Speed(0.02, 0.005), 0.30, 3, _x, _y, _target) {
  def death = Array[Enemy]()  
}

// Normal second: spawns two smaller units
class EnemyN2(_x: Double, _y: Double, _target: Option[Path])
  extends Enemy("n2", 18.0, Speed(0.03, 0.005), 0.35, 2, _x, _y, _target) {
  def death = Array[Enemy](
      new EnemyN1(this.pos.x, this.pos.y, this.target),
      new EnemyN1(this.pos.x, this.pos.y, this.target)
  )
}

// Normal third: spawns two smaller units
class EnemyN3(_x: Double, _y: Double, _target: Option[Path])
  extends Enemy("n3", 40.0, Speed(0.07, 0.005), 0.40, 1, _x, _y, _target) {
  def death = Array[Enemy](
      new EnemyN2(this.pos.x, this.pos.y, this.target),
      new EnemyN2(this.pos.x, this.pos.y, this.target)
  )
}

// Normal fourth: spawns two smaller units
class EnemyN4(_x: Double, _y: Double, _target: Option[Path])
  extends Enemy("n4", 40.0, Speed(0.09, 0.005), 0.45, 1, _x, _y, _target) {
  
  def death = Array[Enemy](
      new EnemyN3(this.pos.x, this.pos.y, this.target),
      new EnemyN3(this.pos.x, this.pos.y, this.target)
  )
}




// A function that returns the random number from the between
// base + variance and base - variance to be used as a speed

object Speed {
  def apply(base: Double, variance: Double) = base + 2 * variance * (nextDouble() - 0.5)
}

