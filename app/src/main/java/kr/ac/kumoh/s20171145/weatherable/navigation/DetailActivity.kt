package kr.ac.kumoh.s20171145.weatherable.navigation

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.NetworkImageView
import kr.ac.kumoh.s20171145.weatherable.MySingleton
import kr.ac.kumoh.s20171145.weatherable.R
import org.json.JSONArray
import org.json.JSONObject

class DetailActivity: AppCompatActivity() {

    companion object {
        const val QUEUE_TAG = "DetailRequest"
        const val SERVER_URL = "https://weatherable-flask-lhavr.run.goorm.io"
    }

    private lateinit var mQueue: RequestQueue
    private  lateinit var mLoader: ImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        mQueue = MySingleton.getInstance(application).requestQueue
        mLoader = MySingleton.getInstance(application).imageLoader
        val postId : String? = intent.getStringExtra(GridFragment.KEY_ID)
//        getReview('"'+ postId.toString() +'"')
        getReview(postId)
        print("id value ${postId.toString()}")

    }
    override fun onStop(){
        super.onStop()
        mQueue.cancelAll(QUEUE_TAG)
    }

    fun getReview(postId: String?) {
        val request = JsonArrayRequest(
            Request.Method.GET,
            "$SERVER_URL/reviews_get/postId?id=$postId",
            null,
            { parseReview(it) },
            { Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show() }
        )
        request.tag = QUEUE_TAG
        mQueue.add(request)
    }

    private fun parseReview(items: JSONArray) {
        val item: JSONObject = items[0] as JSONObject
        val time_ = item.getString("time_")
        val image = item.getString("image")
        val weather = item.getString("weather")
        val content = item.getString("content")
        val place = item.getString("place")

        val imageView = findViewById<NetworkImageView>(R.id.detailviewitem_imageview_content)
        imageView.setImageUrl(image, mLoader)
        val contentView = findViewById<TextView>(R.id.detailviewitem_explain_textview)
        contentView.text = content
        val timeView = findViewById<TextView>(R.id.detailviewitem_dateTime)
        timeView.text = time_
        val weatherView = findViewById<TextView>(R.id.detailviewitem_Weather)
        weatherView.text = weather
        val placeView = findViewById<TextView>(R.id.detailviewitem_place)
        placeView.text = place

    }
}