package edu.umich.jakoba.kotlinChatter

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import coil.load
import edu.umich.jakoba.kotlinChatter.databinding.ActivityDescriptionBinding

class DescriptionActivity: AppCompatActivity() {

    private val viewState: DescriptionViewState by viewModels()
    private lateinit var view: ActivityDescriptionBinding

    // Move to home page and restart
    private fun toHome() {
        var intent = Intent(this, HomeActivity::class.java)
        intent.data = null
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = ActivityDescriptionBinding.inflate(layoutInflater)
        view.root.setBackgroundColor(Color.parseColor("#E0E0E0"))
        setContentView(view.root)

        // remove back arrow
        this.supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // Add logo to action bar
        this.supportActionBar?.setDisplayShowHomeEnabled(true)
        this.supportActionBar?.setLogo(R.drawable.logo1)
        this.supportActionBar?.setDisplayUseLogoEnabled(true)

        // set preview image
        viewState.imageUri = intent.data
        Log.e("descr uri:", viewState.imageUri.toString())
            view.carImage.load(viewState.imageUri){
                crossfade(true)
                crossfade(1000)
                view.carImage.setVisibility(View.VISIBLE)
            }

        // set vehicle values
        viewState.carCost = intent.getStringExtra("carCost")
        view.carCostText.text = viewState.carCost
        Log.e("car_cost", viewState.carCost.toString())
        viewState.carName = intent.getStringExtra("carName")
        view.carMakeText.text = viewState.carName
        viewState.probability = intent.getStringExtra("probability")
        view.probabilityText.text = viewState.probability + "% probability"

        // Return to home page and clear image
        view.retakeButton.setOnClickListener {
            toHome()
        }

        view.backButton.setOnClickListener {
            finish()
        }
    }

}

class DescriptionViewState: ViewModel() {
    var imageUri: Uri? = null
    var carName: String? = null
    var carCost: String? = null
    var probability: String? = null
}