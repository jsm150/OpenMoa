package pe.aioo.openmoa.view.keytouchlistener

import android.content.Context
import android.os.SystemClock
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import pe.aioo.openmoa.view.KeyboardFrameLayout
import pe.aioo.openmoa.view.message.CursorMoveMessage
import pe.aioo.openmoa.view.message.StringKeyMessage

class SpaceKeyTouchListener(context: Context) : BaseKeyTouchListener(context) {

    private val gesture = SpaceCursorGesture(
        config.spaceCursorStepDp * context.resources.displayMetrics.density,
        config.spaceCursorHoldTime,
    )
    private var pointerId = MotionEvent.INVALID_POINTER_ID
    private var pressedView: View? = null
    private var keyboardFrame: KeyboardFrameLayout? = null
    private val activateCursorMode = Runnable {
        val view = pressedView ?: return@Runnable
        if (!view.isShown) {
            finishTouch()
        } else if (gesture.activate(SystemClock.uptimeMillis())) {
            showCursorMode(view)
        }
    }

    override fun previewText(view: View): CharSequence? = null

    private fun showCursorMode(view: View) {
        keyboardFrame?.showCursorMode()
        if (config.hapticFeedback) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }
    }

    private fun moveCursor(x: Float, time: Long) {
        val wasCursorMode = gesture.isCursorMode
        val steps = gesture.move(x, time)
        if (!wasCursorMode && gesture.isCursorMode) pressedView?.let(::showCursorMode)
        if (steps != 0) sendKeyMessage(CursorMoveMessage(steps))
    }

    private fun cancelGesture() {
        pressedView?.let {
            it.removeCallbacks(activateCursorMode)
            resetKey(it)
        }
        gesture.cancel()
        pointerId = MotionEvent.INVALID_POINTER_ID
        pressedView = null
        keyboardFrame = null
    }

    private fun finishTouch() {
        val frame = keyboardFrame
        if (frame != null) frame.dismissCursorMode() else cancelGesture()
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val frame = KeyboardFrameLayout.from(view)
                frame?.beginCursorGesture(::cancelGesture)
                keyboardFrame = frame
                pressedView = view
                pointerId = motionEvent.getPointerId(0)
                gesture.start(motionEvent.x, motionEvent.eventTime)
                view.postDelayed(activateCursorMode, config.spaceCursorHoldTime)
            }
            MotionEvent.ACTION_MOVE -> {
                val index = motionEvent.findPointerIndex(pointerId)
                if (index >= 0) moveCursor(motionEvent.getX(index), motionEvent.eventTime)
            }
            MotionEvent.ACTION_UP -> {
                moveCursor(motionEvent.x, motionEvent.eventTime)
                val insertSpace = gesture.end()
                finishTouch()
                if (insertSpace) sendKeyMessage(StringKeyMessage(" "))
            }
            MotionEvent.ACTION_CANCEL -> {
                finishTouch()
            }
            MotionEvent.ACTION_POINTER_UP -> {
                if (motionEvent.getPointerId(motionEvent.actionIndex) == pointerId) {
                    // Do not transfer an ongoing drag to another finger.
                    finishTouch()
                }
            }
        }
        return super.onTouch(view, motionEvent)
    }
}
