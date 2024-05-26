package com.ertools.memofy.ui.category

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.ertools.memofy.R
import com.ertools.memofy.databinding.FragmentCategoryBinding
import com.ertools.memofy.model.categories.Category
import com.ertools.memofy.ui.categories.CategoriesViewModel

class CategoryFragment : Fragment() {
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private val categoriesViewModel: CategoriesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)

        configureMenu()
        configureColorPicker()
        return binding.root
    }

    private fun configureMenu() {
        requireActivity().addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.findItem(R.id.action_settings).isVisible = false
                menuInflater.inflate(R.menu.new_form, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId) {
                    R.id.action_save -> {
                        saveCategory()
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun configureColorPicker() {
        binding.categoryColorRed.colorPickerName.text = "R"
        binding.categoryColorRed.colorPickerBar.setOnSeekBarChangeListener(
            object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    binding.categoryColorPreview.setBackgroundColor(getColor())
                    binding.categoryColorRed.colorPickerValue.text = progress.toString()
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            }
        )

        binding.categoryColorGreen.colorPickerName.text = "G"
        binding.categoryColorGreen.colorPickerBar.setOnSeekBarChangeListener(
            object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    binding.categoryColorPreview.setBackgroundColor(getColor())
                    binding.categoryColorGreen.colorPickerValue.text = progress.toString()
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            }
        )

        binding.categoryColorBlue.colorPickerName.text = "B"
        binding.categoryColorBlue.colorPickerBar.setOnSeekBarChangeListener(
            object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    binding.categoryColorPreview.setBackgroundColor(getColor())
                    binding.categoryColorBlue.colorPickerValue.text = progress.toString()
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            }
        )
    }

    private fun getColor(): Int {
        val red = binding.categoryColorRed.colorPickerBar.progress
        val green = binding.categoryColorGreen.colorPickerBar.progress
        val blue = binding.categoryColorBlue.colorPickerBar.progress
        return Color.parseColor(String.format("#%02X%02X%02X", red, green, blue))
    }

    private fun saveCategory(): Boolean {
        val title = binding.categoryTitleInput.editText?.text.toString()
        val color = getColor()

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