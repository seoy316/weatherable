package kr.ac.kumoh.s.weatherable.navigation

import android.app.Activity
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
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kr.ac.kumoh.s.weatherable.R
import kr.ac.kumoh.s.weatherable.navigation.model.ContentDTO
import kotlinx.android.synthetic.main.activity_add_photo.*
import kr.ac.kumoh.s.weatherable.MainActivity
import kr.ac.kumoh.s.weatherable.MySingleton
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage : FirebaseStorage? = null
    var photoUri : Uri? = null
    var auth : FirebaseAuth? = null
    var firestore : FirebaseFirestore? = null
    var starRating : Float? = null

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

        //add image upload event
        addphoto_btn_upload.setOnClickListener {
            contentUpload()
            uploadRating()
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

        /*//Callback method
        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
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
        }*/
    }

    companion object {
        const val QUEUE_TAG = "VolleyRequest"
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
                print("params $params")
                return params
            }
        }
        request.setShouldCache(false)
        MainActivity.requestQueue!!.add(request)
    }
}
