package com.example.ft_hangouts.repository

import androidx.lifecycle.LiveData
import com.example.ft_hangouts.database.ContactsAndChatsDatabase
import com.example.ft_hangouts.models.Contact

// репозиторий специально написан статическим образом
// чтобы его можно было вызывать из ресивера. вью модель
// не подойдет так как она привязана к жизенному циклу
// активити и фрагментов
object ContactsRepository{
    private lateinit var db: ContactsAndChatsDatabase

    fun setDB(database: ContactsAndChatsDatabase) { db = database }

    fun getAllContacts() : LiveData<MutableList<Contact>> = db.getDao().getAll()

    fun getContact(phoneNumber: String) : List<Contact> = db.getDao().getContactByNumber(phoneNumber)

    fun getContactLiveData(phoneNumber: String) : LiveData<List<Contact>> = db.getDao().getContactByNumberWithLiveData(phoneNumber)

    suspend fun insertContact(contact: Contact) : Long = db.getDao().insertContact(contact)

    suspend fun deleteContact(contact: Contact) : Unit = db.getDao().deleteContact(contact)
}