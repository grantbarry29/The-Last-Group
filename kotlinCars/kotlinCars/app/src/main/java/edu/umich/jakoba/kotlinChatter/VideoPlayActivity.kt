package edu.umich.jakoba.kotlinChatter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View.inflate
import android.widget.MediaController
import androidx.appcompat.resources.Compatibility.Api21Impl.inflate
import edu.umich.jakoba.kotlinChatter.databinding.ActivityMainBinding.inflate
import edu.umich.jakoba.kotlinChatter.databinding.ActivityVideoPlayBinding

class VideoPlayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.final_screen)


    }
}