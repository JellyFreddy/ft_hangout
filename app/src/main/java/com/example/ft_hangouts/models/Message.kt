package com.example.ft_hangouts.models

import android.os.Parcel
import android.os.Parcelable


data class Message(
    val content: String,
    val direction: Int
) : Parcelable {
    private var messageId: Int = MESSAGE_ID

    init {
        MESSAGE_ID++
    }

    fun getId() = messageId

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(content)
        parcel.writeInt(direction)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Message> {
        override fun createFromParcel(parcel: Parcel): Message {
            return Message(parcel)
        }

        override fun newArray(size: Int): Array<Message?> {
            return arrayOfNulls(size)
        }

        var MESSAGE_ID = 0
    }
}