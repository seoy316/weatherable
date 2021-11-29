package kr.ac.kumoh.s.weatherable

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONException


class SurveySubmit(application: Application) : AndroidViewModel(application) {
    companion object {
        const val QUEUE_TAG = "VolleyRequest"
        const val SERVER_URL = "https://weatherable-flask-lhavr.run.goorm.io"
        // "http://192.168.200.176:8080"
        // http://192.168.0.11:8080
    }

    private var mQueue: RequestQueue
    private var db: FirebaseFirestore
    private var auth: FirebaseAuth
    private var uid: String
    private lateinit var document: DocumentSnapshot
    private lateinit var rainy: ArrayList<Int>
    private lateinit var sunny: ArrayList<Int>

    init {
//        submitSurvey()
//        db = FirebaseFirestore.getInstance()

        getPref()
        mQueue = MySingleton.getInstance(application).requestQueue
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        uid = auth.currentUser!!.uid
    }

    fun submitSurvey() {
        val response: StringRequest = object : StringRequest(
            Method.POST,
            SERVER_URL, // SERVER_URL에 테이블 이름을 붙여준다.
            Response.Listener {
                try {
                    Toast.makeText(getApplication(), "연결 성공", Toast.LENGTH_LONG).show()
                } catch (e: JSONException) {
                    e.printStackTrace()
                } },
            Response.ErrorListener { }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): MutableMap<String, String>? {
                val params = HashMap<String, String>()
                for (i in 1 until rainy.size)
                {
                    params.put("user_id", uid)
                    params.put("weather", rainy[0].toString())
                    params.put("tag_id", i.toString())
                    params.put("rating", rainy[i].toString())
                }

                return params
            }
        }

        response.tag = QUEUE_TAG
        mQueue.add(response)
    }

    fun getPref() {
        db.collection("preferences").document(uid).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    document = it.getResult()
                    if (document.exists()) {
                        rainy = document.get("rainyRating") as ArrayList<Int>
//                        sunny = document.get("sunnyRating") as ArrayList<Int>
                        Log.i("d", "rainy$rainy")
//                        Log.i("d", "sunny$sunny")
                    }
                }
            }
    }
}