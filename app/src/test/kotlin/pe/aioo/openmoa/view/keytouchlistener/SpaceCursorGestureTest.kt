package pe.aioo.openmoa.view.keytouchlistener

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SpaceCursorGestureTest {

    private fun heldGesture(density: Float = 1f): SpaceCursorGesture {
        val gesture = SpaceCursorGesture(12f * density, 400L)
        gesture.start(100f * density, 1000L)
        assertTrue(gesture.activate(1400L))
        return gesture
    }

    @Test
    fun quickTapAndImmediateDragDoNotMoveCursor() {
        val gesture = SpaceCursorGesture(12f, 400L)
        gesture.start(100f, 1000L)
        assertEquals(0, gesture.move(40f, 1100L))
        assertEquals(0, gesture.move(160f, 1399L))
        assertFalse(gesture.isCursorMode)
        assertTrue(gesture.end())
        assertFalse(gesture.end())
    }

    @Test
    fun activatesAt400MillisecondsWithoutAnyMovement() {
        val gesture = SpaceCursorGesture(12f, 400L)
        gesture.start(100f, 1000L)
        assertFalse(gesture.activate(1399L))
        assertTrue(gesture.activate(1400L))
        assertTrue(gesture.isCursorMode)
        assertFalse(gesture.activate(1500L))
        assertFalse(gesture.end())
        assertFalse(gesture.isCursorMode)
    }

    @Test
    fun movementBeforeActivationDoesNotCauseAJump() {
        val gesture = SpaceCursorGesture(12f, 400L)
        gesture.start(100f, 1000L)
        assertEquals(0, gesture.move(160f, 1300L))
        assertTrue(gesture.activate(1400L))
        assertEquals(0, gesture.move(160f, 1410L))
        assertEquals(-1, gesture.move(148f, 1450L))
        assertFalse(gesture.end())
    }

    @Test
    fun lateTimerStillSuppressesSpaceWhenReleasedAfterHoldTime() {
        val gesture = SpaceCursorGesture(12f, 400L)
        gesture.start(100f, 1000L)
        assertEquals(0, gesture.move(160f, 1400L))
        assertTrue(gesture.isCursorMode)
        assertFalse(gesture.end())
    }

    @Test
    fun smallMovementsAccumulateWithoutRepeatingWhileStationary() {
        val gesture = heldGesture()
        assertEquals(0, gesture.move(96f, 1450L))
        assertEquals(0, gesture.move(92f, 1500L))
        assertEquals(-1, gesture.move(88f, 1550L))
        assertEquals(0, gesture.move(88f, 1600L))
        assertEquals(-1, gesture.move(75f, 1650L))
        assertFalse(gesture.end())
    }

    @Test
    fun fastDragPreservesMultipleStepsAndRemainder() {
        val gesture = heldGesture()
        assertEquals(3, gesture.move(140f, 1450L))
        assertEquals(1, gesture.move(148f, 1500L))
        assertFalse(gesture.end())
    }

    @Test
    fun reversingBackToStartDoesNotInsertSpace() {
        val gesture = heldGesture()
        assertEquals(-3, gesture.move(64f, 1450L))
        assertEquals(1, gesture.move(76f, 1500L))
        assertEquals(2, gesture.move(100f, 1550L))
        assertFalse(gesture.end())
    }

    @Test
    fun cancelBeforeActivationPreventsDelayedModeAndSpace() {
        val gesture = SpaceCursorGesture(12f, 400L)
        gesture.start(100f, 1000L)
        gesture.cancel()
        assertFalse(gesture.activate(1400L))
        assertEquals(0, gesture.move(200f, 1450L))
        assertFalse(gesture.end())
    }

    @Test
    fun cancelDuringCursorModeResetsForTheNextTap() {
        val gesture = heldGesture()
        assertEquals(-2, gesture.move(76f, 1450L))
        gesture.cancel()
        assertFalse(gesture.isCursorMode)
        assertFalse(gesture.activate(1500L))
        assertEquals(0, gesture.move(50f, 1550L))
        assertFalse(gesture.end())
        gesture.start(50f, 2000L)
        assertEquals(0, gesture.move(50f, 2100L))
        assertTrue(gesture.end())
    }

    @Test
    fun nextTouchGetsItsOwnHoldDeadline() {
        val gesture = SpaceCursorGesture(12f, 400L)
        gesture.start(100f, 1000L)
        assertTrue(gesture.end())
        gesture.start(100f, 1300L)
        assertFalse(gesture.activate(1400L))
        assertFalse(gesture.activate(1699L))
        assertTrue(gesture.activate(1700L))
        assertFalse(gesture.end())
    }

    @Test
    fun sameDpTravelProducesSameStepsAtDifferentDensities() {
        for (density in listOf(1f, 1.5f, 2f, 3f)) {
            val gesture = heldGesture(density)
            assertEquals(0, gesture.move(111f * density, 1450L))
            assertEquals(1, gesture.move(112f * density, 1500L))
            assertEquals(3, gesture.move(148f * density, 1550L))
            assertFalse(gesture.end())
        }
    }
}
