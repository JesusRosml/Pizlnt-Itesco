package com.example.pizint_itesco

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pizint_itesco.databinding.FragmentFirstLoginBinding

class FirstFragmentLogin : Fragment() {

    private var _binding: FragmentFirstLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstLoginBinding.inflate(inflater, container, false)
        return binding.root

    }
}