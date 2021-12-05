package com.example.ft_hangouts.ui

import androidx.lifecycle.ViewModel
import com.example.ft_hangouts.models.Contact
import com.example.ft_hangouts.repository.ContactsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactsViewModel(
    val rep: ContactsRepository
): ViewModel() {

    fun getAllContacts() = rep.getAllContacts()

    fun getContact(phoneNumber: String) = rep.getContact(phoneNumber)

    fun getContactLiveData(phoneNumber: String) = rep.getContactLiveData(phoneNumber)

    fun insert(contact: Contact) = CoroutineScope(Dispatchers.IO).launch {
        rep.insertContact(contact)
    }

    fun delete(contact: Contact) = CoroutineScope(Dispatchers.IO).launch {
        rep.deleteContact(contact)
    }
}