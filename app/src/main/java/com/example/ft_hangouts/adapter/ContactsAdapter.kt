package com.example.ft_hangouts.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ft_hangouts.R
import com.example.ft_hangouts.models.Contact

class ContactsAdapter: RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var onTextClickListener: ((Contact) -> Unit)? = null
    private var onArrowClickListener: ((Contact) -> Unit)? = null

    private val differCallback = object : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return (oldItem.phoneNumber == newItem.phoneNumber)
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return (oldItem == newItem)
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_small_contact,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = differ.currentList[position]
        holder.itemView.apply {
            val profileName = findViewById<TextView>(R.id.profile_name_tv)
            val profilePicture = findViewById<ImageView>(R.id.profile_photo_iv)
            val arrowImage = findViewById<ImageView>(R.id.just_an_arrow_iv)

            profilePicture.setImageURI(contact.profilePhoto)

            profileName.apply {
                text = contact.name
                setOnClickListener {
                    onTextClickListener?.let { listenerFunction ->
                        listenerFunction(contact)
                    }
                }
            }

            arrowImage.setOnClickListener {
                onArrowClickListener?.let { listenerFunction ->
                    listenerFunction(contact)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun setOnTextClickListener(listener: (Contact) -> Unit) {
        onTextClickListener = listener
    }

    fun setOnArrowClickListener(listener: (Contact) -> Unit) {
        onArrowClickListener = listener
    }
}
