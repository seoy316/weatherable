package kr.ac.kumoh.s.weatherable

import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.firebase.auth.FirebaseAuth
//import kr.ac.kumoh.s.weatherable.MainActivity.Companion.auth
import kr.ac.kumoh.s.weatherable.MainActivity.Companion.uid
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class GetUserId {

    private val RC_SIGN_IN = 9001

    companion object {
        fun requestUser() {
            val request: StringRequest = object : StringRequest(
                Method.POST,
                "${SERVER_URL().url}/users/uid",
                Response.Listener { response ->
                    try {
                        val jsonObject = JSONArray(response)
                        for (i in 0 until jsonObject.length()) {
                            val item: JSONObject = jsonObject[i] as JSONObject
                            val uid = item.getString("uid")
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    var auth = FirebaseAuth.getInstance()
                    params["uid"] = auth!!.currentUser.toString()
                    return params
                }
            }
        }
    }

}
