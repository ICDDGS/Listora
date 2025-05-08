package com.dan.listora.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dan.listora.databinding.FragmentRecipePagerBinding
import com.dan.listora.ui.adapter.RecipePagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class RecipePagerFragment : Fragment() {

    private var _binding: FragmentRecipePagerBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: RecipePagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipePagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = RecipePagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Todas"
                1 -> "Favoritas"
                else -> "Pestaña"
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}