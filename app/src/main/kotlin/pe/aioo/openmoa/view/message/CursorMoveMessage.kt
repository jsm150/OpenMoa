package pe.aioo.openmoa.view.message

import java.io.Serializable

class CursorMoveMessage(val steps: Int) : BaseKeyMessage(), Serializable
