package kr.ac.kumoh.s20171145.weatherable

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_tour_list.*
import kr.ac.kumoh.s20171145.weatherable.navigation.*
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), LocationListener, BottomNavigationView.OnNavigationItemSelectedListener {

    var txt_city: TextView? = null
    var txt_temp: TextView? = null
    lateinit var txt_date: TextView
    lateinit var txt_weather: TextView
    lateinit var txt_time: TextView
    private var strDate: String? = null
    lateinit var viewModel: TourListViewModel
//    var homeFragment = HomeFragment()

    var lon_ : Double = 0.0 // 경도
    var lat_ : Double = 0.0// 위도

    companion object {
        var requestQueue: RequestQueue? = null
        var weatherCode: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        homeFragment = supportFragmentManager.findFragmentById(R.id.id_home_fragment) as HomeFragment

        txt_date = findViewById(R.id.txt_date)
        txt_time = findViewById(R.id.txt_time)
        txt_city = findViewById(R.id.txt_city)
        txt_weather = findViewById(R.id.txt_weather)
        txt_temp = findViewById(R.id.txt_temp)

        bottom_navigation.setOnNavigationItemSelectedListener(this)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )

        val dateNow = Calendar.getInstance().time
        strDate = DateFormat.format("EEE", dateNow) as String


        val button = findViewById<ImageButton>(R.id.btn)
        button.setOnClickListener { //시간데이터와 날씨데이터 활용
            getLocation()
        }

        //volley를 쓸 때 큐가 비어있으면 새로운 큐 생성하기
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(applicationContext)
        }

        LocationServices.getFusedLocationProviderClient(this)
        getLocation()

        val fragmentTourList = TourListFragment.newInstance("TourListFragment")
//        val fabPlaceList = findViewById<ExtendedFloatingActionButton>(R.id.fabPlaceList)
//        val rvTourList = findViewById<RecyclerView>(R.id.rvTourList)

        viewModel = ViewModelProvider(this).get(TourListViewModel::class.java)

        val bundle = Bundle()

        bundle.putDouble("x",lon_)
        bundle.putDouble("y",lat_)
        print("좌표값 $lon_ $lat_")

        bottom_navigation.selectedItemId = R.id.action_home
        registerPushToken()
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        setToolbarDefault()
        when(p0.itemId){
            R.id.action_home ->{
                var homeFragment = HomeFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,homeFragment).commit()
                return true
            }
            R.id.action_search ->{
                var gridFragment = GridFragment()
//                var detailFragment = DetailViewFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,gridFragment).commit()
//                supportFragmentManager.beginTransaction().replace(R.id.main_content,detailFragment).commit()
                return true
            }
            R.id.action_add_photo ->{
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    startActivity(Intent(this,AddPhotoActivity::class.java))
                }
                return true
            }
            R.id.action_recom_list ->{

                val recomFragment = TourListFragment.newInstance("TourListFragment")
                val bundle = Bundle()
                bundle.putDouble("x",lon_)
                bundle.putDouble("y",lat_)
                recomFragment.arguments = bundle
//                var recomFragment = TourListFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,recomFragment).commit()
                return true
            }
            R.id.action_account ->{
                var userFragment = UserFragment()
                var bundle = Bundle()
                var uid = FirebaseAuth.getInstance().currentUser?.uid

                bundle.putString("destinationUid",uid)
                userFragment.arguments = bundle
                supportFragmentManager.beginTransaction().replace(R.id.main_content,userFragment).commit()
                return true
            }
        }
        return false
    }

    private fun setToolbarDefault() {
        toolbar_username.visibility = View.GONE
        toolbar_btn_back.visibility = View.GONE
        toolbar_title_image.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == UserFragment.PICK_PROFILE_FROM_ALBUM && resultCode == RESULT_OK){
            val imageUri = data?.data
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val storageRef = FirebaseStorage.getInstance().reference.child("userProfileImages").child(uid!!)
            storageRef.putFile(imageUri!!).continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
                return@continueWithTask storageRef.downloadUrl
            }.addOnSuccessListener { uri ->
                var map = HashMap<String,Any>()
                map["image"] = uri.toString()
                FirebaseFirestore.getInstance().collection("profileImages").document(uid).set(map)
            }
        }
    }

    fun registerPushToken(){
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
                task ->
            val token = task.result?.token
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val map = mutableMapOf<String,Any>()
            map["pushToken"] = token!!

            FirebaseFirestore.getInstance().collection("pushtokens").document(uid!!).set(map)
        }
    }

    private fun getLocation() {
        val lm = getSystemService(LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        val provider = lm.getBestProvider(criteria, true)
        val isGPSEnabled: Boolean = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled: Boolean = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 115)
            return
        }

        when { //프로바이더 제공자 활성화 여부 체크
            isNetworkEnabled -> {
                val location = provider?.let{ lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) }
                val lon = location?.longitude!! //전역변수로 하니깐 주소 구하는  AddressChange()여기에서 lat, lon이 사용이 안됨 -> 그래서 CurrentCall()이랑 AddressChange()에 매개변수 전달로 변경함
                val lat = location.latitude
                lon_ = lon
                lat_ = lat

                CurrentCall(lon, lat)
            }
            isGPSEnabled -> { //실내에서 응답안할수도 있움 and GPS 는 에뮬레이터에서는 기본적으로 동작하지 않는다(말이야 방구야)
                val location = provider?.let{ lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) }
                val lon = location?.longitude!!
                val lat = location.latitude
                lon_ = lon
                lat_ = lat

                CurrentCall(lon, lat)
            }
            else -> {
                if (provider != null) {
                    lm.requestLocationUpdates(provider, 20000, 0f, this)
                }
            }
        }
    }

    private fun AddressChange(Longitude : Double, Latitude : Double){ //경위도를 주소로 변경하는 함수 매개변수로 경위도 받아서 수행
        val mGeocoder: Geocoder = Geocoder(this)
        val mResultList: List<Address> = mGeocoder.getFromLocation(Latitude, Longitude, 2)
        Log.d("aaa", "mResultList: $mResultList")
        if (mResultList.isNotEmpty()){
            var address = mResultList[0].getAddressLine(0)
            var add = address.split(" ")
            var finalAdd = ""
            for ( i in 1..3)
                finalAdd += add[i] + " "
            txt_city!!.text = finalAdd
//                homeFragment.setCity(finalAdd)
        }
    }

    private fun CurrentCall(lon : Double, lat : Double) {

        val url = "http://api.openweathermap.org/data/2.5/weather?&lat="+lat+"&lon="+lon+"&appid=45dc56ad4496c90b420cd24f1c7c79d5"
        val request: StringRequest = object : StringRequest(
            Method.GET, url,
            Response.Listener { response ->
                try {
                    print("try 실행")
                    val systemTime = System.currentTimeMillis()
                    val format = SimpleDateFormat("a HH시 mm분", Locale.KOREA).format(systemTime)
                    txt_time.text = format
//                    homeFragment.setTime(format)

                    val date = Calendar.getInstance().time
                    val tanggal = DateFormat.format("yyyy년 MM월 dd일", date) as String
                    val formatDate = "$tanggal ${strDate}요일"
                    txt_date.text = formatDate
//                    homeFragment.setDate(formatDate)

                    //api로 받은 파일 jsonobject로 새로운 객체 선언
                    val jsonObject = JSONObject(response)

                    //도시 키값 받기
                    AddressChange(lon, lat) // 다른데로 뺄까하다가 그냥 원래 있던 곳으로 둠

                    //날씨 키값 받기
                    val weatherJson = jsonObject.getJSONArray("weather")
                    val weatherObj = weatherJson.getJSONObject(0)
                    val weather = weatherObj.getString("description")
                    val jsonObjectTwo = jsonObject.getJSONObject("main")
                    val strWeather = weatherObj.getString("main")
                    val strDescWeather = weatherObj.getString("description")
                    val temp = jsonObjectTwo.getDouble("temp")

                    Log.i("txt_weather", jsonObject.toString())
                    when (strDescWeather) {
                        "broken clouds", "overcast clouds", "scattered clouds", "few clouds" -> {
                            txt_weather.text = "흐림"
//                            homeFragment.setWeather("흐림")
                            weatherCode = 2
                        }
                        "light rain" -> {
                            txt_weather.text = "약한 비"
                            weatherCode = 3
//                            homeFragment.setWeather("약한 비")
                        }
                        "haze" -> {
                            txt_weather.text = "안개"
                            weatherCode = 2
//                            homeFragment.setWeather("안개")
                        }
                        "moderate rain" -> {
                            txt_weather.text = "흐리고 비"
                            weatherCode = 3
//                            homeFragment.setWeather("흐리고 비")
                        }
                        "heavy intensity rain" -> {
                            txt_weather.text = "폭우"
                            weatherCode = 3
//                            homeFragment.setWeather("폭우")
                        }
                        "clear sky" -> {
                            txt_weather.text = "맑음"
                            weatherCode = 1
//                            homeFragment.setWeather("맑음")
                        }
                        else -> {
                            txt_weather.text = strWeather
//                            homeFragment.setWeather(strWeather)
                            weatherCode = 2
                        }
                    }

                    //기온 키값 받기
                    val tempK = JSONObject(jsonObject.getString("main"))

                    //기온 받고 켈빈 온도를 섭씨 온도로 변경
                    val tempDo = Math.round((tempK.getDouble("temp") - 273.15) * 100) / 100.0f
                    var tempRnd = Math.round(tempDo)
                    txt_temp!!.text = "$tempRnd°C"
//                    homeFragment.setTemp("$tempRnd°C")
                } catch (e: JSONException) {
                    e.printStackTrace()
                    print("catch 실행")
                }
            },
            Response.ErrorListener { }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                return HashMap()
            }
        }
        request.setShouldCache(false)
        requestQueue!!.add(request)
    }

    override fun onLocationChanged(location: Location) {
        TODO("Not yet implemented")
    }

}