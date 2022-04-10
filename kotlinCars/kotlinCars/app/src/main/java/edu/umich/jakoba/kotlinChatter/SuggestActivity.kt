package edu.umich.jakoba.kotlinChatter

import AdapterSuggestion
import android.content.ComponentName
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.lifecycle.ViewModel
import edu.umich.jakoba.kotlinChatter.databinding.ActivityHomeBinding
import Suggestion
import edu.umich.jakoba.kotlinChatter.databinding.ActivitySuggestBinding

class SuggestActivity: AppCompatActivity() {

    private val viewState: SuggestViewState by viewModels()
    private lateinit var forCropResult: ActivityResultLauncher<Intent>
    private lateinit var forCameraResult: ActivityResultLauncher<Uri>
    private lateinit var view: ActivitySuggestBinding
    private val suggestions: ArrayList<Suggestion?> = ArrayList()
    private val suggestionIntents: ArrayList<Intent>? = null

    // Move to home page and send image back
    private fun toHome(view: View?) {
        var intent = Intent(this, HomeActivity::class.java)
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


        // Return to home page and clear image
        view.retakeButton.setOnClickListener {
            viewState.imageUri = null
            toHome(view.root)
        }

        // Return to home page with image
        view.backButton.setOnClickListener {
            toHome(view.root)
        }

        //  *****REPLACE WITH API CALL******
        // Make api call and populate suggestions array
        var i = 0
        while (i < 10 ){
            var tempSuggestion = Suggestion()
            tempSuggestion.carCost = "$25000 - $35000"
            tempSuggestion.carMake = "Tesla"
            tempSuggestion.carModel = "Model 3"
            tempSuggestion.carYear = "2015-2020"
            tempSuggestion.carImageUri = viewState.imageUri
            suggestions?.add(tempSuggestion)
            i++
        }

        // Create intents for each suggestion to switch to
        if (suggestions != null) {
            for (suggestion in suggestions) {
                intent.data = suggestion?.carImageUri
                intent.putExtra("carCost", suggestion?.carCost)
                intent.putExtra("carMake", suggestion?.carMake)
                intent.putExtra("carModel", suggestion?.carModel)
                intent.putExtra("carYear", suggestion?.carYear)
            }
        }

        // Populate list view with suggestions
        view.suggestionListView.adapter = AdapterSuggestion(this@SuggestActivity,  suggestions)

        // Set list view on click to go to description page
        view.suggestionListView.setOnItemClickListener{ parent, view, position, id ->
            // clicked item
            val selectedItem = parent.getItemAtPosition(position)
            // create new description intent
            var intent = Intent(this, DescriptionActivity::class.java)
            var suggestion = suggestions[position]

            // Set data to send to intent
            intent.data = suggestion?.carImageUri
            intent.putExtra("carCost", suggestion?.carCost)
            intent.putExtra("carMake", suggestion?.carMake)
            intent.putExtra("carModel", suggestion?.carModel)
            intent.putExtra("carYear", suggestion?.carYear)

            startActivity(intent)
        }

    }


}

class SuggestViewState: ViewModel() {
    var imageUri: Uri? = null
}

