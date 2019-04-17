
package tests

import game._
import gui._
import org.junit.Test
import org.junit.Assert._

class UnitTests {
  
  // Global timestep
  val dt = 0.001
  
  // Testing that enemies are alive until damaged enough
  @Test def enemyDeath() {
    val e = new EnemyN2(0, 0, null)  // New enemy
    val hp = e.health
    assertTrue(e.alive)  // Enemy should be alive at start
    e.damage(hp - 1)  // Leave 1 hp
    assertTrue(e.alive)  // Enemy should be alive
    e.damage(1)  // Leave 0 hp
    assertFalse(e.alive)  // Enemy should be dead
  } 
  
  // Testing that enemies move correctly
  @Test def enemyAdvance() {
    val p2 = new Path(10, 0, None)  // Second path
    val p1 = new Path(5, 0, Some(p2))  // First path
    val e = new EnemyN1(0, 0, Some(p1))  // Enemy heading towards first path
    var prevX = 0  // Record previous position
    var prevY = 0
    // Move until reached p1
    while (e.target == Some(p1)) {
      e.advance(dt)  // Moving by timestep
      assertTrue(e.pos.x >= prevX)  // Check that enemy moved towards second path
      assertEquals(e.pos.y, 0.0, 0.0001)  // Check that enemy doesn't move on y-axis
    }
    // Check that enemy at p1 and heading to p2
    assertEquals(e.pos.x, p1.pos.x, 0.0001)
    assertEquals(e.pos.y, p1.pos.y, 0.0001)
    assertEquals(e.target, p1.next)
    // Move until reached p2
    while (e.target.isDefined) {
      e.advance(dt)
      assertTrue(e.pos.x >= prevX)  // Check that enemy moved towards second path
      assertEquals(e.pos.y, 0.0, 0.0001)  // Check that enemy doesn't move on y-axis
    }
    // Check that enemy at p1 and heading to p2
    assertEquals(e.pos.x, p2.pos.x, 0.0001)
    assertEquals(e.pos.y, p2.pos.y, 0.0001)
    assertEquals(e.target, p2.next)
    // Check advancing works after reaching final target
    e.advance(dt)
    assertTrue(e.finished && e.dead)
  }
  
  // Testing path functionalities
  @Test def pathChains() {
    // Creating paths
    val p1 = new Path(0, 0, None)
    val p2 = new Path(1, 1, None)
    val p3 = new Path(2, 2, Some(p2))
    val p4 = new Path(3, 3, Some(p3))
    val answer = Array(p4, p3, p2)
    val arr = p4.toArray()
    assertEquals(answer.size, arr.size)
    (0 until 3).foreach(i => assertEquals(answer(i).x, arr(i).x, 0.0001))
    assertTrue(p2.toArray().size == 1 && p2.isLast)
    p2.assignNext(p1)
    assertTrue(!p2.isLast && p1.isLast && !p4.isLast)
    assertEquals(p1.last, p1)
    assertEquals(p4.last, p1)
  }
  
  // Testing the player
  @Test def player() {
    val p = new Player
    // Rewarding, affording and charging money
    assertEquals(p.money, 1000)
    assertTrue(!p.canAfford(1001) && !p.charge(1001))  // Can not afford
    assertTrue(p.canAfford(999) && p.charge(999))  // Can afford
    assertTrue(p.money == 1)  // Money left
    assertFalse(p.charge(2))  // Can not afford
    p.reward(1)  // Giving money
    assertTrue(p.charge(2))  // Charging rest of money
    p.reward(-1)  // Can not reward negative money
    assertEquals(p.money, 0)  // Money should still be at 0
    // Healing, damaging and HP
    assertTrue(p.alive)  // Should be alive by default
    p.damage(99)  // Damaging to 1 hp
    assertTrue(p.alive && p.health == 1)  // Should be alive with 1 hp
    p.heal(5)  // Healing by 5
    p.heal(-5)  // Healing by -5 should fail
    assertEquals(p.health, 1 + 5)  // Player should have 6 hp now
    p.damage(6)  // Damaging to 0 hp
    assertTrue(p.dead) // Player should be dead
    p.heal(5)  // Healing shouldn't work after death
    assertTrue(p.dead && p.health == 0)  // Player should still be dead.
  }
  
  // Testing bullet projectile movement and collision
  @Test def bulletProjectile() {
    val t = new EnemyN1(0, 0, None)
    val b1 = new Bullet(10, 0, 1, 1, t) // Shouldn't reach
    val b2 = new Bullet(5, 0, 1, 10, t) // Should reach
    // Start moving bullets towards target
    while (!b2.finished) {
      assertTrue(b1.pos.x > 9 ^ b1.finished)
      b1.move(dt)
      b1.hit(Iterator(t))
      b2.move(dt)
      b2.hit(Iterator(t))
    }
    assertTrue(b1.finished && b2.finished)  // b1 out of range, b2 not still going
    assertEquals(t.pos.x + t.size, b2.pos.x, 0.01) // v2 should be at its target
    assertTrue(b1.hitEnemies.isEmpty) // b1 hit nothing
    assertTrue(b2.hitEnemies.size == 1 && b2.hitEnemies.contains(t)) // b2 hit only target
    assertTrue(t.health < t.maxhp) // enemy was damaged
  }
  
  // Testing boomerang projectile movement and collision
  @Test def boomerangProjectile() {
    val t = new EnemyN1(1, 0, None)
    val b = new Boomerang(0, 0, 1, t, 0.25)
    var prevX = 0.0
    val dt = 0.001
    // Moving boomerang until apex
    while (prevX <= b.pos.x) {
      prevX = b.pos.x
      b.move(dt)
      b.hit(Iterator(t))
    }
    assertTrue(b.hitEnemies.isEmpty) // should've cleared hit enemies
    assertTrue(t.health < t.maxhp)  // enemy was still damaged
    var latestHP = t.health
    // Moving boomerang until it returns
    while (!b.finished) {
      b.move(dt)
      b.hit(Iterator(t))
    }
    assertTrue(b.hitEnemies.nonEmpty) // should've hit enemies
    assertTrue(t.health < latestHP) // should've damaged enemy further
    assertEquals(b.pos.x, 0, 0.1) // should've returned to near 0, 0
    assertEquals(b.pos.y, 0, 0.1)
  }
  
  // Testing missile projectile movement and collision against still targets
  @Test def missileStillTarget() {
    val t = new EnemyN1(1, 0, None)  // Still enemy
    val m = new Missile(0, 0, 1, 10, 1, t)
    while (!m.finished) {
      m.move(dt)
      t.advance(dt)
      m.hit(Iterator(t))
    }
    assertTrue(m.hitEnemies.size == 1 && m.hitEnemies.contains(t)) // hit target
    assertEquals(t.pos.x - t.size, m.pos.x, 0.01) // at target
    
  }
  
  // Testing missile projectile movement and collision against moving targets
  @Test def missileMovingTarget() {
    val p = new Path(1, Int.MaxValue) // Target for moving enemy
    val t = new EnemyN2(1, 0, Some(p)) // Moving enemy
    val m = new Missile(0, 0, 1, 10, 1, t)
    while (!m.finished) {
      m.move(dt)
      t.advance(dt)
      m.hit(Iterator(t))
    }
    assertTrue(m.hitEnemies.size == 1 && m.hitEnemies.contains(t)) // hit target
    assertEquals(t.pos.x, m.pos.x, 1.05 * t.size) // at target
    assertEquals(t.pos.y, m.pos.y, 1.05 * t.size)
  }
  
  // Testing missile projectile movement and collision against mutliple targets
  @Test def missileMultipleTargets()  {
    val t = Array.fill(3)(new EnemyN1(1, 0, None))
    val m = new Missile(0, 0, 1, 10, 5, t.head)
    while (!m.finished) {
      m.move(dt)
      m.hit(t.clone().iterator)
    }
    t.foreach(e => assertTrue(e.health < e.maxhp))  // All enemies were hit
  }
  
  // Testing tower targeting
  @Test def towerTargeting() {
    // Enemy e moving towards target
    val t = new CannonTower1(2, 0) { override val radius = 1.0 }
    val p = new Path(3, 0)
    val e1 = new EnemyN1(0, 0, Some(p))
    while (e1.pos.x < 0.95) { 
      e1.advance(dt)
      t.updateTarget(Iterator(e1))
    }
    assertTrue(t.target.isEmpty) // enemy not yet within radius
    while (e1.pos.x < 1.05) {  // move within radius
      e1.advance(dt)
      t.updateTarget(Iterator(e1))
    }
    assertTrue(t.target.get.eq(e1)) // e1 is now the target
    e1.damage(1000) // killing e1
    t.updateTarget(Iterator(e1))
    assertFalse(e1.alive)
    assertTrue(t.target.isEmpty) // e1 should no longer be target
    val e2 = new EnemyN1(0, 0, Some(p))
    while (e2.pos.x < 1.95) {  // moving new enemy to edge of radius
      e2.advance(dt)
      t.updateTarget(Iterator(e2))
    }
    assertTrue(t.target.get.eq(e2)) // e2 should be target
    while (!e2.finished) {  // moving e2 outside of radius
      e2.advance(dt)
      t.updateTarget(Iterator(e2))
    }
    assertTrue(t.target.isEmpty) // t should now lose the target
  }
  
  // Testing tower shooting
  @Test def towerShooting() { assert(true) }
  
  // Testing vector functions: +, -, +=, -=
  @Test def vectorBasicArithmetic() { assert(true) }
  
  // Testing vector size, scale and limit
  @Test def vectorSizeAndScaling() { assert(true) }
  
  // Testing vector distance and moving
  @Test def vectorDistanceAndMove() { assert(true) }
  
  // Testing waves
  @Test def wave() { assert(true) }
  
  // Testing different game scenarios
  @Test def game1() { assert(true) }
}







