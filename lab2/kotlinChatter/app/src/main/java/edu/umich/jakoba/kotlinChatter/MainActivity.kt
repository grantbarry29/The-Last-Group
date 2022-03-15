package edu.umich.jakoba.kotlinChatter

import android.Manifest
import edu.umich.jakoba.kotlinChatter.ChattStore.getChatts
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.maps.SupportMapFragment
import edu.umich.jakoba.kotlinChatter.ChattStore.chatts

class MainActivity: AppCompatActivity() {
    private lateinit var chattListAdapter: ChattListAdapter
    private lateinit var view: MainView
    private var xdown: Float = 0f
    private var ydown: Float = 0f

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        super.dispatchTouchEvent(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                xdown = event.x
                ydown = event.y
            }
            MotionEvent.ACTION_UP -> {
                if ((xdown - event.x) > 100 && kotlin.math.abs(event.y - ydown) < 100) {
                    startActivity(Intent(this, MapsActivity::class.java))
                }
            }
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        view = MainView(this)

        chatts.value?.let {
            chattListAdapter = ChattListAdapter(this, it)
        } ?: run {
            toast("Null chatts array!", false)
            finish()
        }
        view.chattListView.setAdapter(chattListAdapter)

        chatts.observe(this) {
            chattListAdapter.notifyDataSetChanged()
        }

        view.postButton.setOnClickListener {
            startActivity(Intent(this, PostActivity::class.java))
        }

        view.refreshContainer.setOnRefreshListener { refreshTimeline() }

        setContentView(view)

        getChatts(applicationContext)

        val contract = ActivityResultContracts.RequestMultiplePermissions()
        val launcher = registerForActivityResult(contract) { results ->
            results.forEach { result ->
                if (!result.value) {
                    toast("fine location access denied")
                    finish()
                }
            }
        }
        launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
    }

    private fun refreshTimeline() {
        getChatts(applicationContext)

        // stop the refreshing animation upon completion:
        view.refreshContainer.isRefreshing = false
    }

    fun startPost(view: View?) = startActivity(Intent(this, PostActivity::class.java))
}