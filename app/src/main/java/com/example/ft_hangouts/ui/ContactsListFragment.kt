package com.example.ft_hangouts.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ft_hangouts.R
import com.example.ft_hangouts.adapter.ContactsAdapter
import com.example.ft_hangouts.databinding.FragmentContactsBinding
import com.google.android.material.snackbar.Snackbar

class ContactsListFragment: Fragment(R.layout.fragment_contacts) {
    private lateinit var binding: FragmentContactsBinding
    lateinit var viewModel: ContactsViewModel
    lateinit var contactsAdapter: ContactsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // настраиваем биндинг для доступа к элементам лэйаута
        binding = FragmentContactsBinding.bind(view)

        // получаем вью модель из активити
        viewModel = (activity as MainActivity).viewModel

        // открываем окно добавления нового контакта
        binding.addContactFbt.setOnClickListener {
            val action = ContactsListFragmentDirections.actionContactsListFragmentToCreateContactFragment(resources.getString(R.string.new_contact))
            findNavController().navigate(action)
        }

        setupRecyclerView()

        // наблюдаем за списком контактов и
        // обнавляем ресайклер вью при изменениях
        viewModel.getAllContacts().observe(viewLifecycleOwner, { contacts ->
            contacts?.let {
                contactsAdapter.differ.submitList(contacts)
            }
        })

        // создаем колбэк чтобы можно было при его помощи смахиванием
        // удалять контакты
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                // определяем нужный контакт и удаляем его
                val position = viewHolder.adapterPosition
                val contact = contactsAdapter.differ.currentList[position]
                viewModel.delete(contact)

                // возможность восстановления удаленного контакта
                Snackbar.make(view, getString(R.string.deletion_message), Snackbar.LENGTH_LONG).apply {
                    setAction(getString(R.string.undo_deletion_message)) {
                        viewModel.insert(contact)
                    }
                }.show()
            }
        }

        // добавляем возможность удалять контакты смахиванием
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.contactsListRv)
        }
    }

    private fun setupRecyclerView() {

        // создаем адаптер и присваиваем его переменной
        // чтобы по ней можно было обновлять измененный список
        contactsAdapter = ContactsAdapter()

        // устанавливаем листенер для перехода к окну
        // контакта
        contactsAdapter.setOnTextClickListener { contact ->
            val action = ContactsListFragmentDirections.actionContactsListFragmentToCreateContactFragment(contact.name ?: resources.getString(R.string.new_contact))
            action.contact = contact
            findNavController().navigate(action)
        }

        // устанавливаем листенер для перехода к окну
        // чата
        contactsAdapter.setOnArrowClickListener { contact ->
            val action = ContactsListFragmentDirections.actionContactsListFragmentToChatsFragment(contact.phoneNumber, contact.name ?: resources.getString(R.string.new_contact))
            findNavController().navigate(action)
        }

        // добавляем все в ресайклер вью
        binding.contactsListRv.apply {
            adapter = contactsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }


}