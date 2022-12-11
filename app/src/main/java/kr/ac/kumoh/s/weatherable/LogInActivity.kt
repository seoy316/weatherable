package kr.ac.kumoh.s.weatherable


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class LogInActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 9001
    private var auth: FirebaseAuth? = null
    lateinit var email_ : String
    var user_id_ : String  =""

    companion object {
        var requestQueue: RequestQueue? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(applicationContext)
        }

        auth = FirebaseAuth.getInstance()

        val btn_LogIn = findViewById<Button>(R.id.btn_LogIn)
        val btn_signIn = findViewById<Button>(R.id.btn_signIn)

        val edt_Email = findViewById<EditText>(R.id.edt_login_Email)
        val edt_Password = findViewById<EditText>(R.id.edt_login_Password)


        btn_LogIn.setOnClickListener {
//            Log.i("d", "EMAIL$email")
//            Log.i("d", "PASSWORD$password")
            val email = edt_Email.text.toString()
            email_ = edt_Email.text.toString()

            val password = edt_Password.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                val msg = "이메일과 비밀번호는 빈 칸이 아니어야 합니다."
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

            }
            else {
                LoginEmail(auth, email, password)
                requestUserID()

                Log.i("d", "SETDATAUSERID_${user_id_}")

//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
////                intent.putExtra("uid", uid)
//                ActivityCompat.finishAffinity(this)
            }
        }

        btn_signIn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    fun LoginEmail(auth: FirebaseAuth?, email: String, password: String) {
        auth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {
            if (it.isSuccessful) {
//                Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                val user = auth.currentUser
//                requestUserID()
            } else {
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun Logout(auth: FirebaseAuth?) {
        auth!!.signOut()
    }

    fun requestUserID() {
//        println("post email : $email_")
        val request: StringRequest = object : StringRequest(
            Method.POST,
            "${SERVER_URL().url}/users/uid",
            Response.Listener { response ->
                try {
                    println("post try")
                    val jsonObject = JSONArray(response)
                    for (i in 0 until jsonObject.length()) {
                        val item: JSONObject = jsonObject[i] as JSONObject
                        val id = item.getString("id")

                        user_id_ = id

                        println("id_ : $user_id_")

                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("uid", id)
                        startActivity(intent)

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
//                params.put("uid", SurveyRainyActivity.uid.toString())
                println("paramas : $params")
                return params
            }
        }

        request.setShouldCache(false)
        requestQueue?.add(request)
    }
}

