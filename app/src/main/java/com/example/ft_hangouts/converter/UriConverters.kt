package com.example.ft_hangouts.converter

import android.net.Uri
import androidx.room.TypeConverter

class UriConverters {

    @TypeConverter
    fun fromUriToString(uri: Uri): String {
        return uri.toString()
    }

    @TypeConverter
    fun toUri(string: String): Uri {
        return Uri.parse(string)
    }
}