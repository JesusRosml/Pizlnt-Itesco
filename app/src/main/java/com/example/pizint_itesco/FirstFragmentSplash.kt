package com.example.pizint_itesco

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pizint_itesco.databinding.FragmentFirstSplashBinding

class FirstFragmentSplash : Fragment() {

    private var _binding: FragmentFirstSplashBinding ? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

      _binding = FragmentFirstSplashBinding.inflate(inflater, container, false)
      return binding.root
    }
}