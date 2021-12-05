package com.example.ft_hangouts.ui

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ft_hangouts.R
import com.example.ft_hangouts.databinding.FragmenCreateNewContactBinding
import com.example.ft_hangouts.models.Contact
import android.content.Intent
import android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.net.Uri
import androidx.navigation.fragment.navArgs

class CreateOrModifyContactFragment: Fragment(R.layout.fragmen_create_new_contact) {
    private lateinit var viewModel: ContactsViewModel
    private lateinit var binding: FragmenCreateNewContactBinding
    private val args: CreateOrModifyContactFragmentArgs by navArgs()
    val PICK_IMAGE = 1
    lateinit var image: Uri

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // устанавливаем дефолтную картинку для нового контакта
        image = Uri.parse("android.resource://com.example.ft_hangouts/drawable/ic_baseline_add_contact")

        // устанавливаем байндинг для работы с элементами лэйаута
        binding = FragmenCreateNewContactBinding.bind(view)

        // получаем из активити вью модель
        viewModel = (activity as MainActivity).viewModel

        binding.profilePhotoIv.setOnClickListener {
            pickAnImage()
        }

        binding.acceptBtn.setOnClickListener {

            // удаляем старый контакт - в любом случае
            // это нужно если вдруг у нас у контакта поменяют номер
            args.contact?.let { viewModel.delete(it) }

            // проверяем ввели ли номер и если ввели, то
            // передаем его для дальнейшей обработки
            val phoneNumber = checkAndGetPhoneNumber()
            phoneNumber ?: return@setOnClickListener

            // создаем контакт
            val newContact = createContact(phoneNumber)

            // добавляем новый контакт
            viewModel.insert(newContact)

            // возвращаемся к главному фрагменту
            (activity as MainActivity).onBackPressed()
        }

        // обрабатываем переданные аргументы, чтобы можно было использовать
        // фрагмент для редактирования существующих контактов
        processArgs()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // проверяем успешно ли прошел интент и была ли выбрана картинка
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE) {

            // извлекаем Uri картинки
            val selectedImageUri = data?.data

            // получаем разрешение необходимое для продолжительной работы с Uri
            // (уберешь, и при перезапуске приложения картинки будут выдавать exception)
            // (попробуешь заменить на BitMap - фрагмент не вывезет, ибо слишком тяжелый
            // parcelable)
            context?.contentResolver?.takePersistableUriPermission(
                selectedImageUri!!,  (FLAG_GRANT_READ_URI_PERMISSION
                        + Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            )

            // вставляем изображение в нужное место в лэйауте
            binding.profilePhotoIv.setImageURI(selectedImageUri)
            // сохраняем выбранную картинку для дальнейшей передачи в контакт,
            // и сохранения в базе данных
            image =  selectedImageUri!!
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // на всякий случай, чтобы фрагмент поменьше хранил ненужной
        // инфы
        outState.clear()
    }

    private fun processArgs() {
        args.contact?.let { contact ->

            // сохраняем переданное фото
            // и устанавливаем его в нужное место
            // в лэйауте
            contact.profilePhoto?.let {
                image = it
                binding.profilePhotoIv.setImageURI(it)
            }

            // устанавливаем в лэйауте информацию по прочим полям
            contact.address?.let { binding.addressEt.setText(it) }
            contact.dateOfBirth?.let { binding.dateOfBirthEt.setText(it) }
            contact.name?.let { binding.nameEt.setText(it) }
            binding.phoneNumberEt.setText(contact.phoneNumber)
        }
    }

    private fun pickAnImage() {
        // создаем нужный интент для выбора объекта
        val photoPickerIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)

        // добавляем флаги
        photoPickerIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION)
        photoPickerIntent.addFlags(FLAG_GRANT_PERSISTABLE_URI_PERMISSION)

        // устанавливаем тип выбираемого объекта
        photoPickerIntent.type = "image/*"

        // запускаем интент
        startActivityForResult(photoPickerIntent, PICK_IMAGE)
    }

    private fun checkAndGetPhoneNumber() : String? {
        val phoneNumber = binding.phoneNumberEt.text.toString()
        if (phoneNumber.isEmpty()) {
            Toast.makeText(this.context, "phone number field has to be filled", Toast.LENGTH_LONG).show()
            return null
        }
        return phoneNumber
    }

    private fun createContact(phoneNumber: String) : Contact {
        var name = binding.nameEt.text.toString()
        if (name.isEmpty())
            name = resources.getString(R.string.new_contact)
        val address = binding.addressEt.text.toString()
        val dateOfBirth = binding.dateOfBirthEt.text.toString()
        return Contact(
            address = address,
            name = name,
            dateOfBirth = dateOfBirth,
            phoneNumber = phoneNumber,
            profilePhoto = image,
            messages = args.contact?.messages ?: mutableListOf()
        )
    }
}
