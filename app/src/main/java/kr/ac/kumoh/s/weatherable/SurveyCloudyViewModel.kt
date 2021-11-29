package kr.ac.kumoh.s.weatherable

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import org.json.JSONArray
import org.json.JSONObject


class SurveyCloudyViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val QUEUE_TAG = "VolleyRequest"
        const val SERVER_URL = "https://weatherable-flask-lhavr.run.goorm.io"
        // "http://192.168.200.176:8080"
        // http://192.168.0.11:8080
    }

    data class SurveyCloudy(var id: Int, var name: String)

    private var mQueue: RequestQueue
    val survey_cloudy_list = MutableLiveData<ArrayList<SurveyCloudy>>()
    private val survey_cloudy_data = ArrayList<SurveyCloudy>()

    init {
        survey_cloudy_list.value = survey_cloudy_data
        mQueue = MySingleton.getInstance(application).requestQueue
        requestSurveyCloudy()
    }

    fun requestSurveyCloudy() {
        val request = JsonArrayRequest(
            Request.Method.GET,
            "$SERVER_URL/tag_place", // SERVER_URL에 테이블 이름을 붙여준다.
            null,
            {
                survey_cloudy_data.clear()
                parseJson(it)
                survey_cloudy_list.value = survey_cloudy_data
            },
            { Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show() }
        )

        request.tag = QUEUE_TAG
        mQueue.add(request)
    }

    fun getSurveyCloudy(i: Int) = survey_cloudy_data[i]

    fun getSize() = survey_cloudy_data.size

    override fun onCleared() {
        super.onCleared()
        mQueue.cancelAll(QUEUE_TAG)
    }

    private fun parseJson(items: JSONArray) {
        for(i in 0 until items.length()) {
            val item: JSONObject = items[i] as JSONObject
            val id = item.getInt("id")
            val name = item.getString("title")

            survey_cloudy_data.add(SurveyCloudy(id, name))
        }
    }
}