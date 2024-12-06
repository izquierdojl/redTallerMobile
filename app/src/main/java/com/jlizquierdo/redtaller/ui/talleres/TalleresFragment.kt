package com.jlizquierdo.redtaller.ui.talleres

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jlizquierdo.redtaller.databinding.FragmentTalleresBinding
import com.jlizquierdo.redtaller.datos.HttpUtils
import com.jlizquierdo.redtaller.datos.Preferencias
import java.util.Locale

class TalleresFragment : Fragment() {

    private var _binding: FragmentTalleresBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TalleresAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(TalleresViewModel::class.java)

        _binding = FragmentTalleresBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = TalleresAdapter(emptyList())
        binding.recyclerView.adapter = adapter

        // filtro de búsqueda
        val editTextSearch = binding.editTextSearch
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchTerm = s.toString().lowercase(Locale.getDefault())
                loadTalleres(searchTerm)
                binding.recyclerView.adapter?.notifyDataSetChanged()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        loadTalleres("")  // Carga los talleres sin filtro inicialmente

        return root
    }

    private fun loadTalleres(filter: String) {
        val preferencias = Preferencias()
        val sharedPreferences = preferencias.getEncryptedPreferences(requireContext())
        val usuario = sharedPreferences.getString("username", null)
        val password = sharedPreferences.getString("password", null)

        if (usuario != null && password != null) {
            HttpUtils.getTalleres(requireContext(), { talleres ->
                if (talleres != null && talleres.isNotEmpty()) {
                    // Actualiza el adaptador con los talleres obtenidos
                    adapter = TalleresAdapter(talleres)
                    binding.recyclerView.adapter = adapter
                } else {
                    Log.e("Talleres", "No se encontraron talleres.")
                }
            }, usuario, password, filter)
        } else {
            Log.e("Talleres", "Usuario o contraseña no encontrados.")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}