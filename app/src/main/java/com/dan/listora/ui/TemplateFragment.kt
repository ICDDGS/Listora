package com.dan.listora.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dan.listora.R
import com.dan.listora.databinding.FragmentTemplateBinding


class TemplateFragment : Fragment(R.layout.fragment_template) {

    private lateinit var binding: FragmentTemplateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = FragmentTemplateBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding.tvTitle.text = "Template"
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_template, container, false)
    }


}