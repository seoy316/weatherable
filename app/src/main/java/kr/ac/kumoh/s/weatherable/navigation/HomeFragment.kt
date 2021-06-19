package kr.ac.kumoh.s.weatherable.navigation

import android.Manifest
import android.content.pm.PackageManager
import android.location.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import kr.ac.kumoh.s.weatherable.MainActivity
import kr.ac.kumoh.s.weatherable.MainActivity.Companion.requestQueue
import kr.ac.kumoh.s.weatherable.MainActivity.Companion.weatherCode
import kr.ac.kumoh.s.weatherable.R
import kr.ac.kumoh.s.weatherable.TourListFragment
import kr.ac.kumoh.s.weatherable.TourListViewModel
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    var txt_city: TextView? = null
    var txt_temp: TextView? = null
    var txt_date: TextView? = null
    var txt_weather: TextView? = null
    var txt_time: TextView? = null
    private var strDate: String? = null
    lateinit var viewModel: TourListViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_home,container,false)

        txt_date = view.findViewById(R.id.txt_date)
        txt_time = view.findViewById(R.id.txt_time)
        txt_city = view.findViewById(R.id.txt_city)
        txt_weather = view.findViewById(R.id.txt_weather)
        txt_temp = view.findViewById(R.id.txt_temp)

        return view
    }

    fun setDate(text: String){
        txt_date!!.text = text
    }
    fun setCity(text: String){
        txt_city!!.text = text
    }
    fun setTemp(text: String){
        txt_temp!!.text = text
    }
    fun setWeather(text: String){
        txt_weather!!.text = text
    }
    fun setTime(text: String){
        txt_time!!.text = text
    }

    fun getDate(){
//        activity?.findViewById<TextView>(R.id.txt_date)
        txt_date!!.text = activity?.findViewById<TextView>(R.id.txt_date).toString()
    }
    fun getCity(){
//        txt_date!!.text = text

        txt_city!!.text = activity?.findViewById<TextView>(R.id.txt_city).toString()
        print("test $txt_city")
    }

    fun getTemp(){
//        txt_temp!!.text = text
        txt_temp!!.text = activity?.findViewById<TextView>(R.id.txt_temp).toString()
    }
    fun getWeather(){
//        txt_weather!!.text = text
        txt_weather!!.text = activity?.findViewById<TextView>(R.id.txt_weather).toString()
    }

    fun getTime(){
//        txt_time!!.text = text
        txt_time!!.text = activity?.findViewById<TextView>(R.id.txt_time).toString()
    }


}