package kr.ac.kumoh.s.weatherable


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    val setData = SetData()

    companion object {
        var requestQueue: RequestQueue? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

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
            setData.postUser(edt_Email.text.toString())

//            val intent = Intent(this, SurveyRainyActivity::class.java)
//            intent.putExtra("email", email)
//            startActivity(intent)
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

                    val intent = Intent(this, SurveyRainyActivity::class.java)
                    intent.putExtra("email", user!!.email.toString())
                    startActivity(intent)
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
}