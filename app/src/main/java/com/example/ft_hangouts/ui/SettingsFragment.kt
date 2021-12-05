package com.example.ft_hangouts.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.ft_hangouts.R
import com.example.ft_hangouts.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private lateinit var binding: FragmentSettingsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSettingsBinding.bind(view)

        binding.changeColorBtn.setOnClickListener {
            (activity as MainActivity).changeToolbarColor()
            (activity as MainActivity).onBackPressed()
        }
    }
}