package kr.ac.kumoh.s20171145.weatherable.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_grid.view.*
import kr.ac.kumoh.s20171145.weatherable.R
import kr.ac.kumoh.s20171145.weatherable.navigation.model.ContentDTO

class GridFragment : Fragment(){
    var firestore : FirebaseFirestore? = null
    var fragmentView : View? = null
    var uid : String? = null
    lateinit var detailviewitem_imageview_content: ImageView

    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        uid = arguments?.getString("destinationUid")

        fragmentView = LayoutInflater.from(activity).inflate(R.layout.fragment_grid,container,false)
        firestore = FirebaseFirestore.getInstance()
        fragmentView?.gridfragment_recyclerview?.adapter = UserFragmentRecyclerViewAdapter()
        fragmentView?.gridfragment_recyclerview?.layoutManager =
            GridLayoutManager(activity, 3)


        return fragmentView
    }

    companion object{
        const val KEY_ID :String = "id"
    }

    inner class UserFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
        var contentUidList : ArrayList<String> = arrayListOf()

        init {
            firestore?.collection("images")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentUidList.clear()
                //Sometimes, This code return null of querySnapshot when it signout
                if(querySnapshot == null) return@addSnapshotListener

                //Get data
                for(snapshot in querySnapshot.documents){
                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                    contentUidList.add(snapshot.id)

                }
                notifyDataSetChanged()
            }
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
            val detailImg: ImageView
            val detailExp: TextView


            init {
                var view:View = itemView
                detailExp = itemView.findViewById<TextView>(R.id.detailviewitem_explain_textview)
                detailImg = itemView.findViewById(R.id.detailviewitem_imageview_content)
                itemView.setOnClickListener(this)
            }

            override fun onClick(p0: View?){
                val detail = Intent(activity, DetailViewFragment::class.java)
//                detail.putExtra(KEY_ID, "안녕")
//                startActivity(detail)
            }
        }

        //클릭리스너 선언
        private lateinit var itemClickListner: ItemClickListener


        //클릭리스너 등록 매소드
        fun setItemClickListener(itemClickListener: ItemClickListener) {
            this.itemClickListner = itemClickListener

            var homeFragment = DetailViewFragment()
        }




        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            val width = resources.displayMetrics.widthPixels / 3

            val imageview = ImageView(p0.context)
            imageview.layoutParams = LinearLayoutCompat.LayoutParams(width,width)

            return CustomViewHolder(imageview)
        }

        inner class CustomViewHolder(var imageview: ImageView) : RecyclerView.ViewHolder(imageview) {

        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var imageview = (holder as CustomViewHolder).imageview
            Glide.with(holder.itemView.context).load(contentDTOs[position].imageUrl).apply(RequestOptions().centerCrop()).into(imageview)

            holder.itemView.setOnClickListener {
                var fragment = DetailViewFragment()
                var bundle = Bundle()


                bundle.putString("destinationImg",contentDTOs[position].imageUrl)
//                bundle.putString("userId",contentDTOs[position].userId)
                selectWhereDoc(contentDTOs[position].imageUrl.toString())
                print("퍼가요" + contentDTOs[position].imageUrl.toString())

                fragment.arguments = bundle
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_content,fragment)?.commit()
            }

        }

        private fun selectWhereDoc(url: String) {
            firestore?.collection("images")?.whereEqualTo("imageUrl", url)?.get()?.addOnCompleteListener(
                OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        print("확인!")
                    }


                }
            )
        }


    }
}
