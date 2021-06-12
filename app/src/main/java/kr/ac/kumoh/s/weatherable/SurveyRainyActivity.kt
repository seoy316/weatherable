package kr.ac.kumoh.s.weatherable

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_survey_rainy.*


class SurveyRainyActivity : AppCompatActivity() {
    private lateinit var mSurveyRainyModel: SurveyRainyViewModel
    private val mSurveyRainyAdapter = SurveyRainyAdapter()
    private lateinit var getData: GetData

    private var uid: Int? = null
    private lateinit var email: String

    companion object {
        var requestQueue: RequestQueue? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_rainy)

        lsSurveyRainyResult.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = mSurveyRainyAdapter
        }

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(applicationContext)
        }

        mSurveyRainyModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application)).get(SurveyRainyViewModel::class.java)

        mSurveyRainyModel.survey_rainy_list.observe(this, Observer<ArrayList<SurveyRainyViewModel.SurveyRainy>>{
            mSurveyRainyAdapter.notifyDataSetChanged()
        })
        mSurveyRainyModel.requestSurveyRainy()

        getData = GetData()


        val btn_survey_rainy = findViewById<Button>(R.id.btn_survey_rainy)
        btn_survey_rainy.setOnClickListener {
            val sunny = Intent(this, SurveySunnyActivity::class.java)
            startActivity(sunny)
        }
    }

    inner class SurveyRainyAdapter: RecyclerView.Adapter<SurveyRainyAdapter.ViewHolder>() {
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
            val txName: TextView
            val rgRainy: RadioGroup

            init {
                txName = itemView.findViewById<TextView>(R.id.tx_survey_rainy_name)
                rgRainy = itemView.findViewById<RadioGroup>(R.id.rg_survey_rainy)
            }
        }

        override fun getItemCount(): Int {
            return mSurveyRainyModel.getSize()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurveyRainyAdapter.ViewHolder {
            val view = layoutInflater.inflate(
                R.layout.item_survey_rainy,
                parent,
                false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: SurveyRainyAdapter.ViewHolder, position: Int) {
            holder.txName.text = mSurveyRainyModel.getSurveyRainy(position).name
        }

    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.radio_item1 ->
                    if (checked) {
                        for(i in 0 until getData.getUserSize()) {
                            email = intent.getStringExtra("email").toString()
//                            email = "20171145@kumoh.ac.kr"
                            if (email == getData.getUser(i).email) {
                                uid = getData.getUser(i).id
                            }
                        }
                        Log.i("d", "userid$uid")
                    }
                R.id.radio_item2 ->
                    if (checked) {
                        for(i in 0 until getData.getUserSize()) {
                            email = intent.getStringExtra("email").toString()
                            if (email == getData.getUser(i).email) {
                                uid = getData.getUser(i).id
                            }
                        }
                        Log.i("d", "userid$uid")
                    }
                R.id.radio_item3 ->
                    if (checked) {
                        for(i in 0 until getData.getUserSize()) {
                            email = intent.getStringExtra("email").toString()
                            if (email == getData.getUser(i).email) {
                                uid = getData.getUser(i).id
                            }
                        }
                        Log.i("d", "userid$uid")
                    }
                R.id.radio_item4 ->
                    if (checked) {
                        for(i in 0 until getData.getUserSize()) {
                            email = intent.getStringExtra("email").toString()
                            if (email == getData.getUser(i).email) {
                                uid = getData.getUser(i).id
                            }
                        }
                        Log.i("d", "userid$uid")
                    }
                R.id.radio_item5 ->
                    if (checked) {
                        for(i in 0 until getData.getUserSize()) {
//                                    email = intent. getStringExtra("email").toString()
                            if (getData.getUser(i).email == email) {
                                uid = getData.getUser(i).id
                                Log.i("d", "userid$uid")
                            }
                        }
//                                Log.i("d", "userid$uid")
                    }
            }
        }
    }
}