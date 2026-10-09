package pe.aioo.openmoa.view.keytouchlistener

/** Requires a hold before converting horizontal travel into cursor steps. */
class SpaceCursorGesture(private val stepDistance: Float, private val holdTime: Long) {

    private var active = false
    private var anchorX = 0f
    private var latestX = 0f
    private var startedAt = 0L
    var isCursorMode = false
        private set

    fun start(x: Float, time: Long) {
        active = true
        isCursorMode = false
        startedAt = time
        latestX = x
        anchorX = x
    }

    fun activate(time: Long): Boolean {
        if (!active || isCursorMode || time - startedAt < holdTime) return false
        isCursorMode = true
        // Movement before activation must not cause a cursor jump.
        anchorX = latestX
        return true
    }

    fun move(x: Float, time: Long): Int {
        if (!active) return 0
        latestX = x
        if (!isCursorMode) {
            activate(time)
            return 0
        }
        val steps = ((x - anchorX) / stepDistance).toInt()
        if (steps != 0) {
            anchorX += steps * stepDistance
        }
        return steps
    }

    /** Entering cursor mode suppresses space even if the finger never moved. */
    fun end(): Boolean {
        val insertSpace = active && !isCursorMode
        cancel()
        return insertSpace
    }

    fun cancel() {
        active = false
        isCursorMode = false
    }
}
