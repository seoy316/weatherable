package kr.ac.kumoh.s20170998.weatherable

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_log_in.*

class LogInActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 9001
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        auth = FirebaseAuth.getInstance()

        val btn_LogIn = findViewById<Button>(R.id.btn_LogIn)
        val btn_signIn = findViewById<Button>(R.id.btn_signIn)

        btn_LogIn.setOnClickListener {
            LoginEmail()
        }

        btn_signIn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun LoginEmail() {
        auth!!.signInWithEmailAndPassword(edt_Email.text.toString(), edt_Password.text.toString()).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                val user = auth?.currentUser

                val intent = Intent(this, MainActivity::class.java)
                val rainy = Intent(this, SurveyRainyActivity::class.java)
                val sunny = Intent(this, SurveySunnyActivity::class.java)

                rainy.putExtra("uid", user!!.uid)
                sunny.putExtra("uid", user!!.uid)

                startActivity(intent)

                ActivityCompat.finishAffinity(this)
            } else {
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun Logout(auth: FirebaseAuth?) {
        auth!!.signOut()
    }
}