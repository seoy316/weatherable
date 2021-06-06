package kr.ac.kumoh.s.weatherable

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.DecimalFormat

class TourListViewModel(application: Application): AndroidViewModel(application) {

    companion object {
        const val QUEUE_TAG = "VolleyRequest"
        const val SERVER_URL = "https://flask-weatherable-wkrtj.run.goorm.io/"
    }

    data class TourList(var name: String, var address: String, var distance: String)

    private var mQueue: RequestQueue
    val tour_list = MutableLiveData<ArrayList<TourList>>()
    private val list_data = ArrayList<TourList>()

    init {
        mQueue = MySingleton.getInstance(application).requestQueue
    }

    fun postJSON(x: Double?, y:Double?) {
        list_data.clear()
        val url = SERVER_URL + "distance"
        val request: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    println("연결 성공")
                    val jsonObject = JSONArray(response)
                    val dec = DecimalFormat("#.##")
                    for (i in 0 until jsonObject.length()){
                        val item:JSONObject = jsonObject[i] as JSONObject
                        val name = item.getString("name")
                        val address = item.getString("address")
                        val distance = dec.format(item.getDouble("dist")) + "km"

                        println("test_name : $name")
                        println("test_adress : $address")
                        println("test_distance : $distance")

                        list_data.add(TourList(name, address, distance))
                        tour_list.value = list_data
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>
            {
                val params = HashMap<String, String>()
                params.put("x",x.toString())
                params.put("y",y.toString())
                print("params $params")
                return params
            }
        }
        request.setShouldCache(false)
        MainActivity.requestQueue!!.add(request)
    }

    fun getTourList(i: Int) = list_data[i]
    fun getSize() = list_data.size

    override fun onCleared() {
        super.onCleared()
        mQueue.cancelAll(QUEUE_TAG)
    }

//    private fun parseJson(items: JSONArray) {
//        for (i in 0 until items.length()) {
//            val item: JSONObject = items[i] as JSONObject
//            val name = item.getString("name")
//            val address = item.getString("address")
//            val distance = item.getString("dist")
//
//            list_data.add(TourList(name, address))
//        }
//    }
}