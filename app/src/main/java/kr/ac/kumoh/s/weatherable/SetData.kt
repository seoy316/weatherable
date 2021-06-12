package kr.ac.kumoh.s.weatherable

import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONException

class SetData() {
    companion object {
        const val QUEUE_TAG = "VolleyRequest"
        const val SERVER_URL = "http://flask-weatherable-wkrtj.run.goorm.io/user"
    }

//    private val mQueue: RequestQueue
//
//    init {
//        mQueue = MySingleton.getInstance(application).requestQueue

    fun postUser(email: String) {
        val request: StringRequest = object : StringRequest(
            Method.POST,
            SERVER_URL,
            Response.Listener { response ->
                try {
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {  }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()

                params.put("email", email)

                return params
            }
        }

        request.setShouldCache(false)
        SignInActivity.requestQueue!!.add(request)
    }

    fun postRate(uid: Int, tid: Int, weather: Int, rating: Int) {
        val request: StringRequest = object : StringRequest(
            Method.POST,
            SERVER_URL,
            Response.Listener { response ->
                try {
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {  }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()

                params.put("user_id", uid.toString())
                params.put("tag_id", tid.toString())
                params.put("weather", weather.toString())
                params.put("rating", rating.toString())

                return params
            }
        }

        request.setShouldCache(false)
        if (SurveyRainyActivity.requestQueue != null){
            SurveyRainyActivity.requestQueue!!.add(request)
        }
        else if (SurveySunnyActivity.requestQueue != null){
            SurveySunnyActivity.requestQueue!!.add(request)
        }
    }
}