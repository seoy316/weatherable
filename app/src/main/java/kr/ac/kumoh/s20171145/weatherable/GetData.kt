package kr.ac.kumoh.s20171145.weatherable

import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import org.json.JSONArray
import org.json.JSONObject


class GetData {
    companion object {
        const val SERVER_URL = "https://flask-weatherable-wkrtj.run.goorm.io"
    }

    data class User(var id: Int, var email: String)

    val user = MutableLiveData<ArrayList<User>>()
    private val user_data = ArrayList<User>()

    init {
        requestUserData()
    }

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
        if (SurveyRainyActivity.requestQueue != null){
            SurveyRainyActivity.requestQueue!!.add(request)
        }
        else if (SurveySunnyActivity.requestQueue != null){
            SurveySunnyActivity.requestQueue!!.add(request)
        }
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