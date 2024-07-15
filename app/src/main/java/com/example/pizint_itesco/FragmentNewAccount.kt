package com.example.pizint_itesco

import android.R
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.pizint_itesco.databinding.FragmentSecondNewAccountBinding
import org.json.JSONArray
import androidx.navigation.fragment.findNavController
//import com.android.volley.BuildConfig
import com.example.pizint_itesco.BuildConfig
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject

class FragmentNewAccount : Fragment() {

    private var _binding: FragmentSecondNewAccountBinding? = null
    private var career: String = ""

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSecondNewAccountBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val textReturnLogin = binding.textReturnRegister
        val buttonCreateAccount = binding.buttonRegister
        val careerSpinner = binding.listCareer

        getCareerUniversityOfTheServer()

        textReturnLogin.setOnClickListener{
            returnSectionLogin()
        }

        careerSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()

                career = selectedItem
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        buttonCreateAccount.setOnClickListener {
            sendDataNewAccount()
        }
    }

    private fun sendDataNewAccount() {
        val emptyText = "^\\s*$".toRegex()
        val nameInput = binding.inputName
        val surnameInput = binding.inputSurname
        val secondSurnameInput = binding.inputSecondSurname
        val emailInput = binding.inputEmail
        val passwordInput = binding.inputPassword

        val name = nameInput.text.toString().trim()
        val surname = surnameInput.text.toString().trim()
        val secondSurname = secondSurnameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        if( emptyText.matches( name ) || emptyText.matches( surname ) || emptyText.matches( secondSurname ) || emptyText.matches( email ) || emptyText.matches( password ) ) {
            Snackbar.make(binding.root, "Todos los campos son obligatorios", Snackbar.LENGTH_LONG).show()

            return
        }

        if( career === "Seleccione la carrera a la que pertenece" ) {
            Snackbar.make(binding.root, "Seleccione la carrera a la que pertenece", Snackbar.LENGTH_LONG).show()

            return
        }

        val dataUser = JSONObject()

        try {
            dataUser.put("name", name)
            dataUser.put("surname", surname)
            dataUser.put("secondSurname", secondSurname)
            dataUser.put("email", email)
            dataUser.put("password", password)
            dataUser.put("career", career)
            dataUser.put("admin", false)
            dataUser.put("typeUser", "Alumno")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val queue = Volley.newRequestQueue(context)
        val url = BuildConfig.API_URL + "/register"

        val responseServer = FragmentLogin.CustomJsonObjectRequest(Request.Method.POST, url, dataUser,
                { response ->
                    val message = response.optString("message", "No se encontró ningún mensaje")
                    val register = response.optBoolean("register", false)

                    Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).apply {
                        duration = 2000
                        show()
                    }

                    if (register) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            findNavController().navigate(com.example.pizint_itesco.R.id.action_SecondFragment_to_FirstFragment)
                        }, 2000)
                    }
                },
                { error ->
                    val responseBody = String(error.networkResponse.data, Charsets.UTF_8)

                    try {
                        val jsonError = JSONObject(responseBody)
                        val message = jsonError.optString("message", "No se encontró nigun mensaje")

                        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                        Log.d("RegisterUser", "Error: $message")
                    } catch (e: Exception) {
                        Log.d("RegisterUser", "Error: ${error.message}")
                    }
                }
            )

        queue.add(responseServer)
    }

    private fun getCareerUniversityOfTheServer() {
        val spinner: Spinner = binding.listCareer
        val queue = Volley.newRequestQueue(context)
        val url = BuildConfig.API_URL + "/careerUniversity"

        val requestServer = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val careerNames = ArrayList<String>()
                careerNames.add("Seleccione la carrera a la que pertenece")

                for (i in 0 until response.length()) {
                    val career = response.getJSONObject(i)
                    val nameCareer = career.getString("name")
                    careerNames.add(nameCareer)
                }

                val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, careerNames)
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            },
            { error ->
                error.printStackTrace()
            }
        )

        queue.add(requestServer)
    }

    private fun returnSectionLogin() {
        findNavController().navigate(com.example.pizint_itesco.R.id.action_SecondFragment_to_FirstFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}