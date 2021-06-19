package kr.ac.kumoh.s20171145.weatherable.navigation

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
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
import kotlinx.android.synthetic.main.activity_add_photo.*
import kr.ac.kumoh.s20171145.weatherable.MainActivity
import kr.ac.kumoh.s20171145.weatherable.R
import kr.ac.kumoh.s20171145.weatherable.navigation.model.ContentDTO
import org.json.JSONException
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        //Initiate
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        //Open the album
        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)

        // 평점 등록
        ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            starRating = rating
        }

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
            //InputAddress = add_edit_review.text.toString()
            //uploadRating()
            postJSON()
            print("입력테스트 $InputAddress")
            print("평점 : ${starRating}")
        }

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

        var storageRef = storage?.reference?.child("images")?.child(imageFileName)

        //Promise method
        storageRef?.putFile(photoUri!!)?.continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener { uri ->
            var contentDTO = ContentDTO()

            //Insert downloadUrl of image
            contentDTO.imageUrl = uri.toString()

            //Insert uid of user
            contentDTO.uid = auth?.currentUser?.uid

            //Insert userId
            contentDTO.userId = auth?.currentUser?.email

            //Insert explain of content
            contentDTO.explain = addphoto_edit_explain.text.toString()

            //Insert timestamp
            contentDTO.timestamp = System.currentTimeMillis()

            firestore?.collection("images")?.document()?.set(contentDTO)

            setResult(Activity.RESULT_OK)

            finish()
        }
    }

    companion object {
        const val SERVER_URL = "https://flask-weatherable-wkrtj.run.goorm.io/"
    }

    private fun uploadRating() {

        val request: StringRequest = object : StringRequest(
            Method.POST, SERVER_URL + "rate",
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
                params.put("rating", starRating.toString())
                //params.put("address", InputAddress.toString())
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
                print("params $params")
                return params
            }
        }
        request.setShouldCache(false)
        MainActivity.requestQueue!!.add(request)
    }
}
