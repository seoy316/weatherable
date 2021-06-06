package kr.ac.kumoh.s20170998.weatherable

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonArrayRequest
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder

class SurveySunnyViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val QUEUE_TAG = "VolleyRequest"
        const val SERVER_URL = "http://192.168.228.250:8080"
    }

    data class SurveySunny(var name: String, var image: String)

    private var mQueue: RequestQueue
    var mLoader: ImageLoader
    val survey_sunny_list = MutableLiveData<ArrayList<SurveySunny>>()
    private val survey_sunny_data = ArrayList<SurveySunny>()
    init {
        survey_sunny_list.value = survey_sunny_data
        mQueue = MySingleton.getInstance(application).requestQueue
        mLoader = MySingleton.getInstance(application).imageLoader
        requestSurveySunny()
    }

    fun getImageUrl(i: Int): String = "$SERVER_URL/image/" + URLEncoder.encode(survey_sunny_data[i].image, "utf-8")

    fun requestSurveySunny() {
        val request = JsonArrayRequest(
            Request.Method.GET,
            SERVER_URL+"/sunny", // SERVER_URL에 테이블 이름을 붙여준다.
            null,
            {
                survey_sunny_data.clear()
                parseJson(it)
                survey_sunny_list.value = survey_sunny_data
            },
            { Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show() }
        )

        request.tag = QUEUE_TAG
        mQueue.add(request)
    }

    fun getSurveySunny(i: Int) = survey_sunny_data[i]

    fun getSize() = survey_sunny_data.size

    override fun onCleared() {
        super.onCleared()
        mQueue.cancelAll(QUEUE_TAG)
    }

    private fun parseJson(items: JSONArray) {
        for(i in 0 until items.length()) {
            val item: JSONObject = items[i] as JSONObject
            val name = item.getString("name")
            val image = item.getString("image")

            survey_sunny_data.add(SurveySunny(name, image))
        }
    }
}