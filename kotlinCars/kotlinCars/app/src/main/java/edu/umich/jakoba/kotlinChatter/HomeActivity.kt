package edu.umich.jakoba.kotlinChatter

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import edu.umich.jakoba.kotlinChatter.databinding.ActivityHomeBinding
import androidx.core.view.isInvisible


class HomeActivity: AppCompatActivity() {

    private val viewState: HomeViewState by viewModels()
    private lateinit var forCropResult: ActivityResultLauncher<Intent>
    private lateinit var forCameraResult: ActivityResultLauncher<Uri>
    private lateinit var view: ActivityHomeBinding



    // Move to jakob's page
    private fun toSelection(view: View?) {
        var intent = Intent(this, SuggestActivity::class.java)
        intent.data = viewState.imageUri
        startActivity(intent)
    }

    // Disable button if no permission granted
    private fun markButtonDisable(button: ImageButton) {
        button?.isEnabled = false
        button?.alpha = 0.8f
    }

    // Enable continue button
    private fun disableIdentifyButton() {
        view.identifyButton?.isEnabled = false
        view.identifyButton?.alpha = 0.8f
    }

    private fun enableIdentifyButton () {
        view.identifyButton?.isEnabled = true
        view.identifyButton?.alpha = 1f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            view = ActivityHomeBinding.inflate(layoutInflater)
            view.root.setBackgroundColor(Color.parseColor("#E0E0E0"))
            setContentView(view.root)
            disableIdentifyButton()


            if (intent.data != null){
                viewState.imageUri = intent.data
                viewState.imageUri?.let { view.previewImage.display(it) }
                view.noImageText.isInvisible = true
                enableIdentifyButton()
            }

            // Add logo to action bar
            this.supportActionBar?.setDisplayShowHomeEnabled(true)
            this.supportActionBar?.setLogo(R.drawable.logo1)
            this.supportActionBar?.setDisplayUseLogoEnabled(true)
            this.supportActionBar?.setDisplayShowTitleEnabled(true)



            // Listener for moving to Jakob's page
            view.identifyButton.setOnClickListener {
                if (view.noImageText.isInvisible) {
                    toSelection(view.root)
                }

            }

            // Permissions request
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
                results.forEach {
                    if (!it.value) {
                        //toast("${it.key} access denied")
                        if (it.key == Manifest.permission.CAMERA) {
                            markButtonDisable(view.cameraButton)
                        }
                        if (it.key == Manifest.permission.READ_EXTERNAL_STORAGE) {
                            markButtonDisable(view.cameraButton)
                        }
                        //finish()
                    }
                }
            }.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )

            // Select image from album
            val cropIntent = initCropIntent()
            val forPickedResult =
                registerForActivityResult(ActivityResultContracts.GetContent(), fun(uri: Uri?) {
                    uri?.let {

                            val inStream = contentResolver.openInputStream(it) ?: return
                            viewState.imageUri = mediaStoreAlloc("image/jpeg")
                            viewState.imageUri?.let {
                                val outStream = contentResolver.openOutputStream(it) ?: return
                                val buffer = ByteArray(8192)
                                var read: Int
                                while (inStream.read(buffer).also { read = it } != -1) {
                                    outStream.write(buffer, 0, read)
                                }
                                outStream.flush()
                                outStream.close()
                                inStream.close()
                            }

                            doCrop(cropIntent)

                    } ?: run { Log.d("Pick media", "failed") }
                })
            // Set album button listener
            // view.albumButton.setBackgroundColor(Color.parseColor("#6200EE"))
            view.albumButton.setOnClickListener {
                forPickedResult.launch("*/*")
            }

            // Crop image
            forCropResult =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        result.data?.data.let {
                            viewState.imageUri?.run {
                                if (!toString().contains("ORIGINAL")) {
                                    // delete uncropped photo taken for posting
                                    contentResolver.delete(this, null, null)
                                }
                            }
                            viewState.imageUri = it
                            viewState.imageUri?.let { view.previewImage.display(it) }
                            view.noImageText.isInvisible = true
                            enableIdentifyButton()
                        }
                    }
                }


            if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                toast("Device has no camera!")
                return
            }

            // Capture image from camera
            val forCameraResult =
                registerForActivityResult(ActivityResultContracts.TakePicture())
                { success ->
                    if (success) {

                        doCrop(cropIntent)
                        //viewState.imageUri?.let { view.previewImage.display(it) }
                    } else {
                        Log.d("TakePicture", "failed")
                    }
                }
            // Set camera button listener to camera result
            view.cameraButton.setOnClickListener {
                viewState.imageUri = mediaStoreAlloc("image/jpeg")
                forCameraResult.launch(viewState.imageUri)
            }
        }


    // For storing images
    private fun mediaStoreAlloc(mediaType: String): Uri? {
        val values = ContentValues()
        values.put(MediaStore.MediaColumns.MIME_TYPE, mediaType)
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)

        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }

    private fun initCropIntent(): Intent? {
        // Is there any published Activity on device to do image cropping?
        val intent = Intent("com.android.camera.action.CROP")
        intent.type = "image/*"
        val listofCroppers = packageManager.queryIntentActivities(intent, 0)
        // No image cropping Activity published
        if (listofCroppers.size == 0) {
            toast("Device does not support image cropping")
            return null
        }

        intent.component = ComponentName(
            listofCroppers[0].activityInfo.packageName,
            listofCroppers[0].activityInfo.name)

        // create a square crop box:
        intent.putExtra("outputX", 500)
            .putExtra("outputY", 500)
            .putExtra("aspectX", 1)
            .putExtra("aspectY", 1)
            // enable zoom and crop
            .putExtra("scale", true)
            .putExtra("crop", true)
            .putExtra("return-data", true)

        return intent
    }

    private fun doCrop(intent: Intent?) {
     /*  intent ?: run {
            // viewState.imageUri?.let { view.previewImage.display(it) }
            return
        }*/

        viewState.imageUri?.let {
            intent?.data = it
            forCropResult.launch(intent)
        }
    }
}

class HomeViewState: ViewModel() {
    var imageUri: Uri? = null
}