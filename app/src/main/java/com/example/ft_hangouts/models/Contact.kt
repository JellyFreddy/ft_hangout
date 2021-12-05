package com.example.ft_hangouts.models

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = false)
    val phoneNumber: String,
    @ColumnInfo(name = "name")
    val name: String?,
    @ColumnInfo(name = "address")
    val address: String?,
    @ColumnInfo(name = "date_of_birth")
    val dateOfBirth: String?,
    @ColumnInfo(name = "profile_picture")
    val profilePhoto: Uri?,
    @ColumnInfo(name = "messages")
    val messages: MutableList<Message>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Uri::class.java.classLoader),
        mutableListOf<Message>().apply {
            val size = parcel.readInt()
            for( i in 0..size)
                this.add(i, parcel.readParcelable(Message::class.java.classLoader)!!)
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(phoneNumber)
        parcel.writeString(name)
        parcel.writeString(address)
        parcel.writeString(dateOfBirth)
        parcel.writeParcelable(profilePhoto, flags)
        parcel.writeInt(messages.size)
        for(current in messages)
            parcel.writeParcelable(current, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Contact> {
        override fun createFromParcel(parcel: Parcel): Contact {
            return Contact(parcel)
        }

        override fun newArray(size: Int): Array<Contact?> {
            return arrayOfNulls(size)
        }
    }
}