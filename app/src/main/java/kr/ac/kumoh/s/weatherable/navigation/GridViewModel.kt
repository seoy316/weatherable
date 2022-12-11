package kr.ac.kumoh.s.weatherable.navigation

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import kr.ac.kumoh.s.weatherable.MainActivity.Companion.uid
import kr.ac.kumoh.s.weatherable.MySingleton
import kr.ac.kumoh.s.weatherable.SERVER_URL
import kr.ac.kumoh.s.weatherable.navigation.GridFragment.Companion.KEY_ID
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap


class GridViewModel (application: Application) : AndroidViewModel(application) {

    data class review(var content: String, var image: String, var time_: String,
                      var weather: String, var place: String, var postId: String)
    companion object {
        const val QUEUE_TAG = "VolleyRequest"
    }

    private var mQueue: RequestQueue
    var mLoader: ImageLoader
    val list = MutableLiveData<ArrayList<review>>()
    private val data = ArrayList<review>()

    init {
        list.value = data
        mQueue = MySingleton.getInstance(application).requestQueue
        mLoader = MySingleton.getInstance(application).imageLoader
        getJSON(uid)
    }

    fun getImageUrl(i: Int): String = data[i].image

    fun getJSON(uid: String?) {
        val request = JsonArrayRequest(
            Request.Method.GET,
            "${SERVER_URL().url}/story/read/uid?id=$uid",
            null,
            { parseJson(it) },
            {Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show() }
        )
        request.setShouldCache(false)
        mQueue!!.add(request)
    }

    fun getReview(i: Int) = data[i]
    fun getSize() = data.size

    override fun onCleared() {
        super.onCleared()
        mQueue.cancelAll(QUEUE_TAG)
    }

    private fun parseJson(items: JSONArray) {
        for (i in 0 until items.length()) {
            val item: JSONObject = items[i] as JSONObject
            val time_ = item.getString("time_")
            val image = item.getString("image")
            val weather = item.getString("weather")
            val content = item.getString("content")
            val place = item.getString("place")
            val postId = item.getString("postId")

            data.add(review(content, image, time_, weather, place, postId))
        }
    }
}