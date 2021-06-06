package kr.ac.kumoh.s.weatherable

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.NetworkImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_survey_rainy.*
import kotlinx.android.synthetic.main.item_survey_sunny.*
import kr.ac.kumoh.s.weatherable.R


class SurveyRainyActivity : AppCompatActivity() {
    private lateinit var mSurveyRainyModel: SurveyRainyViewModel
    private val mSurveyRainyAdapter = SurveyRainyAdapter()

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_rainy)

        lsSurveyRainyResult.apply {
            layoutManager = GridLayoutManager(applicationContext, 3)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = mSurveyRainyAdapter
        }

        mSurveyRainyModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application)).get(SurveyRainyViewModel::class.java)

        mSurveyRainyModel.survey_rainy_list.observe(this, Observer<ArrayList<SurveyRainyViewModel.SurveyRainy>>{
            mSurveyRainyAdapter.notifyDataSetChanged()
        })
        mSurveyRainyModel.requestSurveyRainy()

        db = FirebaseFirestore.getInstance()

        val btn_survey_rainy = findViewById<Button>(R.id.btn_survey_rainy)
        btn_survey_rainy.setOnClickListener {
            val intent = Intent(this, SurveySunnyActivity::class.java)
            startActivity(intent)
        }

    }

    inner class SurveyRainyAdapter: RecyclerView.Adapter<SurveyRainyAdapter.ViewHolder>() {
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), AdapterView.OnItemSelectedListener  {
            val txName: TextView
            val niImage: NetworkImageView = itemView.findViewById<NetworkImageView>(R.id.ni_survey_rainy) // NetWorkImageView를 사용하여 이미지를 보여준다.
            val spn_rainy: Spinner

            init {
                txName = itemView.findViewById<TextView>(R.id.tx_survey_rainy_name)
                niImage.setDefaultImageResId(R.id.ni_survey_rainy)
                spn_rainy = itemView.findViewById<Spinner>(R.id.spn_rainy)

                ArrayAdapter.createFromResource(
                        this@SurveyRainyActivity,
                        R.array.spn_items,
                        android.R.layout.simple_spinner_item
                ).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spn_rainy.adapter = adapter
                }

                spn_rainy.onItemSelectedListener = this
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
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
            holder.niImage.setImageUrl(mSurveyRainyModel.getImageUrl(position), mSurveyRainyModel.mLoader)
        }
    }

}