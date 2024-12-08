package com.jlizquierdo.redtaller.ui.actuaciones

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jlizquierdo.redtaller.R
import com.jlizquierdo.redtaller.databinding.FragmentActuacionBinding
import com.jlizquierdo.redtaller.datos.HttpUtils
import com.jlizquierdo.redtaller.datos.Preferencias
import com.jlizquierdo.redtaller.modelo.Actuacion
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

        binding.botonFilter.setOnClickListener( {
            mostrarDialogoFiltros()
        } )

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
                    // Actualiza el adaptador con los talleres obtenidos
                    adapter = ActuacionesAdapter(actuaciones)
                    binding.recyclerView.adapter = adapter
                } else {
                    Log.e("Talleres", "No se encontraron talleres.")
                }
            }, usuario, password, filter)
        } else {
            Log.e("Actuaciones", "Usuario o contraseña no encontrados.")
        }
    }

    private fun mostrarDialogoFiltros() {

        val tiposList = listOf("Reparación", "Garantía", "Mantenimiento", "Accidente")
        val filtroTipos = BooleanArray(tiposList.size) { true }

        val matrículas = adapter.getOriginalData().map { it.matricula!!.matricula }.distinct()
        val matrículasAll = listOf("Todas") + matrículas

        val spinnerSelection = 0 // Indica el índice de la matrícula seleccionada. 0 es "Todas" por defecto.
        val view = layoutInflater.inflate(R.layout.dialog_filtro_actuacion, null)
        val spinner: Spinner = view.findViewById(R.id.spinner_matricula)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, matrículasAll)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(spinnerSelection)

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Filtrar por Tipo y Matrícula")

        // Establecer el layout para el diálogo
        builder.setView(view)

        builder.setMultiChoiceItems(
            tiposList.toTypedArray(),
            filtroTipos
        ) { _, which, isChecked ->
            filtroTipos[which] = isChecked
        }

        builder.setPositiveButton("Aplicar") { _, _ ->
            val tiposSeleccionados = tiposList
                .filterIndexed { index, _ -> filtroTipos[index] }
                .map { it.first().toString() }
            val matriculaSeleccionada = matrículasAll[spinner.selectedItemPosition]
            aplicarFiltros(tiposSeleccionados, matriculaSeleccionada)
        }

        builder.setNegativeButton("Cancelar", null)

        builder.create().show()
    }

    private fun aplicarFiltros(tiposSeleccionados: List<String>, matriculaSeleccionada: String) {
        val listaFiltrada = adapter.getOriginalData().filter { actuacion ->
            val tipoCoincide = actuacion.tipo in tiposSeleccionados
            val matriculaCoincide = matriculaSeleccionada == "Todas" || actuacion.matricula?.matricula == matriculaSeleccionada
            tipoCoincide && matriculaCoincide
        }
        adapter.updateData(listaFiltrada)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}