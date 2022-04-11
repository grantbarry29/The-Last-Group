package edu.umich.jakoba.kotlinChatter

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
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
        viewState.imageUri?.let { view.carImage.display(it) }

        // set vehicle values
        viewState.carCost = intent.getStringExtra("carCost")
        view.carCostText.text = viewState.carCost
        viewState.carMake = intent.getStringExtra("carMake")
        view.carMakeText.text = viewState.carMake
        viewState.carModel = intent.getStringExtra("carModel")
        view.carModelText.text = viewState.carModel
        viewState.carYear = intent.getStringExtra("carYear")
        view.carYearText.text = viewState.carYear

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
    var carImageUri: Uri? = null
    var carMake: String? = null
    var carModel: String? = null
    var carYear: String? = null
    var carCost: String? = null
}