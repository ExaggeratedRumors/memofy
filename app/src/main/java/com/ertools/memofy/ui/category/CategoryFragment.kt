package com.ertools.memofy.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.databinding.Bindable
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ertools.memofy.R
import com.ertools.memofy.databinding.FragmentCategoryBinding
import com.ertools.memofy.model.MemofyApplication
import com.ertools.memofy.model.categories.Category
import com.ertools.memofy.ui.categories.CategoriesViewModel
import com.ertools.memofy.ui.categories.CategoriesViewModelFactory

class CategoryFragment : Fragment() {
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var categoriesViewModel: CategoriesViewModel
    val selectedColorText = MutableLiveData<String>()
    val selectedColor = MutableLiveData<Int>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        val categoryRepository = (requireContext().applicationContext as MemofyApplication).categoryRepository
        categoriesViewModel = ViewModelProvider(
            this, CategoriesViewModelFactory(categoryRepository)
        )[CategoriesViewModel::class.java]

        configureMenu()
        configureColorPicker()
        return binding.root
    }

    private fun configureMenu() {
        requireActivity().addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.findItem(R.id.action_settings).isVisible = false
                menuInflater.inflate(R.menu.form, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId) {
                    R.id.action_save -> {
                        Toast.makeText(requireContext(), "Save", Toast.LENGTH_SHORT).show()
                        saveCategory()
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun configureColorPicker() {
        binding.categoryColorRed.colorPickerName.text = "R"
        binding.categoryColorGreen.colorPickerName.text = "G"
        binding.categoryColorBlue.colorPickerName.text = "B"
    }

    private fun saveCategory(): Boolean {
        val title = binding.categoryTitleInput.editText?.text.toString()
        val red = binding.categoryColorRed.colorPickerValue.text.toString().toInt()
        val green = binding.categoryColorGreen.colorPickerValue.text.toString().toInt()
        val blue = binding.categoryColorBlue.colorPickerValue.text.toString().toInt()
        val color = android.graphics.Color.rgb(red, green, blue)

        if(title.isEmpty()) {
            Toast.makeText(requireContext(), "Invalid title", Toast.LENGTH_SHORT).show()
            return false
        }
        val category = Category(
            title,
            color
        )
        categoriesViewModel.insertCategory(category)
        findNavController().navigate(R.id.action_nav_category_to_nav_categories)
        return true
    }
}