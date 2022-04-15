import SuggestionStore.suggestions
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat.startActivity
import coil.load
import edu.umich.jakoba.kotlinChatter.DescriptionActivity
import edu.umich.jakoba.kotlinChatter.databinding.ListitemSuggestionBinding

class AdapterSuggestion(context: Context, suggestions: ArrayList<Suggestion?>) :
    ArrayAdapter<Suggestion?>(context, 0, suggestions) {

    fun setInvis(layout: RelativeLayout) {
        layout.visibility = GONE
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val listItemView = (convertView?.tag /* reuse binding */ ?: run {
            val rowView = LayoutInflater.from(context).inflate(edu.umich.jakoba.kotlinChatter.R.layout.listitem_suggestion, parent, false)
            rowView.tag = ListitemSuggestionBinding.bind(rowView) // cache binding
            rowView.tag
        }) as ListitemSuggestionBinding

        getItem(position)?.run {

            // set text
            listItemView.suggestionText.text = carName

            // set background border
            // set swapping background color
            listItemView.root.setBackgroundColor(Color.parseColor(if (position % 2 == 0) "#E2E2E2" else "#D9D9D9"))
            listItemView.root.clipToOutline = true

            // show image
            carImageUri?.let {
                listItemView.suggestionImage.load(it){
                    crossfade(true)
                    crossfade(1000)
                    listItemView.suggestionImage.setVisibility(View.VISIBLE)
                }
            } ?: run {
                listItemView.suggestionImage.setVisibility(View.GONE)
                listItemView.suggestionImage.setImageBitmap(null)
            }

            // Set on click listener
            listItemView.layout.setOnClickListener{ parent ->

                    // create new description intent
                    var intent = Intent(parent.context, DescriptionActivity::class.java)
                    var suggestion = suggestions[position]

                    // Set data to send to intent
                   // intent.data = Uri.parse(suggestion?.carImageUri)
                    intent.putExtra("carName", suggestion?.carName)
                    intent.putExtra("probability", suggestion?.probability)

                    startActivity(parent.context,intent, Bundle())

            }

        }

        return listItemView.root
    }
}