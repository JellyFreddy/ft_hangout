package com.example.ft_hangouts.broadcastReceivers

import android.R.string.unknownName
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsMessage
import com.example.ft_hangouts.models.Contact
import com.example.ft_hangouts.models.Message
import com.example.ft_hangouts.repository.ContactsRepository
import com.example.ft_hangouts.utils.Constants.PACKAGE_NAME
import com.example.ft_hangouts.utils.Constants.TO_ME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.StringBuilder

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        // инициализируем дефолтную картинку профиля
        // на случай, если это новый контакт
        val image = Uri.parse("android.resource://${PACKAGE_NAME}/drawable/ic_baseline_add_contact")

        // получаем из интента информацию к обработке
        val bundle: Bundle? = intent.extras
        val smsMessages: Array<SmsMessage?>
        if (bundle != null) {
            // получаем protocol data units
            val msgObjects: Array<*>? = bundle.get("pdus") as Array<*>?

            // создаем пустой массив нужного размера, чтобы потом заполнить его
            // смс сообщениями
            smsMessages = arrayOfNulls(msgObjects!!.size)
            for (i in smsMessages.indices) {

                // извлекаем из смс текст сообщения
                val body = StringBuilder()
                smsMessages[i] = SmsMessage.createFromPdu(msgObjects[i] as ByteArray?)
                body.append(smsMessages[i]!!.messageBody)

                // извлекаем из смс номер отправителя
                val number = smsMessages[i]!!.originatingAddress.toString()

                addOrModifyContactInDataBase(number, context, image, body)
            }
        }
    }

    private fun addOrModifyContactInDataBase(number: String, context: Context, image: Uri?, body: StringBuilder) {

        // создаем версию контакта, которая будет использована в случай если
        // у нас нет данного контакта в базе данных
        var contact = Contact(
            number,
            context.resources.getString(unknownName),
            null,
            null,
            image,
            mutableListOf(Message(body.toString(), TO_ME))
        )

        CoroutineScope(Dispatchers.IO).launch {
            // проверяем есть ли у нас контакт в базе данных
            val listOfContact = ContactsRepository.getContact(number)

            // если есть, то мы добавим ему прешедшее сообщение
            // и добавим его в базу данных
            if (listOfContact.isNotEmpty()) {
                contact = listOfContact.get(0).apply {
                    messages.add(Message(body.toString(), TO_ME))
                }
            }
            ContactsRepository.insertContact(contact)
        }
    }
}