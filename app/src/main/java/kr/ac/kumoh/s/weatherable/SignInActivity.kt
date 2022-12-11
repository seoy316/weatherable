package kr.ac.kumoh.s.weatherable

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kr.ac.kumoh.s.weatherable.rate.SurveyRainyActivity
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    val logIn = LogInActivity()

    lateinit var email_ : String
    lateinit var uid_ : String
    var user_id_ : String  =""

    companion object {
        var requestQueue: RequestQueue? = null
        val SERVER_URL = "https://weatherable-flask-psgys.run.goorm.io"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        print("회원가입 액티비티")
        println(SERVER_URL())
        println(SERVER_URL().url)
        println(SERVER_URL().url.toString())

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val edt_Email = findViewById<EditText>(R.id.edt_Email)
        val edt_Password = findViewById<EditText>(R.id.edt_Password)
        val btn_SignIn = findViewById<Button>(R.id.btn_signIn)

        btn_SignIn.setOnClickListener {
            val email = edt_Email.text.toString().trim()
            email_ = edt_Email.text.toString()
//            uid_ = auth.currentUser!!.uid.toString()
            val password = edt_Password.text.toString().trim()

            /* Validate... */
            if (email.isEmpty() || password.isEmpty()) {
                val msg = "이메일과 비밀번호는 빈 칸이 아니어야 합니다."
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
            else {
                createUser(email, password)

//                if (user != null) {
//                    setData.postUser(user.uid, email)
//                }

//                logIn.LoginEmail(auth, email, password)
//
//                requestUserID()
            }
        }

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(applicationContext)
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
                    updateUI(user)

                    uid_ = user!!.uid

                    logIn.LoginEmail(auth, email, password)
                    requestUserID()


//                    val intent = Intent(this, SurveyRainyActivity::class.java)
//                    intent.putExtra("email", user!!.email.toString())
//                    startActivity(intent)
                } else if(it.exception?.message.isNullOrEmpty()) {
                    Toast.makeText(this, "이메일과 비밀번호는 빈 칸이 아니어야 합니다.", Toast.LENGTH_SHORT).show()
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
        }
    }

    fun getEmail(user: FirebaseUser?): String { return user!!.email.toString() }

    fun getId(user: FirebaseUser): String { return user.uid }

    fun requestUserID() {
//        println("post email : $email_")
        val request: StringRequest = object : StringRequest(
            Method.POST,
            "${SERVER_URL().url}/users/sign-in",
            Response.Listener { response ->
                try {
                    println("post try")
                    val jsonObject = JSONArray(response)
                    for (i in 0 until jsonObject.length()) {
                        val item: JSONObject = jsonObject[i] as JSONObject
                        val id = item.getString("id")

                        //
                        user_id_ = id

                        println("id_ : $user_id_")

                        val rainy = Intent(this, SurveyRainyActivity::class.java)
                        rainy.putExtra("uid", user_id_)
                        startActivity(rainy)
//
//                        val intent = Intent(this, MainActivity::class.java)
//                        intent.putExtra("uid", user_id_)
//                        startActivity(intent)

                        ActivityCompat.finishAffinity(this)

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
                params["email"] = email_.toString()
                params["uid"] = uid_.toString()
//                params.put("uid", SurveyRainyActivity.uid.toString())
                println("paramas : $params")
                return params
            }
        }

        request.setShouldCache(false)
        LogInActivity.requestQueue?.add(request)
    }
}