package kr.ac.kumoh.s.weatherable

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_survey_sunny.*

class SurveySunnyActivity : AppCompatActivity() {
    private lateinit var mSurveySunnyModel: SurveySunnyViewModel
    private val mSurveySunnyAdapter = SurveySunnyAdapter()
    val post = PostRate()

    private var id: Int? = null
    private val tid_list = arrayListOf<Int?>(null, null, null, null, null, null, null)
    private var rating = arrayListOf<Int?>(null, null, null, null, null, null, null)

    companion object {
        var requestQueue: RequestQueue? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_sunny)

        lsSurveySunnyResult.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = mSurveySunnyAdapter
        }

        mSurveySunnyModel =
            ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application)).get(
                SurveySunnyViewModel::class.java
            )

        mSurveySunnyModel.survey_sunny_list.observe(
            this,
            Observer<ArrayList<SurveySunnyViewModel.SurveySunny>> {
                mSurveySunnyAdapter.notifyDataSetChanged()
            })
        mSurveySunnyModel.requestSurveySunny()



//        for (i in 0 until getData.getUserSize()) {
//            if (email == getData.getUser(i).email) {
//                id = getData.getUser(i).id
//            }
//        }
        val uid = intent.getStringExtra("uid").toString()
        Log.i("d", "SUNNYYUID$uid")

        val btn_survey_sunny = findViewById<Button>(R.id.btn_survey_sunny)
        btn_survey_sunny.setOnClickListener {

            post.postRate(uid, tid_list, 1, rating)
            val cloudy = Intent(this, SurveyCloudyActivity::class.java)
            cloudy.putExtra("uid", uid)
            startActivity(cloudy)
        }

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(applicationContext)
        }
    }

    inner class SurveySunnyAdapter : RecyclerView.Adapter<SurveySunnyAdapter.ViewHolder>() {
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
            val txName: TextView
            val rgItem1: RadioButton
            val rgItem2: RadioButton
            val rgItem3: RadioButton
            val rgItem4: RadioButton
            val rgItem5: RadioButton

            init {
                txName = itemView.findViewById<TextView>(R.id.tx_survey_sunny_name)
                rgItem1 = itemView.findViewById<RadioButton>(R.id.radio_item1)
                rgItem2 = itemView.findViewById<RadioButton>(R.id.radio_item2)
                rgItem3 = itemView.findViewById<RadioButton>(R.id.radio_item3)
                rgItem4 = itemView.findViewById<RadioButton>(R.id.radio_item4)
                rgItem5 = itemView.findViewById<RadioButton>(R.id.radio_item5)
//                rgGroup.checkedRadioButtonId
            }
        }


        override fun getItemCount(): Int {
            return mSurveySunnyModel.getSize()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurveySunnyAdapter.ViewHolder {
            val view = layoutInflater.inflate(
                R.layout.item_survey_sunny,
                parent,
                false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: SurveySunnyAdapter.ViewHolder, position: Int) {
            holder.txName.text = mSurveySunnyModel.getSurveySunny(position).name

            tid_list[position] = mSurveySunnyModel.getSurveySunny(position).id
//            tid_list.add(mSurveySunnyModel.getSurveySunny(position).id)
            Log.i("d", "tid_list$tid_list")

            holder.rgItem1.setOnClickListener {
                rating[position] = 1
                setRadio(holder, 1)
            }

            holder.rgItem2.setOnClickListener {
                rating[position] = 2
                setRadio(holder, 2)
            }

            holder.rgItem3.setOnClickListener {
                rating[position] = 3
                setRadio(holder, 3)
            }

            holder.rgItem4.setOnClickListener {
                rating[position] = 4
                setRadio(holder, 4)
            }

            holder.rgItem5.setOnClickListener {
                rating[position] = 5
                setRadio(holder, 5)
            }
        }

        private fun setRadio(holder: SurveySunnyAdapter.ViewHolder, selection: Int) {
            if (selection == 1) holder.rgItem1.isChecked = true
            if (selection == 2) holder.rgItem2.isChecked = true
            if (selection == 3) holder.rgItem3.isChecked = true
            if (selection == 4) holder.rgItem4.isChecked = true
            if (selection == 5) holder.rgItem5.isChecked = true
        }

    }
}