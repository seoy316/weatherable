package kr.ac.kumoh.s.weatherable.navigation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.StringRequest
import kr.ac.kumoh.s.weatherable.MainActivity.Companion.SERVER_URL
import kr.ac.kumoh.s.weatherable.MainActivity.Companion.uid
import kr.ac.kumoh.s.weatherable.MySingleton
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.URLEncoder
import java.util.HashMap


class DetailViewModel (application: Application) : AndroidViewModel(application) {

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
        getJSON()
    }

    fun getImageUrl(i: Int): String = data[i].image

    fun getJSON() {
        val request: StringRequest = object : StringRequest(
            Method.POST,
            "$SERVER_URL/reviews_get",
            Response.Listener { response ->
                try {
                    println("연결 성공?")
                    val items = JSONArray(response)
                    for (i in 0 until items.length()) {
                        val item: JSONObject = items[i] as JSONObject
                        val time_ = item.getString("time_")
                        val image = item.getString("image")
                        val weather = item.getString("weather")
                        val content = item.getString("content")
                        val place = item.getString("place")
                        val postId = item.getString("postId")
                        println("$place")

                        data.add(review(content, image, time_, weather, place, postId))
                    }
                    println("출력 성공?")

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                println("실패")
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()

                params.put("uid", uid.toString())
                print("params $params")
                return params
            }
        }
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