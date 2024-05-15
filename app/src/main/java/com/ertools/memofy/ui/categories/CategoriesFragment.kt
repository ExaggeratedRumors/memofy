package com.ertools.memofy.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ertools.memofy.databinding.FragmentCategoriesBinding
import com.ertools.memofy.model.MemofyApplication

class CategoriesFragment : Fragment() {
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        binding.categoriesRecycler.layoutManager = LinearLayoutManager(requireContext())


        val categoryRepository = (requireContext().applicationContext as MemofyApplication).categoryRepository
        val categoriesViewModel = ViewModelProvider(
            this, CategoriesViewModelFactory(categoryRepository)
        )[CategoriesViewModel::class.java]

        configureCategoryAdapter(categoriesViewModel)
        configureAddCategoryButton()

        return binding.root
    }

    private fun configureCategoryAdapter(categoriesViewModel: CategoriesViewModel) {
        categoriesViewModel.categories.observe(viewLifecycleOwner) {
            val categoriesAdapter = CategoriesAdapter(requireContext(), it)
            binding.categoriesRecycler.adapter = categoriesAdapter
        }
    }

    private fun configureAddCategoryButton() {
        binding.categoriesAddButton.setOnClickListener {
            //findNavController().navigate(R.id.action_nav_tasks_to_nav_task)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}