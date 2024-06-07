package com.ertools.memofy

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ertools.memofy.database.MemofyApplication
import com.ertools.memofy.databinding.ActivityMainBinding
import com.ertools.memofy.notification.TaskNotificationManager
import com.ertools.memofy.ui.categories.CategoriesViewModel
import com.ertools.memofy.ui.categories.CategoriesViewModelFactory
import com.ertools.memofy.ui.settings.SettingsViewModel
import com.ertools.memofy.ui.task.TaskViewModel
import com.ertools.memofy.ui.task.TaskViewModelFactory
import com.ertools.memofy.ui.tasks.TasksViewModel
import com.ertools.memofy.ui.tasks.TasksViewModelFactory
import com.ertools.memofy.utils.Utils
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    /** View models **/
    private lateinit var tasksViewModel: TasksViewModel
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var categoriesViewModel: CategoriesViewModel
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createViewModels()
        configureNavigation()
        configureNotifications()
    }

    /** Create menu **/
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    /** Service menu **/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                findNavController(R.id.nav_host_fragment_content_main)
                    .navigate(R.id.action_global_to_nav_settings)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /** Handle navigation **/
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun configureNavigation() {
        setSupportActionBar(binding.appBarMain.appBarToolbar)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_tasks, R.id.nav_categories
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun configureNotifications() {
        TaskNotificationManager().configureNotificationChannel(this)
    }

    private fun createViewModels() {
        tasksViewModel = ViewModelProvider(
            this,
            TasksViewModelFactory(
                (applicationContext as MemofyApplication).taskRepository,
                (applicationContext as MemofyApplication).categoryRepository
            )
        )[TasksViewModel::class.java]

        taskViewModel = ViewModelProvider(
            this,
            TaskViewModelFactory(
                (applicationContext as MemofyApplication).taskRepository,
                (applicationContext as MemofyApplication).annexesRepository
            )
        )[TaskViewModel::class.java]

        categoriesViewModel = ViewModelProvider(
            this,
            CategoriesViewModelFactory((applicationContext as MemofyApplication).categoryRepository)
        )[CategoriesViewModel::class.java]

        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
    }

    private fun deleteDatabase() {
        this.deleteDatabase(Utils.DATABASE_NAME)
    }
}