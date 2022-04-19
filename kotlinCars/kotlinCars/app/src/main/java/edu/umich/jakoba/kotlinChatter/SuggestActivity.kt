package edu.umich.jakoba.kotlinChatter

import AdapterSuggestion
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import edu.umich.jakoba.kotlinChatter.databinding.ActivitySuggestBinding
import SuggestionStore
import SuggestionStore.suggestions
import android.util.Log
import android.widget.Toast
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList

class SuggestActivity: AppCompatActivity() {

    private val viewState: SuggestViewState by viewModels()
    private lateinit var forCropResult: ActivityResultLauncher<Intent>
    private lateinit var forCameraResult: ActivityResultLauncher<Uri>
    private lateinit var view: ActivitySuggestBinding
    private lateinit var SuggestionListAdapter: AdapterSuggestion
    private val suggestionIntents: ArrayList<Intent>? = null

    // Move to home page and send image back
    private fun toHome(view: View?) {
        var intent = Intent(this, HomeActivity::class.java)
        intent.data = viewState.imageUri
        startActivity(intent)
    }

    private fun retry(view: View?) {
        var intent = Intent(this, SuggestActivity::class.java)
        intent.data = viewState.imageUri
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = ActivitySuggestBinding.inflate(layoutInflater)
        view.root.setBackgroundColor(Color.parseColor("#E0E0E0"))
        setContentView(view.root)

        // set preview image
        viewState.imageUri = intent.data
        viewState.imageUri?.let { view.userPhoto.display(it) }

        // remove back arrow
        this.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        this.supportActionBar?.setDisplayShowHomeEnabled(true)
        this.supportActionBar?.setLogo(R.drawable.logo1)
        this.supportActionBar?.setDisplayUseLogoEnabled(true)


        // Return to home page and clear image
        view.retakeButton.setOnClickListener {
            viewState.imageUri = null
            toHome(view.root)
        }

        // Return to home page with image
        view.backButton.setOnClickListener {
            toHome(view.root)
        }

        view.retryButton.setOnClickListener {
            retry(view.root)
        }
        SuggestionListAdapter = AdapterSuggestion(this@SuggestActivity, suggestions)
        view.suggestionListView.adapter = SuggestionListAdapter
        suggestions.addOnListChangedCallback(propertyObserver)



        // Actual API call returns list of suggestions
        var msg = "Identifying vehicle..."
        SuggestionStore.postCar(applicationContext, viewState.imageUri) {
            runOnUiThread {
                toast(msg)
            }
        }

        // Set list view on click to go to description page
        view.suggestionListView.setOnItemClickListener{ parent, view, position, id ->
            // clicked item
            val selectedItem = parent.getItemAtPosition(position)
            // create new description intent
            var intent = Intent(this, DescriptionActivity::class.java)
            var suggestion = suggestions[position]

            // Set data to send to intent
            intent.data = Uri.parse(suggestion?.carImageUri)
            intent.putExtra("carName", suggestion?.carName)
            intent.putExtra("carCost", suggestion?.carCost)
            Log.e("car_cost", suggestion?.carCost.toString())
            intent.putExtra("probability", suggestion?.probability)

            startActivity(intent)
        }

    }


    // Property observer to check for api replies
    private val propertyObserver = object: ObservableList.OnListChangedCallback<ObservableArrayList<Int>>() {
        override fun onChanged(sender: ObservableArrayList<Int>?) { }
        override fun onItemRangeChanged(sender: ObservableArrayList<Int>?, positionStart: Int, itemCount: Int) { }
        override fun onItemRangeInserted(
            sender: ObservableArrayList<Int>?,
            positionStart: Int,
            itemCount: Int
        ) {
            runOnUiThread {
                Log.e("observer", "hey there")
                SuggestionListAdapter.setInvis(view.loadingPanel)
                SuggestionListAdapter.notifyDataSetChanged()
                if (suggestions.size == 0) {
                    val duration = Toast.LENGTH_LONG
                    val text = "We are experiencing Heavy traffic. Please wait 30 seconds and try again."
                    val toast = Toast.makeText(applicationContext, text, duration)
                    toast.show()
                }
            }
        }
        override fun onItemRangeMoved(sender: ObservableArrayList<Int>?, fromPosition: Int, toPosition: Int,
                                      itemCount: Int) { }
        override fun onItemRangeRemoved(sender: ObservableArrayList<Int>?, positionStart: Int, itemCount: Int) { }
    }

    override fun onDestroy() {
        super.onDestroy()

        suggestions.removeOnListChangedCallback(propertyObserver)
    }


}

class SuggestViewState: ViewModel() {
    var imageUri: Uri? = null
}

