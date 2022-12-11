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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.toolbox.NetworkImageView
import kotlinx.android.synthetic.main.fragment_grid.view.*
import kr.ac.kumoh.s.weatherable.R


class GridFragment : Fragment() {
    private lateinit var mModel: GridViewModel
    private val mAdapter = GridAdapter()

    companion object {
        const val KEY_ID: String = "post-id"
        fun newInstance(string: String?): GridFragment {
            val fragmentGrid = GridFragment()
            return fragmentGrid
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mModel = ViewModelProvider(activity as AppCompatActivity).get(GridViewModel::class.java)
        mModel.list.observe(viewLifecycleOwner, {
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

    inner class GridAdapter : RecyclerView.Adapter<GridAdapter.ViewHolder>() {

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
        ): GridAdapter.ViewHolder {
            val view = layoutInflater.inflate(
                R.layout.item,
                parent,
                false)
            return ViewHolder(view)
        }

        inner class CustomViewHolder(var imageview: ImageView) : GridAdapter.ViewHolder(imageview) { }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.niImage.setImageUrl(mModel.getImageUrl(position), mModel.mLoader)
/*            var imageview = (holder as CustomViewHolder).imageview
            Glide.with(holder.itemView.context).load(mModel.getImageUrl(position)).apply(
                RequestOptions().centerCrop()).into(imageview)*/
        }
    }
}


