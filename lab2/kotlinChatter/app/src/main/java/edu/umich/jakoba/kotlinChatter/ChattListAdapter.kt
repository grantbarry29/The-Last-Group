package edu.umich.jakoba.kotlinChatter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

class ChattListAdapter(context: Context, users: ArrayList<Chatt?>) :
    ArrayAdapter<Chatt?>(context, 0, users) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val listItemView = convertView as? ChattListItem ?: ChattListItem(context)

        listItemView.setBackgroundColor(Color.parseColor(if (position % 2 == 0) "#E0E0E0" else "#EEEEEE"))

        getItem(position)?.run {
            listItemView.usernameTextView.text = username
            listItemView.messageTextView.text = message
            listItemView.timestampTextView.text = timestamp
            geodata?.let {
                listItemView.geodataTextView.text =
                    "Posted from ${it.loc}, while facing ${it.facing} moving at ${it.speed} speed."

                listItemView.mapButton.visibility = View.VISIBLE
                listItemView.mapButton.setOnClickListener { v ->
                    if (v.id == listItemView.mapButton.id) {
                        val intent = Intent(context, MapsActivity::class.java)
                        intent.putExtra("INDEX", position)
                        context.startActivity(intent)
                    }
                }
            } ?: run {
                listItemView.geodataTextView.text = ""
                listItemView.mapButton.visibility = View.INVISIBLE
                listItemView.mapButton.setOnClickListener(null)
            }
        }


        return listItemView
    }
}