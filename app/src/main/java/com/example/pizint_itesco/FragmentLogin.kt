package com.example.pizint_itesco

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.pizint_itesco.databinding.FragmentFirstLoginBinding

class FragmentLogin : Fragment() {

    private var _binding: FragmentFirstLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applySpannableText()
    }

    private fun applySpannableText() {
        val fullText = "¿No tienes una cuenta? Regístrate"
        val spannableString = SpannableString(fullText)

        // Aplica color a la parte "Regístrate"
        val startIndex = fullText.indexOf("Regístrate")
        val endIndex = startIndex + "Regístrate".length
        // Usa ContextCompat para obtener el color entero
        val colorInt = ContextCompat.getColor(requireContext(), R.color.colorTextRegister) // Asegúrate de tener un color definido en res/values/colors.xml
        val colorSpan = ForegroundColorSpan(colorInt)
        spannableString.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Aplica el SpannableString al TextView
        binding.textViewRegister.text = spannableString
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}