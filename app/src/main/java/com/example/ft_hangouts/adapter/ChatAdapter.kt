package com.example.ft_hangouts.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ft_hangouts.R
import com.example.ft_hangouts.models.Message
import com.example.ft_hangouts.utils.Constants.TO_ME

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.MessagesViewHolder>() {

    inner class MessagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallBack = object : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return (newItem.getId() == oldItem.getId())
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return (newItem == oldItem)
        }

    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {
        val layout = when(viewType) {
            TO_ME -> LayoutInflater.from(parent.context).inflate(
                R.layout.item_to_me_message,
                parent,
                false
            )
            else -> LayoutInflater.from(parent.context).inflate(
                R.layout.item_from_me_message,
                parent,
                false
            )
        }
        return MessagesViewHolder(layout)
    }

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        val messageText = differ.currentList[position].content
        holder.itemView.apply {
            val messageTextView = findViewById<TextView>(R.id.message_tv)
            messageTextView.text = messageText
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return differ.currentList[position].direction
    }
}