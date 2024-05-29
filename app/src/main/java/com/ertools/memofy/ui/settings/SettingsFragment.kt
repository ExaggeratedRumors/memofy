package com.ertools.memofy.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.ertools.memofy.R
import com.ertools.memofy.databinding.FragmentSettingsBinding
import com.ertools.memofy.utils.Utils
import com.google.android.material.snackbar.Snackbar

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        configureMenu()
        configureSettings()
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
                    R.id.action_save -> saveSettings()
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun configureSettings() {
        var delay = viewModel.getDelayFromPreferences(requireContext())
        if(delay < 0) delay = Utils.NOTIFICATION_TIME_DEFAULT
        binding.settingsDelayInput.editText?.setText(delay.toString())
    }

    private fun saveSettings(): Boolean {
        val delay = binding.settingsDelayInput.editText?.text.toString()
        if(delay.isEmpty() || delay.toIntOrNull() == null || delay.toInt() < 0){
            Snackbar.make(
                binding.root,
                getString(R.string.error_settings_delay),
                Snackbar.LENGTH_SHORT
            ).show()
            return false
        }
        viewModel.saveDelayToPreferences(requireContext(), delay.toInt())
        findNavController().popBackStack()
        return true
    }
}