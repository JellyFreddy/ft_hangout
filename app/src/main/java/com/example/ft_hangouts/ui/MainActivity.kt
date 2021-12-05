package com.example.ft_hangouts.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.ft_hangouts.R
import com.example.ft_hangouts.database.ContactsAndChatsDatabase
import com.example.ft_hangouts.databinding.ActivityMainBinding
import com.example.ft_hangouts.repository.ContactsRepository
import com.example.ft_hangouts.utils.Constants.BLACK_TOOLBAR
import com.example.ft_hangouts.utils.Constants.BLUE_TOOLBAR
import com.example.ft_hangouts.utils.Constants.PACKAGE_NAME
import java.util.*

class MainActivity : AppCompatActivity() {

    // вью модель не приватная, чтобы использовать ее
    // во фрагментах
    lateinit var viewModel: ContactsViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    // костылик ))
    // не смог разобраться как получать цвет фона тулбара
    private var toolbarColor = BLUE_TOOLBAR

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // настраиваем баиндинг, чтобы иметь доступ к UI
        // элементам xml
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // настраиваем навигацию фрагментов
        setupNavigation()

        // настраиваем тулбар вместо убранного экшн бара
        setupToolBar()

        // получаем необходимые разрешения
        getAllNecessaryPermissions()

        // настраиваем репозиторий и вью модель
        setupRepositoryAndViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun changeToolbarColor() {
        when (toolbarColor) {
            BLACK_TOOLBAR -> {
                toolbarColor = BLUE_TOOLBAR
                binding.toolbar.setBackgroundColor(resources.getColor(R.color.pacific_blue))
            }
            else -> {
                toolbarColor = BLACK_TOOLBAR
                binding.toolbar.setBackgroundColor(resources.getColor(R.color.black))
            }
        }
    }

    private fun setupToolBar() {
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController)
    }

    private fun getAllNecessaryPermissions() {

        // получаем разрешение на получение смс сообщений
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECEIVE_SMS), 1000)
        }

        // получаем разрешение на отправку смс сообщений
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.SEND_SMS), 1001)
        }

        // получаем разрешение на телефонный звонок
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 1002)
        }
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()
    }

    private fun setupRepositoryAndViewModel() {

        // добавляем базу данных в репозиторий.
        ContactsRepository.setDB(ContactsAndChatsDatabase(this))

        // создаем вью модель. делаем это через фабрику, чтобы у нас
        // не терялись данны при изменении состояния (смене языка, смене
        // ориентации телефона)
        val viewModelProviderFactory = ContactViewModelProviderFactory()
        viewModel = ViewModelProvider(this, viewModelProviderFactory)
            .get(ContactsViewModel::class.java)
    }

    override fun onStop() {
        super.onStop()

        // получаем shared preferences и записываем в них
        // время ухода приложения в фон
        val sharedPreferences = this.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE)
        val timeKey = "${PACKAGE_NAME}.datetime"
        val inBackgroundTime = Calendar.getInstance().time
        sharedPreferences.edit().putLong(timeKey, inBackgroundTime.time).apply()
    }

    override fun onStart() {
        super.onStart()

        // получаем shared preferences и извлекаем из них
        // время ухода приложения в фоновый режим
        val sharedPreferences = this.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE)
        val timeKey = "${PACKAGE_NAME}.datetime"
        val inBackgroundTime = Date()
        sharedPreferences.getLong(timeKey, inBackgroundTime.time)
        Toast.makeText(this, inBackgroundTime.toString(), Toast.LENGTH_LONG).show()
    }
}