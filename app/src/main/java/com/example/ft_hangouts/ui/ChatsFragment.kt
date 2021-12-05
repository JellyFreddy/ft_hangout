package com.example.ft_hangouts.ui

import android.content.Context
import android.os.Bundle
import android.telephony.SmsManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ft_hangouts.R
import com.example.ft_hangouts.adapter.ChatAdapter
import com.example.ft_hangouts.databinding.FragmentChatBinding
import com.example.ft_hangouts.models.Message
import com.example.ft_hangouts.utils.Constants.FROM_ME
import android.content.Intent
import android.net.Uri


class ChatsFragment : Fragment(R.layout.fragment_chat){
    private lateinit var binding: FragmentChatBinding
    lateinit var viewModel: ContactsViewModel
    lateinit var chatAdapter: ChatAdapter
    private val args: com.example.ft_hangouts.ui.ChatsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // настраиваем байндинг для получения доступа
        // к элементам лэйаута
        binding = FragmentChatBinding.bind(view)

        // получаем вбю модель из активити
        viewModel = (activity as MainActivity).viewModel

        // настраиваем ресайклер вью
        setupRecyclerView()

        val contactName = args.contactNumber
        val observableContact = viewModel.getContactLiveData(contactName)

        observableContact.observe(viewLifecycleOwner) {
            chatAdapter.differ.submitList(observableContact.value?.get(0)?.messages)
        }

        binding.sendBtn.setOnClickListener {

            // получаем сообщение к отправке и проверяем его наличие
            // если все пусто, то не отправляем
            val messageToSend = binding.messageEt.text.toString()
            if (messageToSend.isNullOrEmpty())
                return@setOnClickListener

            // добавляем наше сообщение в базу данных, как отправленное нами
            val contact = observableContact.value?.get(0)
            contact?.messages?.add(Message(messageToSend, FROM_ME))
            viewModel.insert(contact!!)

            // закрываем клавиатуру
            val imm = (activity as MainActivity).applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)

            // отчищаем поле ввода сообщения
            binding.messageEt.text.clear()

            // отправляем смс
            sendSmsMessage(contact.phoneNumber, messageToSend)
        }

        binding.callIv.setOnClickListener {
            makeCall()
        }
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter()
        binding.chatRv.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun makeCall() {
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:" + args.contactNumber) //change the number
        startActivity(callIntent)
    }

    private fun sendSmsMessage(number: String, message: String) {
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(number, null, message, null, null)
    }
}