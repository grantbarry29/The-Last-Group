package edu.umich.jakoba.kotlinChatter

import android.content.Context
import android.util.TypedValue
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet

class ChattListItem(context: Context): ConstraintLayout(context) {
    val usernameTextView: TextView
    val timestampTextView: TextView
    val messageTextView: TextView
    val geodataTextView: TextView
    val mapButton: ImageButton

    init {
        usernameTextView = TextView(context).apply {
            id = generateViewId()
            textSize = 18.0f
        }
        timestampTextView = TextView(context).apply {
            id = generateViewId()
            textSize = 14.0f
        }
        messageTextView = TextView(context).apply {
            id = generateViewId()
            textSize = 18.0f
            setLineSpacing(0.0f, 1.2f)
        }
        geodataTextView = TextView(context).apply {
            id = generateViewId()
            setAutoSizeTextTypeUniformWithConfiguration(12, 14, 1, TypedValue.COMPLEX_UNIT_SP)
            setLineSpacing(0.0f, 1.2f)
        }
        mapButton = ImageButton(context).apply {
            id = generateViewId()
            visibility = GONE
            setBackgroundResource(R.drawable.border)
            setImageResource(android.R.drawable.ic_menu_mylocation)
        }
        id = generateViewId()
        addView(usernameTextView)
        addView(timestampTextView)
        addView(messageTextView)
        addView(geodataTextView)
        addView(mapButton)

        val fill = LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT).apply {
            setPadding(context.dp2px(5.82f), context.dp2px(7.27f),
                context.dp2px(5.82f), context.dp2px(13.82f))
        }
        setLayoutParams(fill)

        with (ConstraintSet()) {
            clone(this@ChattListItem)

            connect(usernameTextView.id, ConstraintSet.TOP, id, ConstraintSet.TOP)
            connect(usernameTextView.id, ConstraintSet.START, id, ConstraintSet.START)

            connect(timestampTextView.id, ConstraintSet.TOP, id, ConstraintSet.TOP)
            connect(timestampTextView.id, ConstraintSet.END, id, ConstraintSet.END)

            val margin = context.dp2px(8f)
            connect(messageTextView.id, ConstraintSet.TOP, usernameTextView.id, ConstraintSet.BOTTOM, margin)
            connect(messageTextView.id, ConstraintSet.START, id, ConstraintSet.START)


            connect(geodataTextView.id, ConstraintSet.TOP, messageTextView.id, ConstraintSet.BOTTOM, margin)
            connect(geodataTextView.id, ConstraintSet.START, id, ConstraintSet.START)

            connect(mapButton.id, ConstraintSet.TOP, timestampTextView.id, ConstraintSet.BOTTOM, margin)
            connect(mapButton.id, ConstraintSet.BOTTOM, geodataTextView.id, ConstraintSet.TOP, margin)
            connect(mapButton.id, ConstraintSet.END, id, ConstraintSet.END)
            val dim = context.dp2px(40f)
            constrainWidth(mapButton.id, dim)
            constrainHeight(mapButton.id, dim)
            applyTo(this@ChattListItem)
        }
    }
}
