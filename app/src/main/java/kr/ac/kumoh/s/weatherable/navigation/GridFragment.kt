package kr.ac.kumoh.s.weatherable.navigation

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.toolbox.NetworkImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_grid.view.*
import kr.ac.kumoh.s.weatherable.R
import kr.ac.kumoh.s.weatherable.TourListFragment


class GridFragment : Fragment() {
    var firestore: FirebaseFirestore? = null

    private lateinit var mModel: DetailViewModel
    private val mAdapter = DetailAdapter()

    companion object {
        const val KEY_ID: String = "review_id"
        fun newInstance(string: String?): GridFragment {
            val fragmentGrid = GridFragment()
//            val args = Bundle()
//
//            args.putString("string", string)
//            fragmentGrid.arguments = args

            return fragmentGrid
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mModel = ViewModelProvider(activity as AppCompatActivity).get(DetailViewModel::class.java)
        mModel.list.observe(viewLifecycleOwner, Observer<ArrayList<DetailViewModel.review>> {
            mAdapter.notifyDataSetChanged()
        })

        val root = inflater.inflate(R.layout.fragment_grid, container, false)
        val lsResult = root.findViewById<RecyclerView>(R.id.gridfragment_recyclerview)
        lsResult.apply {
            root?.gridfragment_recyclerview?.layoutManager= GridLayoutManager(activity, 3, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
        }
        return root
    }

    inner class DetailAdapter : RecyclerView.Adapter<DetailAdapter.ViewHolder>() {

        open inner class ViewHolder : RecyclerView.ViewHolder, View.OnClickListener {
            val niImage: NetworkImageView = itemView.findViewById(R.id.image)

            constructor(itemView: View) : super(itemView) {
                itemView.setOnClickListener(this)
                niImage.setDefaultImageResId(R.id.image)
            }

            override fun onClick(p0: View?) {
                val detail = Intent(activity, DetailActivity::class.java)
                detail.putExtra(KEY_ID, mModel.getReview(adapterPosition).postId)
                startActivity(detail)
            }
        }

        override fun getItemCount(): Int {
            return mModel.getSize()
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): DetailAdapter.ViewHolder {
            val view = layoutInflater.inflate(
                R.layout.item,
                parent,
                false)
            return ViewHolder(view)
        }

        inner class CustomViewHolder(var imageview: ImageView) : DetailAdapter.ViewHolder(imageview) { }

        override fun onBindViewHolder(holder: DetailAdapter.ViewHolder, position: Int) {
            holder.niImage.setImageUrl(mModel.getImageUrl(position), mModel.mLoader)


        }

    }

}


