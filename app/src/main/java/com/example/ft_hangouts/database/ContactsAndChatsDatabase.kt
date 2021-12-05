package com.example.ft_hangouts.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.ft_hangouts.converter.UriConverters
import com.example.ft_hangouts.converter.ListMessageConverters
import com.example.ft_hangouts.database.dao.ContactDAO
import com.example.ft_hangouts.models.Contact

@Database(
    entities = [Contact::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(UriConverters::class, ListMessageConverters::class)
abstract class ContactsAndChatsDatabase: RoomDatabase() {
    abstract fun getDao(): ContactDAO

    companion object {
        @Volatile
        private var instance: ContactsAndChatsDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) =
            instance ?: synchronized(LOCK) {
                buildDatabase(context).also { instance = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ContactsAndChatsDatabase::class.java,
                "contacts.db"
            ).build()
    }
}