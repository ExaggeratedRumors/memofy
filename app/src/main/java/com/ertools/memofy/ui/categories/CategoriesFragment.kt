package com.ertools.memofy.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ertools.memofy.R
import com.ertools.memofy.databinding.FragmentCategoriesBinding
import com.ertools.memofy.model.MemofyApplication

class CategoriesFragment : Fragment() {
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private val categoriesViewModel: CategoriesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        binding.categoriesRecycler.layoutManager = LinearLayoutManager(requireContext())

        configureCategoryAdapter(categoriesViewModel)
        configureAddCategoryButton()

        return binding.root
    }

    private fun configureCategoryAdapter(categoriesViewModel: CategoriesViewModel) {
        categoriesViewModel.categories.observe(viewLifecycleOwner) {
            val categoriesAdapter = CategoriesAdapter(requireContext(), categoriesViewModel, it)
            binding.categoriesRecycler.adapter = categoriesAdapter
        }
    }

    private fun configureAddCategoryButton() {
        binding.categoriesAddButton.setOnClickListener {
            findNavController().navigate(R.id.action_nav_categories_to_nav_category)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}