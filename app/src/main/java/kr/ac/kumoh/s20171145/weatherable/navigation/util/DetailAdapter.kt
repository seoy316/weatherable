package kr.ac.kumoh.s20171145.weatherable.navigation.util

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.core.View
import kr.ac.kumoh.s20171145.weatherable.navigation.DetailViewFragment
import kr.ac.kumoh.s20171145.weatherable.navigation.model.ContentDTO


class DetailAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //데이터를 저장할 아이템리스트
    val items = ArrayList<ContentDTO>()


    //클릭 인터페이스 정의
    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }

    //클릭리스너 선언
    private lateinit var itemClickListner: ItemClickListener

    //클릭리스너 등록 매소드
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var view = holder as DetailViewFragment.DetailViewRecyclerViewAdapter.CustomViewHolder
//        view!!.text = items[position].price.toString()

        //view에 onClickListner를 달고, 그 안에서 직접 만든 itemClickListener를 연결시킨다
        holder.itemView.setOnClickListener {
//            itemClickListner.onClick(it, position)
        }
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}


