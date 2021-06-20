package kr.ac.kumoh.s.weatherable


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONException
import kr.ac.kumoh.s.weatherable.MainActivity.Companion.SERVER_URL

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    companion object {
        var requestQueue: RequestQueue? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        requestQueue = Volley.newRequestQueue(applicationContext)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val edt_Email = findViewById<EditText>(R.id.edt_Email)
        val edt_Password = findViewById<EditText>(R.id.edt_Password)
        val btn_SignIn = findViewById<Button>(R.id.btn_signIn)

        btn_SignIn.setOnClickListener {
            val email = edt_Email.text.toString().trim()
            val password = edt_Password.text.toString().trim()

            /* Validate... */

            createUser(email, password)
        }
    }

    private fun createEmail(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Authentication Success.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }

            }
    }

    private fun createUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    print("email: $email")
                    postUid(email)
                    updateUI(user)

                    val intent = Intent(this, LogInActivity::class.java)
                    startActivity(intent)

                    ActivityCompat.finishAffinity(this)
                } else {
                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        user?.let {
            val txt_Result = findViewById<TextView>(R.id.txt_Result)
            txt_Result.text = "Email: ${getEmail(user)}\nUid: ${getId(user)}"

            val userHashMap: HashMap<Any, String> = HashMap<Any, String>()

            userHashMap.put("uid", getId(user))
            userHashMap.put("email", getEmail(user))

            db.collection("user").add(userHashMap)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d("Weatherable", "Document ID = " + it.result)
                    }
                }
                .addOnFailureListener {
                }
        }
    }

    fun postUid(user: String) {
        val url = SERVER_URL + "user"
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
                params.put("uid",user)
                print("params $params")
                return params
            }
        }
        request.setShouldCache(false)
        requestQueue?.add(request)
    }

    private fun getEmail(user: FirebaseUser?): String { return user!!.email.toString() }

    private fun getId(user: FirebaseUser?): String { return user!!.uid }

}