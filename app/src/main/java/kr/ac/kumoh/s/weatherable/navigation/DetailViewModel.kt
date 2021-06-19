package kr.ac.kumoh.s.weatherable.navigation

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonArrayRequest
import kr.ac.kumoh.s.weatherable.MySingleton
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.sql.Timestamp

class DetailViewModel (application: Application) : AndroidViewModel(application) {

    data class review(var explain: String, var imageUrl: String, var timestamp: String, var uid: String, var userId: String)
    companion object {
        const val QUEUE_TAG = "VolleyRequest"
        const val SERVER_URL = "http://192.168.35.120:8080"
    }

    private var mQueue: RequestQueue
    var mLoader: ImageLoader
    val list = MutableLiveData<ArrayList<review>>()
    private val data = ArrayList<review>()

    init {
        list.value = data
        mQueue = MySingleton.getInstance(application).requestQueue
        mLoader = MySingleton.getInstance(application).imageLoader
        getJSON()
    }

    fun getImageUrl(i: Int): String = "$SERVER_URL/image/" + URLEncoder.encode(data[i].imageUrl, "utf-8")

    fun getJSON() {
        val request = JsonArrayRequest(
            Request.Method.GET,
            "$SERVER_URL/table?name=mainCharacter",
            null,
            {
                //Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show()
                data.clear()
                parseJson(it)
                list.value = data
            },
            {
                Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show()
            }
        )

        request.tag = QUEUE_TAG
        mQueue.add(request)
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
            val explain = item.getString("explain")
            val imageUrl = item.getString("imageUrl")
            val timestamp = item.getString("timestamp")
            val uid = item.getString("uid")
            val userId = item.getString("userId")

            data.add(review(explain, imageUrl, timestamp, uid, userId))
        }
    }
}