package pe.aioo.openmoa.view.keytouchlistener

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View
import pe.aioo.openmoa.view.message.BaseKeyMessage
import pe.aioo.openmoa.view.message.StringKeyMessage
import kotlin.math.*

class CrossKeyTouchListener(
    context: Context,
    private val keyList: List<BaseKeyMessage>,
) : BaseKeyTouchListener(context) {

    private var startX: Float = 0f
    private var startY: Float = 0f
    private var selectedKey: BaseKeyMessage = keyList[2]

    override fun previewText(view: View): CharSequence? =
        (selectedKey as? StringKeyMessage)?.key

    private fun keyAt(x: Float, y: Float): BaseKeyMessage {
        val distance = sqrt((x - startX).pow(2) + (y - startY).pow(2))
        if (distance > config.gestureThreshold) {
            val degree = (atan2(y - startY, x - startX) * 180f) / PI
            return when {
                abs(degree) < 45f -> keyList[1]
                abs(degree) < 135f -> if (degree > 0) keyList[2] else keyList[0]
                else -> keyList[3]
            }
        }
        return keyList[2]
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = motionEvent.x
                startY = motionEvent.y
                selectedKey = keyList[2]
            }
            MotionEvent.ACTION_MOVE -> selectedKey = keyAt(motionEvent.x, motionEvent.y)
            MotionEvent.ACTION_UP -> {
                selectedKey = keyAt(motionEvent.x, motionEvent.y)
                sendKeyMessage(selectedKey)
            }
            MotionEvent.ACTION_CANCEL -> selectedKey = keyList[2]
        }
        return super.onTouch(view, motionEvent)
    }

}
