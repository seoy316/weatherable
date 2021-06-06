package kr.ac.kumoh.s20170998.weatherable

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton


class TourListFragment : BottomSheetDialogFragment() {
    private lateinit var mTourListModel: TourListViewModel
    private val mTourListAdapter = TourListAdapter()
    var fabClose: FloatingActionButton? = null
    var x : Double? =  0.0// 경도
    var y : Double? = 0.0// 위도

    companion object {
        fun newInstance(string: String?): TourListFragment {
            val fragmentTourList = TourListFragment()
            val args = Bundle()

            args.putString("string", string)
            fragmentTourList.arguments = args

            return fragmentTourList
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (view?.parent as View).setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mTourListModel = ViewModelProvider(activity as AppCompatActivity).get(TourListViewModel::class.java)
        mTourListModel.tour_list.observe(viewLifecycleOwner, Observer<ArrayList<TourListViewModel.TourList>> {mTourListAdapter.notifyDataSetChanged()})


        val root = inflater.inflate(R.layout.fragment_tour_list, container, false)
        val rvTourList = root.findViewById<RecyclerView>(R.id.rvTourList)
        rvTourList.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = mTourListAdapter
        }

        fabClose = root.findViewById(R.id.fabClose)
        fabClose?.setOnClickListener {
            dismiss()
        }

        var bundle = this.arguments
        if (bundle != null) {
            x = bundle?.getDouble("x")
            y = bundle?.getDouble("y")
            mTourListModel.postJSON(x,y)

        }
        println("fragment x : $x, y : $y") // test code

        return root
    }


    inner class TourListAdapter: RecyclerView.Adapter<TourListAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val txName: TextView
            val txAddress: TextView

            init {
                txName = itemView.findViewById(R.id.place_name)
                txAddress = itemView.findViewById(R.id.place_address)
            }
        }

        override fun getItemCount(): Int {
            return mTourListModel.getSize()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_tour_list,
                    parent,
                    false
                )

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.txName.text = mTourListModel.getTourList(position).name
            holder.txAddress.text = mTourListModel.getTourList(position).address
        }
    }
}