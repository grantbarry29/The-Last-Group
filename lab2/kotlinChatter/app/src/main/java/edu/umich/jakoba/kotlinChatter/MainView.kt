package edu.umich.jakoba.kotlinChatter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.ListView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainView(context: Context): ConstraintLayout(context) {
    val chattListView: ListView
    val postButton: FloatingActionButton
    val refreshContainer: SwipeRefreshLayout

    init {
        chattListView = ListView(context)

        postButton = FloatingActionButton(context).apply {
            id = generateViewId()
            setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC107")))
            setImageResource(android.R.drawable.ic_input_add)
        }

        refreshContainer = SwipeRefreshLayout(context).apply {
            id = generateViewId()
            addView(chattListView)
        }

        id = generateViewId()
        setBackgroundColor(Color.parseColor("#E0E0E0"))
        addView(postButton)
        addView(refreshContainer)

        val fill = LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT).apply {
            val pad = context.dp2px(8f)
            setPadding(pad, pad, pad, pad)
        }
        setLayoutParams(fill)

        with (ConstraintSet()) {
            clone(this@MainView)

            val margin = context.dp2px(16f)
            connect(postButton.id, ConstraintSet.BOTTOM, id, ConstraintSet.BOTTOM, margin)
            connect(postButton.id, ConstraintSet.END, id, ConstraintSet.END, margin)

            applyTo(this@MainView)
        }
    }
}