package kr.ac.kumoh.s.weatherable.navigation

import DetailActivity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.NetworkImageView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kr.ac.kumoh.s.weatherable.R
import kr.ac.kumoh.s.weatherable.navigation.model.AlarmDTO
import kr.ac.kumoh.s.weatherable.navigation.model.ContentDTO
import kr.ac.kumoh.s.weatherable.navigation.util.FcmPush
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.item_detail.view.*

class DetailViewFragment : Fragment(){
    var index: Int = 0
    var firestore : FirebaseFirestore? = null
    var uid : String? = null
    var img : String? = null

    companion object {
        const val KEY_ID : String = "review_id"
    }

    private lateinit var mModel: DetailViewModel
    private val mAdapter = ReviewAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val bundle = arguments
        if (bundle != null)
            index = bundle.getInt("index")

        img = arguments?.getString("destinationImg")

        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_detail,container,false)
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        view.detailviewfragment_recyclerview.adapter = DetailViewRecyclerViewAdapter()
        view.detailviewfragment_recyclerview.layoutManager = LinearLayoutManager(activity)
        return view
    }

    inner class ReviewAdapter: RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {
        inner class ViewHolder: RecyclerView.ViewHolder, View.OnClickListener{
            val txName: TextView
            val niImage: NetworkImageView = itemView.findViewById<NetworkImageView>(R.id.image)


            constructor(itemView: View): super(itemView){
                txName = itemView.findViewById<TextView>(R.id.detailviewitem_explain_textview)
                itemView.setOnClickListener(this)
                niImage.setDefaultImageResId(R.id.detailviewitem_imageview_content)
            }

            override fun onClick(p0: View?){
                val detail = Intent(activity, DetailActivity::class.java)
                detail.putExtra(KEY_ID, mModel.getReview(adapterPosition).imageUrl)
                startActivity(detail)
            }
        }

        override fun getItemCount(): Int {
            return mModel.getSize()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewAdapter.ViewHolder {
            val view = layoutInflater.inflate(
                R.layout.item_detail,
                parent,
                false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ReviewAdapter.ViewHolder, position: Int) {
            holder.txName.text = mModel.getReview(position).explain
            holder.niImage.setImageUrl(mModel.getImageUrl(position), mModel.mLoader)
        }
    }

    inner class DetailViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
        var contentUidList : ArrayList<String> = arrayListOf()

        init {
            firestore?.collection("images")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentDTOs.clear()
                contentUidList.clear()
                //Sometimes, This code return null of querySnapshot when it signout
                if(querySnapshot == null) return@addSnapshotListener

                for(snapshot in querySnapshot!!.documents){
                    var item = snapshot.toObject(ContentDTO::class.java)
                    contentDTOs.add(item!!)
                    contentUidList.add(snapshot.id)
                }
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(p0.context).inflate(R.layout.item_detail,p0,false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            var viewholder = (p0 as CustomViewHolder).itemView

            //UserId
            viewholder.detailviewitem_profile_textview.text = contentDTOs!![p1].userId

            //Image
            Glide.with(p0.itemView.context).load(contentDTOs!![p1].imageUrl).into(viewholder.detailviewitem_imageview_content)

            //Explain of content
            viewholder.detailviewitem_explain_textview.text = contentDTOs!![p1].explain
            print("테스트 ${viewholder.detailviewitem_explain_textview.text}")

            //likes
            viewholder.detailviewitem_favoritecounter_textview.text = "Likes " + contentDTOs!![p1].favoriteCount

            //This code is when the page is loaded
            if(contentDTOs!![p1].favorites.containsKey(uid)){
                //This is like status
                viewholder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favorite)

            }else{
                //This is unlike status
                viewholder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favorite_border)
            }

            //This code is when the profile image is clicked
            viewholder.detailviewitem_profile_image.setOnClickListener {
                var fragment = GridFragment()
                var bundle = Bundle()
                bundle.putString("destinationUid",contentDTOs[p1].uid)
                bundle.putString("userId",contentDTOs[p1].userId)
                fragment.arguments = bundle
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_content,fragment)?.commit()
            }

            viewholder.detailviewitem_comment_imageview.setOnClickListener { v ->
                var intent = Intent(v.context,CommentActivity::class.java)
                intent.putExtra("contentUid",contentUidList[p1])
                intent.putExtra("destinationUid",contentDTOs[p1].uid)
                startActivity(intent)
            }
        }

    }
}