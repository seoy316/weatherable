package kr.ac.kumoh.s20171145.weatherable

import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONException

class PostRate() {
    companion object {
        const val QUEUE_TAG = "VolleyRequest"
        const val SERVER_URL = "https://flask-weatherable-wkrtj.run.goorm.io"
    }

//    private val mQueue: RequestQueue
//
//    init {
//        mQueue = MySingleton.getInstance(application).requestQueue

    fun postRate(uid: String, tid: ArrayList<Int?>, weather: Int, rating: ArrayList<Int?>) {
        val request: StringRequest = object : StringRequest(
            Method.POST,
            "$SERVER_URL/tag_place",
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
        else if (SurveyCloudyActivity.requestQueue != null){
            SurveyCloudyActivity.requestQueue!!.add(request)
        }
    }

//    fun requestUserID() : String {
//        val request: StringRequest = object : StringRequest(
//            Method.POST,
//            "$SERVER_URL/user_post",
//            Response.Listener { response ->
//                try  {
//                    println("post try")
//                    val jsonObject = JSONArray(response)
//                    for (i in 0 until jsonObject.length()) {
//                        val item: JSONObject = jsonObject[i] as JSONObject
//                        val id = item.getString("id")
//
//                        user_id_ = id
//                        println("id_ : $user_id_")
//
//                 }
//
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                }
//            },
//            Response.ErrorListener { }) {
//            @Throws(AuthFailureError::class)
//            override fun getParams(): Map<String, String>
//            {
//                val params = HashMap<String, String>()
//                params["email"] = email_
////                params.put("uid", SurveyRainyActivity.uid.toString())
//                println("paramas : $params")
//
//
//                return params
//            }
//        }
//
//        request.setShouldCache(false)
//        if (SurveyRainyActivity.requestQueue != null) {
//            SurveyRainyActivity.requestQueue?.add(request)
//        }
//        else if (LogInActivity.requestQueue != null) {
//            LogInActivity.requestQueue?.add(request)
//        }
//
//
//        return user_id_
//    }
}