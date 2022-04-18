import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.databinding.ObservableArrayList
import okhttp3.*
import okio.IOException
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import edu.umich.jakoba.kotlinChatter.toFile
import edu.umich.jakoba.kotlinChatter.toast
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt
import kotlin.reflect.full.declaredMemberProperties

object SuggestionStore {
private const val serverUrl = "https://18.218.44.128/"
private val nFields = Suggestion::class.declaredMemberProperties.size
    val suggestions = ObservableArrayList<Suggestion?>()
fun postCar(context: Context, imageUri: Uri?,
            completion: (String) -> Unit) {

    suggestions.clear()
    val client = OkHttpClient().newBuilder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // Add image to post form
    val mpFD = MultipartBody.Builder().setType(MultipartBody.FORM)
    imageUri?.run {
        toFile(context)?.let {
            mpFD.addFormDataPart(
                "image", "image",
                it.asRequestBody("image/jpeg".toMediaType())
            )
        } ?: context.toast("Unsupported image format")
    }


    val request = Request.Builder()
        .url(serverUrl + "postcar/")
        .post(mpFD.build())
        .build()

    Log.e("request", request.toString())

    client.newCall(request).enqueue(object : Callback {

        override fun onFailure(call: Call, e: IOException) {
            Log.e("postcar", "Failed POST request")
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {

                val jsonResponse = JSONObject(response.body?.string() ?: "")
                Log.e("response", jsonResponse.toString())

                val carsReceived = try {
                    jsonResponse.getJSONArray("text")
                } catch (e: JSONException) {
                    JSONArray()
                }
               val pricesReceived = try {
                   jsonResponse.getJSONArray("prices")
                } catch (e: JSONException) {
                    JSONArray()
                }
                val imagesReceived = try {
                    jsonResponse.getJSONArray("image")
                } catch (e: JSONException) {
                    JSONArray()
                }
                Log.e("cars: ", carsReceived.toString())
                Log.e("prices: ", pricesReceived.toString())
                Log.e("images: ", imagesReceived.toString())

                suggestions.clear()

                for (i in 0 until carsReceived.length()) {
                    val carEntry = carsReceived[i]
                    val split = carEntry.toString().split(",")
                    var carName = split[0]
                    var prob = split[1].removeRange(0,5)
                    prob = prob.trimStart()
                    while (prob.length > 7) {
                        prob = prob.dropLast(1)
                    }
                    while (prob.length > 4) {
                        prob = prob.drop(1)
                    }
                    //prob = prob.removeRange(4, prob.length-4)
                    if (prob.get(0) == '0') {
                        prob = ((prob.toDouble() * 100).roundToInt()).toString()
                    } else {
                        prob = "0"
                    }


                    //val carEntry = carsReceived[i] as JSONArray
                    Log.e("car", carEntry.toString())
                    Log.e("carName", carName)
                    Log.e("prob", prob)
                    var newSuggestion = Suggestion()
                    newSuggestion.carName = carName
                    newSuggestion.probability = prob
                    newSuggestion.carCost = pricesReceived[i].toString()
                    newSuggestion.carImageUri = imagesReceived[i].toString().replace("""\/""", "/")
                    Log.e("image", newSuggestion.carImageUri.toString())
                    Log.e("car_cost", newSuggestion.carCost.toString())
                    suggestions.add(newSuggestion)

                }

            }
        }
    })
}
}