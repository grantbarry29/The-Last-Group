import android.content.Context
import android.net.Uri
import android.util.Log
import okhttp3.*
import okio.IOException
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import edu.umich.jakoba.kotlinChatter.toFile
import edu.umich.jakoba.kotlinChatter.toast
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import kotlin.reflect.full.declaredMemberProperties

object SuggestionStore {
private const val serverUrl = "https://35.185.48.28/"
private val nFields = Suggestion::class.declaredMemberProperties.size
fun postCar(context: Context, imageUri: Uri?,
            completion: (String) -> Unit) {

    val cars: ArrayList<Suggestion?> = ArrayList()
    val client = OkHttpClient()

    // Add image to post form
    val mpFD = MultipartBody.Builder().setType(MultipartBody.FORM)
    imageUri?.run {
        toFile(context)?.let {
            mpFD.addFormDataPart(
                "image", "carImage",
                it.asRequestBody("image/jpeg".toMediaType())
            )
        } ?: context.toast("Unsupported image format")
    }


    val request = Request.Builder()
        .url(serverUrl + "postCars/")
        .post(mpFD.build())
        .build()


    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("getCars", "Failed GET request")
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                val carsReceived = try {
                    JSONObject(response.body?.string() ?: "").getJSONArray("cars")
                } catch (e: JSONException) {
                    JSONArray()
                }

                cars.clear()
                for (i in 0 until carsReceived.length()) {
                    val carEntry = carsReceived[i] as JSONArray
                    if (carEntry.length() == nFields) {
                        var newSuggestion = Suggestion()
                        newSuggestion.carImageUri = Uri.parse(carEntry[0].toString())
                        newSuggestion.carMake = carEntry[1].toString()
                        newSuggestion.carModel = carEntry[2].toString()
                        newSuggestion.carYear = carEntry[3].toString()
                        newSuggestion.carCost = carEntry[4].toString()
                        cars.add(newSuggestion)
                    } else {
                        Log.e("get cars",
                            "Received unexpected number of fields " + carEntry.length()
                                .toString() + " instead of " + nFields.toString()
                        )
                    }
                }
            }
        }
    })
}
}