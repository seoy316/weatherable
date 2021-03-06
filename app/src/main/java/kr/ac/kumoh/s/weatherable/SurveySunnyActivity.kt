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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_survey_sunny.*

class SurveySunnyActivity : AppCompatActivity() {
    private lateinit var mSurveySunnyModel: SurveySunnyViewModel
    private val mSurveySunnyAdapter = SurveySunnyAdapter()
    private lateinit var getData: GetData
    val setData = SetData()

    private var uid: Int? = null
    private var tid: Int? = null
    private lateinit var email: String
    private lateinit var name: String
    private val tid_list = arrayListOf<Int>()
    private var rating = arrayListOf<Int?>(null, null, null, null, null, null, null, null, null)

    companion object {
        var requestQueue: RequestQueue? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_sunny)

        lsSurveySunnyResult.apply {
            layoutManager = GridLayoutManager(applicationContext, 3)
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

        getData = GetData()

        val btn_survey_sunny = findViewById<Button>(R.id.btn_survey_sunny)
        btn_survey_sunny.setOnClickListener {
//            for(i in 0 until mSurveyRainyModel.getSize()) {
//                setData.postRate(uid, tid_list[i], 3, rating[i])
//            }

            setData.postRate(uid, tid_list, 1, rating)
            val main = Intent(this, MainActivity::class.java)
            startActivity(main)
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
                txName = itemView.findViewById<TextView>(R.id.tx_survey_rainy_name)
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
                R.layout.item_survey_rainy,
                parent,
                false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: SurveySunnyAdapter.ViewHolder, position: Int) {
            holder.txName.text = mSurveySunnyModel.getSurveySunny(position).name

            tid_list.add(mSurveySunnyModel.getSurveySunny(position).id)
            Log.i("d", "tid_list$tid_list")

            holder.rgItem1.setOnClickListener {
                rating[position] = 1
                setRadio(holder, 1)
//                email = intent.getStringExtra("email").toString()
                email = "20171145@kumoh.ac.kr"
                for (i in 0 until getData.getUserSize()) {
                    if (email == getData.getUser(i).email) {
                        uid = getData.getUser(i).id
                        Log.i("d", "userID$uid")
                    }
                }
                Log.i("d", "RatingChecked$rating")
            }

            holder.rgItem2.setOnClickListener {
                rating[position] = 2
                setRadio(holder, 2)
//                email = intent.getStringExtra("email").toString()
                email = "20171145@kumoh.ac.kr"
                for (i in 0 until getData.getUserSize()) {
                    if (email == getData.getUser(i).email) {
                        uid = getData.getUser(i).id
                        Log.i("d", "userID$uid")
                    }
                }
                Log.i("d", "RatingChecked$rating")
            }

            holder.rgItem3.setOnClickListener {
                rating[position] = 3
                setRadio(holder, 3)
//                email = intent.getStringExtra("email").toString()
                email = "20171145@kumoh.ac.kr"
                for (i in 0 until getData.getUserSize()) {
                    if (email == getData.getUser(i).email) {
                        uid = getData.getUser(i).id
                        Log.i("d", "userID$uid")
                    }
                }
                Log.i("d", "RatingChecked$rating")
            }

            holder.rgItem4.setOnClickListener {
                rating[position] = 4
                setRadio(holder, 4)
//                email = intent.getStringExtra("email").toString()
                email = "20171145@kumoh.ac.kr"
                for (i in 0 until getData.getUserSize()) {
                    if (email == getData.getUser(i).email) {
                        uid = getData.getUser(i).id
                        Log.i("d", "userID$uid")
                    }
                }
                Log.i("d", "RatingChecked$rating")
            }

            holder.rgItem5.setOnClickListener {
                rating[position] = 5
                setRadio(holder, 5)
//                email = intent.getStringExtra("email").toString()
                email = "20171145@kumoh.ac.kr"
                for (i in 0 until getData.getUserSize()) {
                    if (email == getData.getUser(i).email) {
                        uid = getData.getUser(i).id
                        Log.i("d", "userID$uid")
                    }
                }
                Log.i("d", "RatingChecked$rating")
            }
        }

        private fun setRadio(holder: SurveySunnyAdapter.ViewHolder, selection: Int) {
            holder.rgItem1.isChecked = false
            holder.rgItem2.isChecked = false
            holder.rgItem3.isChecked = false
            holder.rgItem4.isChecked = false
            holder.rgItem5.isChecked = false

//            rb1.isChecked = false
//            rb2.isChecked = false
//            rb3.isChecked = false
//            rb4.isChecked = false
//            rb5.isChecked = false

            if (selection == 1) holder.rgItem1.isChecked = true
            if (selection == 2) holder.rgItem2.isChecked = true
            if (selection == 3) holder.rgItem3.isChecked = true
            if (selection == 4) holder.rgItem4.isChecked = true
            if (selection == 5) holder.rgItem5.isChecked = true
        }

    }
}