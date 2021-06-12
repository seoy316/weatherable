package kr.ac.kumoh.s.weatherable

import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import org.json.JSONArray
import org.json.JSONObject


class GetData {
    companion object {
        const val SERVER_URL = "http://192.168.0.11:8080"
    }

    data class User(var id: Int, var email: String)

    val user = MutableLiveData<ArrayList<User>>()
    private val user_data = ArrayList<User>()

    init {
        requestUserData()
    }

//    fun requestUserData() {
//        user_data.clear()
//        val request: StringRequest = object : StringRequest(
//            Method.GET,
//            SERVER_URL,
//            Response.Listener { response ->
//                try {
//                    val parser = JsonParser()
////                    val obj: Any = parser.parse(b)
//
////                    val parser = JsonParser()
////                    val obj: Any = parser.parse(response)
////                    val jsonObject = obj as JSONArray
//
//                    val jsonObject = JSONArray(response)
//                    for (i in 0 until jsonObject.length()) {
//                        val item: JSONObject = jsonObject[i] as JSONObject
//                        val id = item.getInt("id")
//                        val email = item.getString("email")
//
//                        user_data.add(User(id, email))
//                        user.value = user_data
//                    }
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                }
//            },
//            Response.ErrorListener { }) {}
//
//        request.setShouldCache(false)
//        SurveyRainyActivity.requestQueue!!.add(request)
//    }

    fun requestUserData() {
        val request = JsonArrayRequest(
            Request.Method.GET,
            "$SERVER_URL/user", // SERVER_URL에 테이블 이름을 붙여준다.
            null,
            {
                user_data.clear()
                parserUserJson(it)
                user.value = user_data
            },
            { print("가져오기 실패") }
        )

        request.setShouldCache(false)
        SurveyRainyActivity.requestQueue!!.add(request)
    }

    fun getUser(i: Int) = user_data[i]

    fun getUserSize() = user_data.size

    private fun parserUserJson(items: JSONArray) {
        for (i in 0 until items.length()) {
            val item: JSONObject = items[i] as JSONObject
            val id = item.getInt("id")
            val email = item.getString("email")

            user_data.add(User(id, email))
        }
    }
}