package edu.umich.jakoba.kotlinChatter

import android.content.Context
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet

class PostView(context: Context): ConstraintLayout(context) {
    val usernameTextView: TextView
    val messageTextView: EditText

    init {
        usernameTextView = TextView(context).apply {
            id = generateViewId()
            textSize = 24.0f
            setText(R.string.username)
        }

        messageTextView = EditText(context).apply {
            id = generateViewId()
            textSize = 18.0f
            setLineSpacing(0.0f, 1.2f)
            setText(R.string.message)
        }

        id = generateViewId()
        addView(usernameTextView)
        addView(messageTextView)

        val fill = LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT).apply {
            val pad = context.dp2px(8f)
            setPadding(pad, pad, pad, pad)
        }
        setLayoutParams(fill)

        with (ConstraintSet()) {
            clone(this@PostView)

            connect(usernameTextView.id, ConstraintSet.TOP, id, ConstraintSet.TOP, context.dp2px(21.82f))
            connect(usernameTextView.id, ConstraintSet.START, id, ConstraintSet.START)
            connect(usernameTextView.id, ConstraintSet.END, id, ConstraintSet.END)

            connect(messageTextView.id, ConstraintSet.TOP, usernameTextView.id, ConstraintSet.TOP, context.dp2px(36.36f))
            connect(messageTextView.id, ConstraintSet.START, id, ConstraintSet.START)

            applyTo(this@PostView)
        }
    }
}