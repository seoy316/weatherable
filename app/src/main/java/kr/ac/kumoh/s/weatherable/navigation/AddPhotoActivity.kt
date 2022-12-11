package kr.ac.kumoh.s.weatherable.navigation

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kr.ac.kumoh.s.weatherable.R
import kr.ac.kumoh.s.weatherable.navigation.model.ContentDTO
import kotlinx.android.synthetic.main.activity_add_photo.*
import kr.ac.kumoh.s.weatherable.MainActivity
import kr.ac.kumoh.s.weatherable.MainActivity.Companion.weatherCode
import kr.ac.kumoh.s.weatherable.MySingleton
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage : FirebaseStorage? = null
    var photoUri : Uri? = null
    var auth : FirebaseAuth? = null
    var firestore : FirebaseFirestore? = null
    var starRating : Float? = null
    var InputAddress : String? = null
    var place_name_ : String? = null
    var address_:String? = null
    var x_:String? = null
    var y_:String? = null
    /////크롤링 변수
    var tag_text:String? = null
    var tag_list = listOf<String>()

    var image : String? = null
    var postId: String? = null
    var time_: String? = null
    var content : String? = null
    var uid: String? = null
    var weather : String? = null
    var place : String? = null


    companion object{
        const val SERVER_URL = "https://weatherable-flask-lhavr.run.goorm.io"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        //Initiate
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        uid = auth?.currentUser?.uid

        //Open the album
        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)

        // 평점 등록
        ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            starRating = rating
        }

        // 날씨 선택
//        rg_weather.setOnCheckedChangeListener { group, checkedId ->
//            when(checkedId) {
//                R.id.radio_item1 ->
//            }
//        }

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyBBVbfSUh3_IYI2C3-prNbb9XRZj9LNF7A")
        }

        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                place_name_ = place.name
                address_ = place.address
                y_ = place.latLng!!.latitude.toString()
                x_= place.latLng!!.longitude.toString()
                tagcrawling() ///// 크롤링 실행

                Log.i(ContentValues.TAG, "Place: ${place_name_}, ${address_}, ${x_}, ${y_}")
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i(ContentValues.TAG, "An error occurred: $status")
            }
        })

        //add image upload event
        addphoto_btn_upload.setOnClickListener {
            contentUpload()
            content = addphoto_edit_explain.text.toString()
            postJSON()

        }
    }

//    크롤링하는거
    fun tagcrawling(){
        Thread(Runnable {
            val url = "https://m.search.naver.com/search.naver?sm=mtp_sly.hst&where=m&query=$place_name_&acr=1"
            val doc = Jsoup.connect(url).get()
            val tag_1 = doc.select("span[class=_3ocDE]")
            val tag_2 = doc.select("span[class= kAdc3]")
            val tag_3 = doc.select("span[class=_3Qp1c]")

            if (tag_1.isEmpty())
            {
                if (tag_2.isEmpty())
                {
                    if(tag_3.isEmpty())
                        tag_text = "기타"

                    else
                        tag_text = tag_3[0].text()
                }
                else
                    tag_text = tag_2[0].text()
            }
            else
                tag_text = tag_1[0].text()

            tag_list = tag_text?.split(",")!!

            if (tag_list[0] == "보물" || tag_list[0] == "문화")
                tag_text = "문화재"
            else if (tag_list[0] == "도시")
                tag_text = "공원"
            else
                tag_text = tag_list[0]
        }).start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_FROM_ALBUM){
            if(resultCode == Activity.RESULT_OK){
                //This is path to the selected image
                photoUri = data?.data
                addphoto_image.setImageURI(photoUri)

            }else{
                //Exit the addPhotoActivity if you leave the album without selecting it
                finish()

            }
        }
    }
    fun contentUpload(){
        //Make filename

        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png"

//        var storageRef = storage?.reference?.child("images")?.child(imageFileName)
        var storageRef = storage?.reference?.child("img")?.child(imageFileName)

        //Promise method
        storageRef?.putFile(photoUri!!)?.continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener { uri ->
            image = uri.toString()
            print("image $image")

            var contentDTO = ContentDTO()
//            //Insert downloadUrl of image
            contentDTO.imageUrl = uri.toString()
            firestore?.collection("img")?.document()?.set(contentDTO)

            setResult(Activity.RESULT_OK)
            finish()

            uploadPosting()
        }
    }

    private fun uploadPosting() {

        val request: StringRequest = object : StringRequest(
            Method.POST, "$SERVER_URL/reviews_post",
            Response.Listener { response ->
                try {
                    println("연결 성공")

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                println("실패")
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()

//                params.put("postId", .toString())
//                params.put("time_", time_.toString())
                params.put("content", content.toString())
                params.put("uid", uid.toString())
                params.put("weather", weatherCode.toString())
                params.put("place", place_name_.toString())
                params.put("image", image.toString())

                print("params $params")
                return params
            }
        }
        request.setShouldCache(false)
        MainActivity.requestQueue!!.add(request)
    }

    private fun postJSON() {
        val url = SERVER_URL + "send_review"
        val request: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    println("연결 성공")

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>
            {
                val params = HashMap<String, String>()
                params.put("rating", starRating.toString())
                params.put("name",place_name_.toString())
                params.put("address",address_.toString())
                params.put("x",x_.toString())
                params.put("y",y_.toString())
                params.put("tag", tag_text.toString())
                print("params $params")
                return params
            }
        }
        request.setShouldCache(false)
        MainActivity.requestQueue!!.add(request)
    }
}
