package kr.ac.kumoh.s20170998.weatherable

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.NetworkImageView
import kotlinx.android.synthetic.main.activity_survey_sunny.*

class SurveySunnyActivity : AppCompatActivity() {
    private lateinit var mSurveySunnyModel: SurveySunnyViewModel
    private val mSurveySunnyAdapter = SurveySunnyAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_sunny)

        lsSurveySunnyResult.apply {
            layoutManager = GridLayoutManager(applicationContext, 3)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = mSurveySunnyAdapter
        }

        mSurveySunnyModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application)).get(SurveySunnyViewModel::class.java)

        mSurveySunnyModel.survey_sunny_list.observe(this, Observer<ArrayList<SurveySunnyViewModel.SurveySunny>>{
            mSurveySunnyAdapter.notifyDataSetChanged()
        })
        mSurveySunnyModel.requestSurveySunny()

    }

    inner class SurveySunnyAdapter: RecyclerView.Adapter<SurveySunnyAdapter.ViewHolder>() {
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), AdapterView.OnItemSelectedListener {
            val txName: TextView
            val niImage: NetworkImageView = itemView.findViewById<NetworkImageView>(R.id.ni_survey_sunny) // NetWorkImageView를 사용하여 이미지를 보여준다.
            val spn_sunny: Spinner

            init {
                txName = itemView.findViewById<TextView>(R.id.tx_survey_sunny_name)
                niImage.setDefaultImageResId(R.id.ni_survey_sunny)
                spn_sunny = itemView.findViewById<Spinner>(R.id.spn_sunny)

                ArrayAdapter.createFromResource(
                        this@SurveySunnyActivity,
                        R.array.spn_items,
                        android.R.layout.simple_spinner_item
                ).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spn_sunny.adapter = adapter
                }

                spn_sunny.onItemSelectedListener = this
            }


            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
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
            holder.niImage.setImageUrl(mSurveySunnyModel.getImageUrl(position), mSurveySunnyModel.mLoader)
        }

    }
}