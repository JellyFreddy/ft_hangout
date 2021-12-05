package com.example.ft_hangouts.converter

import androidx.room.TypeConverter
import com.example.ft_hangouts.models.Message
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListMessageConverters {

    @TypeConverter
    fun fromMessageToString(message: MutableList<Message>): String {
        val gson = Gson()
        return gson.toJson(message)
    }

    @TypeConverter
    fun fromStringToMessage(json: String): MutableList<Message> {
        val type = object : TypeToken<MutableList<Message>>() {}.type
        val gson = Gson()
        return gson.fromJson(json, type)
    }
}