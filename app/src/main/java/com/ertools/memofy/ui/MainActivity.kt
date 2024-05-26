package com.ertools.memofy.ui

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ertools.memofy.R
import com.ertools.memofy.databinding.ActivityMainBinding
import com.ertools.memofy.model.MemofyApplication
import com.ertools.memofy.ui.categories.CategoriesViewModel
import com.ertools.memofy.ui.categories.CategoriesViewModelFactory
import com.ertools.memofy.ui.task.TaskViewModel
import com.ertools.memofy.ui.task.TaskViewModelFactory
import com.ertools.memofy.ui.tasks.TasksViewModel
import com.ertools.memofy.ui.tasks.TasksViewModelFactory
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    /** View models **/
    private lateinit var tasksViewModel: TasksViewModel
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var categoriesViewModel: CategoriesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //this.deleteDatabase(Utils.DATABASE_NAME)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureNavigation()
        createViewModels()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

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
            TaskViewModelFactory((applicationContext as MemofyApplication).annexesRepository)
        )[TaskViewModel::class.java]

        categoriesViewModel = ViewModelProvider(
            this,
            CategoriesViewModelFactory((applicationContext as MemofyApplication).categoryRepository)
        )[CategoriesViewModel::class.java]
    }
}