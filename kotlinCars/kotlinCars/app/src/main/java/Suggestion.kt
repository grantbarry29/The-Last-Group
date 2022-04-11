import android.net.Uri

import android.view.ViewGroup

import android.view.LayoutInflater

import android.content.Context
import android.graphics.Color
import android.view.View

import android.widget.ArrayAdapter
import coil.load
import edu.umich.jakoba.kotlinChatter.R

import edu.umich.jakoba.kotlinChatter.databinding.ListitemSuggestionBinding


class Suggestion {

    var carImageUri: Uri? = null
    var carMake: String? = null
    var carModel: String? = null
    var carYear: String? = null
    var carCost: String? = null

}

class AdapterSuggestion(context: Context, suggestions: ArrayList<Suggestion?>) :
    ArrayAdapter<Suggestion?>(context, 0, suggestions) {



    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val listItemView = (convertView?.tag /* reuse binding */ ?: run {
            val rowView = LayoutInflater.from(context).inflate(edu.umich.jakoba.kotlinChatter.R.layout.listitem_suggestion, parent, false)
            rowView.tag = ListitemSuggestionBinding.bind(rowView) // cache binding
            rowView.tag
        }) as ListitemSuggestionBinding

        getItem(position)?.run {

            // set text
            listItemView.suggestionText.text = carYear + " " + carMake + " " + carModel

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

        }

        return listItemView.root
    }
}


