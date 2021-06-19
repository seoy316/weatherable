import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.NetworkImageView
import kr.ac.kumoh.s.weatherable.MySingleton
import kr.ac.kumoh.s.weatherable.R
import kr.ac.kumoh.s.weatherable.navigation.DetailViewFragment
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder

class DetailActivity: AppCompatActivity() {
    companion object {
        const val QUEUE_TAG = "DetailRequest"
        const val SERVER_URL = "http://192.168.35.120:8080"
    }
    private lateinit var mQueue: RequestQueue
    private  lateinit var mLoader: ImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_detail)
        mQueue = MySingleton.getInstance(application).requestQueue
        mLoader = MySingleton.getInstance(application).imageLoader
        val name : String? = intent.getStringExtra(DetailViewFragment.KEY_ID)
        getReview('"'+ name.toString() +'"')
    }
    override fun onStop(){
        super.onStop()
        mQueue.cancelAll(QUEUE_TAG)
    }

    fun getReview(name: String?) {
        val request = JsonArrayRequest(
            Request.Method.GET,
            "$SERVER_URL/select?name=$name",
            null,
            { parseReview(it) },
            { Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show() }
        )
        request.tag = QUEUE_TAG
        mQueue.add(request)
    }

    private fun parseReview(items: JSONArray) {
        val item: JSONObject = items[0] as JSONObject
        val explain = item.getString("explain")
        val imageUrl = item.getString("imageUrl")
        val timestamp = item.getString("timestamp")
        val uid = item.getString("uid")
        val userId = item.getString("userId")

        val imageUrlView = findViewById<ImageView>(R.id.detailviewitem_imageview_content)
//        imageUrlView.setImageUrl("$SERVER_URL/image/"+ URLEncoder.encode(imageUrl, "utf-8"), mLoader)
//        imageUrlView.imageMatrix = imageUrlView
        val explainView = findViewById<TextView>(R.id.detailviewitem_explain_textview)
        explainView.text = explain
//        val expView = findViewById<TextView>(R.id.)
//        expView.text = model
//        val actorView = findViewById<TextView>(R.id.character_actor)
//        actorView.text =actor

    }

}