package com.tinder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
                                    // 모델, 뷰홀더
class CardItemAdapter: ListAdapter<CardItem, CardItemAdapter.ViewHolder>(diffUtil) {

    // 카드아이템어뎁터의 뷰홀더를 추가해 주기위해 이너클래스 생성성, 여기는 뷰바인딩 아님
   inner class ViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        // 아이템을 받아와서 초기화해주는 바인드함수생성
        fun bind(cardItem: CardItem){
            view.findViewById<TextView>(R.id.nameTextView).text = cardItem.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // 이전에는 뷰바인딩에서 레이아웃인플레잇을 했는데 이번엔 뷰바인딩 아니니까 인플레잇 따로 만듦
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_card, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //아이템을 가져와서 바인드함수 호출
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<CardItem>() {
            override fun areItemsTheSame(oldItem: CardItem, newItem: CardItem): Boolean {
                return oldItem.userId == newItem.userId
            }

            override fun areContentsTheSame(oldItem: CardItem, newItem: CardItem): Boolean {
                return oldItem == newItem
            }

        }
    }
}

