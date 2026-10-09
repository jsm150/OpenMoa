package pe.aioo.openmoa.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import pe.aioo.openmoa.R
import pe.aioo.openmoa.databinding.KeyboardFrameLayoutBinding

class KeyboardFrameLayout : FrameLayout {

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init()
    }

    private lateinit var binding: KeyboardFrameLayoutBinding
    private val keyPreview = KeyPreviewPopup(context)
    private var cancelCursorGesture: (() -> Unit)? = null

    fun showKeyPreview(key: View, text: CharSequence) = keyPreview.show(key, text)

    fun dismissKeyPreview(key: View? = null) = keyPreview.dismiss(key)

    fun beginCursorGesture(onCancel: () -> Unit) {
        dismissCursorMode()
        cancelCursorGesture = onCancel
    }

    fun showCursorMode() {
        if (cancelCursorGesture == null) return
        dismissKeyPreview()
        binding.cursorModeOverlay.visibility = View.VISIBLE
    }

    fun dismissCursorMode() {
        binding.cursorModeOverlay.visibility = View.GONE
        val cancel = cancelCursorGesture
        cancelCursorGesture = null
        cancel?.invoke()
    }

    override fun onDetachedFromWindow() {
        dismissCursorMode()
        dismissKeyPreview()
        super.onDetachedFromWindow()
    }

    private fun init() {
        inflate(context, R.layout.keyboard_frame_layout, this)
        binding = KeyboardFrameLayoutBinding.bind(this)
    }

    fun setKeyboardView(view: View) {
        dismissCursorMode()
        dismissKeyPreview()
        view.parent?.let {
            if (it is ViewGroup) {
                it.removeView(view)
            }
        }
        binding.keyboardLayout.removeAllViews()
        binding.keyboardLayout.addView(view)
    }

    companion object {
        fun from(view: View): KeyboardFrameLayout? {
            var parent = view.parent as? View
            while (parent != null) {
                if (parent is KeyboardFrameLayout) return parent
                parent = parent.parent as? View
            }
            return null
        }
    }

}
