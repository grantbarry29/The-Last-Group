package edu.umich.jakoba.kotlinChatter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.text.TextUtils
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

class MapInfoWindow(context: Context): LinearLayout(context) {
    val titleTextView: TextView
    val snippetTextView: TextView

    init {
        titleTextView = TextView(context).apply {
            textSize = 14.0f
            setHorizontalGravity(Gravity.CENTER)
            ellipsize = TextUtils.TruncateAt.END
            maxLines = 1
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(ColorStateList.valueOf(Color.parseColor("#0000FF")))
        }

        snippetTextView = TextView(context).apply {
            textSize = 14.0f
            ellipsize = TextUtils.TruncateAt.END
            maxLines = 10
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(ColorStateList.valueOf(Color.parseColor("#000000")))
        }

        addView(titleTextView)
        addView(snippetTextView)

        val fill = LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT).apply {
            val pad = context.dp2px(3.64f)
            setPadding(pad, pad, pad, pad)
            orientation = VERTICAL
        }
        setLayoutParams(fill)
    }
}