package pe.aioo.openmoa.view

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import com.google.android.material.color.MaterialColors
import pe.aioo.openmoa.R
import kotlin.math.roundToInt

/** A visual hint only: it never takes focus or intercepts a keyboard gesture. */
class KeyPreviewPopup(context: Context) {
    private val density = context.resources.displayMetrics.density
    private fun dp(value: Int) = (value * density).roundToInt()

    private val label = TextView(context).apply {
        gravity = Gravity.CENTER
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f)
        typeface = Typeface.DEFAULT_BOLD
        setTextColor(MaterialColors.getColor(context, R.attr.keyForeground, 0))
        background = GradientDrawable().apply {
            cornerRadius = dp(10).toFloat()
            setColor(MaterialColors.getColor(context, R.attr.keyBackgroundPressed, 0))
            setStroke(dp(1), MaterialColors.getColor(context, R.attr.keyForegroundMuted, 0))
        }
        importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
    }
    private val popup = PopupWindow(label, dp(64), dp(68), false).apply {
        isTouchable = false
        isOutsideTouchable = false
        inputMethodMode = PopupWindow.INPUT_METHOD_NOT_NEEDED
        // Allow the top row's preview to extend above the IME window.
        isClippingEnabled = false
        windowLayoutType = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL
        elevation = dp(6).toFloat()
        animationStyle = 0
    }
    private var owner: View? = null

    fun show(key: View, text: CharSequence) {
        if (!key.isAttachedToWindow || !key.isShown || key.windowToken == null) return
        label.text = text
        owner = key
        val location = IntArray(2)
        key.getLocationInWindow(location)
        val rootLocation = IntArray(2)
        key.rootView.getLocationInWindow(rootLocation)
        val margin = dp(4)
        val minX = rootLocation[0] + margin
        val maxX = (rootLocation[0] + key.rootView.width - popup.width - margin).coerceAtLeast(minX)
        val x = (location[0] + (key.width - popup.width) / 2).coerceIn(minX, maxX)
        val y = location[1] - popup.height - margin
        if (popup.isShowing) {
            popup.update(x, y, popup.width, popup.height)
        } else {
            popup.showAtLocation(key, Gravity.NO_GRAVITY, x, y)
        }
    }

    fun dismiss(key: View? = null) {
        // Releasing an earlier finger must not hide another key's newer preview.
        if (key != null && owner !== key) return
        popup.dismiss()
        owner = null
    }
}
