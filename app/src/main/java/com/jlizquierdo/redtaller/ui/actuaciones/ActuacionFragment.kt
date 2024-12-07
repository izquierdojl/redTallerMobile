package com.jlizquierdo.redtaller.ui.actuaciones

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.jlizquierdo.redtaller.databinding.FragmentActuacionBinding
import com.jlizquierdo.redtaller.datos.HttpUtils
import com.jlizquierdo.redtaller.datos.Preferencias
import com.jlizquierdo.redtaller.ui.actuaciones.ActuacionesAdapter
import com.jlizquierdo.redtaller.ui.talleres.TalleresAdapter

class ActuacionFragment : Fragment() {

    private var _binding: FragmentActuacionBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var adapter: ActuacionesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val actuacionViewModel =
            ViewModelProvider(this).get(ActuacionViewModel::class.java)

        _binding = FragmentActuacionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = ActuacionesAdapter(emptyList())
        binding.recyclerView.adapter = adapter

        loadActuaciones("")

        return root
    }

    private fun loadActuaciones(filter: String) {
        val preferencias = Preferencias()
        val sharedPreferences = preferencias.getEncryptedPreferences(requireContext())
        val usuario = sharedPreferences.getString("username", null)
        val password = sharedPreferences.getString("password", null)

        if (usuario != null && password != null) {
            HttpUtils.getActuaciones(requireContext(), { actuaciones ->
                if (actuaciones != null && actuaciones.isNotEmpty()) {
                    adapter = ActuacionesAdapter(actuaciones)
                    binding.recyclerView.adapter = adapter
                } else {
                    Log.e("Actuaciones", "No se encontraron actuaciones.")
                }
            }, usuario, password, filter)
        } else {
            Log.e("Actuaciones", "Usuario o contraseña no encontrados.")
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}