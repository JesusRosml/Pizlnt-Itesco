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
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import android.content.Context
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import androidx.navigation.fragment.findNavController

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

import com.android.volley.NetworkResponse
import com.android.volley.toolbox.HttpHeaderParser


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
        val buttonAuth = binding.buttonLogin
        val textRegisterUser = binding.textViewRegister

        buttonAuth.setOnClickListener {
            authenticationUser()
            closeKeyboard()
        }

        textRegisterUser.setOnClickListener {
            openFragmentRegister()
        }

        applySpannableText()
    }

    private fun applySpannableText() {
        val fullText = "¿No tienes una cuenta? Regístrate"
        val spannableString = SpannableString(fullText)

        val startIndex = fullText.indexOf("Regístrate")
        val endIndex = startIndex + "Regístrate".length
        val colorInt = ContextCompat.getColor(
            requireContext(),
            R.color.colorTextRegister
        )
        val colorSpan = ForegroundColorSpan(colorInt)
        spannableString.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.textViewRegister.text = spannableString
    }

    class CustomJsonObjectRequest(
        method: Int,
        url: String,
        requestBody: JSONObject?,
        private val responseListener: Response.Listener<JSONObject>,
        errorListener: Response.ErrorListener
    ) : JsonObjectRequest(method, url, requestBody, responseListener, errorListener) {

        override fun parseNetworkResponse(response: NetworkResponse): Response<JSONObject> {
            return if (response.statusCode == 401) {
                // Aquí manejas cómo quieres procesar los códigos 401.
                // Por ejemplo, convertir la respuesta en un JSONObject.
                val jsonResponse = JSONObject(String(response.data))
                Response.success(jsonResponse, HttpHeaderParser.parseCacheHeaders(response))
            } else {
                // Para otros códigos, usa el comportamiento predeterminado.
                super.parseNetworkResponse(response)
            }
        }
    }

    private fun authenticationUser() {
        val emptyText = "^\\s*$".toRegex()
        val email = binding.emailUser.text.toString()
        val password = binding.passwordUser.text.toString()

        if (emptyText.matches(email) || emptyText.matches(password)) {
            Snackbar.make(binding.root, "Los campos de correo electrónico y contraseña no pueden estar vacíos", Snackbar.LENGTH_LONG).show()
            return
        }

        val credentialUser = JSONObject()
        try {
            credentialUser.put("email", email)
            credentialUser.put("password", password)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val queue = Volley.newRequestQueue(context)
        val url = BuildConfig.API_URL + "/login"

        val responseServer = CustomJsonObjectRequest(Request.Method.POST, url, credentialUser,
            { response ->
                val message = response.optString("message", "No se encontró nigun mensaje")
                val auth = response.optBoolean("authentication", false )
                val user = response.optJSONObject("user")

                val id = user?.optString("_id", "No ID")
                val name = user?.optString("name", "No Name")
                val surname = user?.optString("surname", "No Surname")
                val secondSurname = user?.optString("secondSurname", "No SecondSurname")
                val email = user?.optString("email", "No Email")
                val career = user?.optString("career", "No Career")
                val isAdmin = user?.optBoolean("admin", false)
                val typeUser = user?.optString("typeUser", "No TypeUser")

                val sharedPreferences = activity?.getSharedPreferences("userData", Context.MODE_PRIVATE)
                val editor = sharedPreferences?.edit()

                editor?.putString("id", id)
                editor?.putString("name", name)
                editor?.putString("surname", surname)
                editor?.putString("secondSurname", secondSurname)
                editor?.putString("email", email)
                editor?.putString("career", career)
                if (isAdmin != null) { editor?.putBoolean("isAdmin", isAdmin) }
                editor?.putString("typeUser", typeUser)

                editor?.apply()

                if( auth ) {
                    val intent = Intent(activity, Home::class.java)
                    activity?.startActivity(intent)
                    activity?.finish()
                }
            },
            { error ->
                val responseBody = String(error.networkResponse.data, Charsets.UTF_8)

                try {
                    val jsonError = JSONObject(responseBody)
                    val message = jsonError.optString("message", "No se encontró nigun mensaje")

                    Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                    Log.d("LoginFragment", "Error: $message")
                } catch (e: Exception) {
                    Log.d("LoginFragment", "Error: ${error.message}")
                }
            }
        )

        queue.add(responseServer)
    }

    private fun closeKeyboard() {
        val view = view?.findFocus()
        if (view != null) {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun openFragmentRegister() {
        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}