package com.example.ft_hangouts.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ft_hangouts.models.Contact

@Dao
interface ContactDAO {

    @Query("SELECT * FROM contacts")
    fun getAll(): LiveData<MutableList<Contact>>

    @Query("SELECT * FROM contacts WHERE phoneNumber = :number")
    fun getContactByNumber(number: String): List<Contact>

    @Query("SELECT * FROM contacts WHERE phoneNumber = :number")
    fun getContactByNumberWithLiveData(number: String): LiveData<List<Contact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact): Long

    @Delete
    suspend fun deleteContact(contact: Contact): Unit
}