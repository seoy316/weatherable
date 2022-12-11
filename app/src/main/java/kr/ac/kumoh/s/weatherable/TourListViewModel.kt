package kr.ac.kumoh.s.weatherable

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import kr.ac.kumoh.s.weatherable.MainActivity.Companion.weatherCode
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.DecimalFormat

class TourListViewModel(application: Application): AndroidViewModel(application) {

    companion object {
        const val QUEUE_TAG = "VolleyRequest"
    }

    data class TourList(var name: String, var address: String, var distance: String, var tour_x: Double, var tour_y: Double)

    private var mQueue: RequestQueue
    val tour_list = MutableLiveData<ArrayList<TourList>>()
    private val list_data = ArrayList<TourList>()


    init {
        mQueue = MySingleton.getInstance(application).requestQueue
    }

    fun postJSON(x: Double?, y:Double?) {
        list_data.clear()
        val url = "${SERVER_URL().url}/places/req"
        val request: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    println("연결 성공")
                    val jsonObject = JSONArray(response)
                    print("추천리스트 $jsonObject")
                    val dec = DecimalFormat("#.##")
                    for (i in 0 until jsonObject.length()){
                        val item:JSONObject = jsonObject[i] as JSONObject
                        val name = item.getString("name")
                        print("name 나와라 " + name)
                        val address = item.getString("address")
                        val distance = dec.format(item.getDouble("dist")) + "km"
                        val tour_x = item.getDouble("x")
                        val tour_y = item.getDouble("y")

                        println("test_name : $name")
                        println("test_adress : $address")
                        println("test_distance : $distance")

                        list_data.add(TourList(name, address, distance,tour_x,tour_y))
                        print("list check" + list_data)
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
                params.put("weatherCode", weatherCode.toString())
                params.put("uid", MainActivity.id.toString())
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

}