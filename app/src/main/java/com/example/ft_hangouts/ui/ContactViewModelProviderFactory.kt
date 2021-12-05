package com.example.ft_hangouts.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ft_hangouts.repository.ContactsRepository

class ContactViewModelProviderFactory: ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ContactsViewModel(ContactsRepository) as T
    }
}