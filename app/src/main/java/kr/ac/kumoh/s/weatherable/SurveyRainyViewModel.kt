package kr.ac.kumoh.s.weatherable

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

class SurveyRainyViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val QUEUE_TAG = "VolleyRequest"
        const val SERVER_URL = "http://192.168.228.250:8080"
        // "http://192.168.200.176:8080"
        // http://192.168.0.11:8080
    }

    data class SurveyRainy(var name: String, var image: String)

    private var mQueue: RequestQueue
    var mLoader: ImageLoader
    val survey_rainy_list = MutableLiveData<ArrayList<SurveyRainy>>()
    private val survey_rainy_data = ArrayList<SurveyRainy>()
    init {
        survey_rainy_list.value = survey_rainy_data
        mQueue = MySingleton.getInstance(application).requestQueue
        mLoader = MySingleton.getInstance(application).imageLoader
        requestSurveyRainy()
    }

    fun getImageUrl(i: Int): String = "$SERVER_URL/image/" + URLEncoder.encode(survey_rainy_data[i].image, "utf-8")

    fun requestSurveyRainy() {
        val request = JsonArrayRequest(
            Request.Method.GET,
            SERVER_URL+"/rainy", // SERVER_URL에 테이블 이름을 붙여준다.
            null,
            {
                survey_rainy_data.clear()
                parseJson(it)
                survey_rainy_list.value = survey_rainy_data
            },
            { Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show() }
        )

        request.tag = QUEUE_TAG
        mQueue.add(request)
    }

    fun getSurveyRainy(i: Int) = survey_rainy_data[i]

    fun getSize() = survey_rainy_data.size

    override fun onCleared() {
        super.onCleared()
        mQueue.cancelAll(QUEUE_TAG)
    }

    private fun parseJson(items: JSONArray) {
        for(i in 0 until items.length()) {
            val item: JSONObject = items[i] as JSONObject
            val name = item.getString("name")
            val image = item.getString("image")

            survey_rainy_data.add(SurveyRainy(name, image))
        }
    }
}