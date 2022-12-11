package kr.ac.kumoh.s.weatherable.rate

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import kr.ac.kumoh.s.weatherable.MySingleton
import kr.ac.kumoh.s.weatherable.SERVER_URL
import org.json.JSONArray
import org.json.JSONObject

class SurveySunnyViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val QUEUE_TAG = "VolleyRequest"
    }

    data class SurveySunny(var id: Int, var name: String)

    private var mQueue: RequestQueue
    val survey_sunny_list = MutableLiveData<ArrayList<SurveySunny>>()
    private val survey_sunny_data = ArrayList<SurveySunny>()

    init {
        survey_sunny_list.value = survey_sunny_data
        mQueue = MySingleton.getInstance(application).requestQueue
        requestSurveySunny()
    }

    fun requestSurveySunny() {
        val request = JsonArrayRequest(
            Request.Method.GET,
            "${SERVER_URL().url}/places/tag/rate", // SERVER_URL에 테이블 이름을 붙여준다.
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
            val id = item.getInt("id")
            val name = item.getString("title")

            survey_sunny_data.add(SurveySunny(id, name))
        }
    }
}