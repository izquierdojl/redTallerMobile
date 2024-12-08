package com.jlizquierdo.redtaller.ui.actuaciones

import ActuacionDetalleAdapter
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jlizquierdo.redtaller.R
import com.jlizquierdo.redtaller.databinding.ListItemActuacionBinding
import com.jlizquierdo.redtaller.datos.HttpUtils
import com.jlizquierdo.redtaller.datos.Preferencias
import com.jlizquierdo.redtaller.modelo.Actuacion
import com.jlizquierdo.redtaller.modelo.Actuacion_Detalle
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class ActuacionesAdapter(
    private var originalData: List<Actuacion> ) : RecyclerView.Adapter<ActuacionesAdapter.ActuacionViewHolder>()
{

    private var filteredData: List<Actuacion> = originalData

    fun getOriginalData(): List<Actuacion> = originalData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActuacionViewHolder {
        val binding = ListItemActuacionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActuacionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActuacionViewHolder, position: Int) {
        val actuacion = filteredData[position]
        holder.bind(actuacion)
    }

    override fun getItemCount(): Int = filteredData.size

    // Método para actualizar los datos filtrados
    fun updateData(newData: List<Actuacion>) {
        filteredData = newData
        notifyDataSetChanged() // Notifica al RecyclerView que los datos han cambiado
    }

    inner class ActuacionViewHolder(private val binding: ListItemActuacionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(actuacion: Actuacion) {
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val titulo = StringBuilder()
            titulo.append(actuacion.matricula!!.matricula)
            titulo.append(" / ")
            titulo.append("${actuacion.matricula!!.marca} ${actuacion.matricula!!.modelo}")

            binding.root.setOnClickListener {
                val preferencias = Preferencias()
                val sharedPreferences = preferencias.getEncryptedPreferences(it.context)
                val usuario = sharedPreferences.getString("username", null)
                val password = sharedPreferences.getString("password", null)
                mostrarDetallesDialog(it.context,actuacion,usuario.toString(),password.toString())
            }

            binding.vehiculo.text = titulo
            binding.taller.text = actuacion.taller!!.nombre
            binding.fecha.text = actuacion.fecha?.let { dateFormat.format(it) }

            when (actuacion.tipo) {
                "R" -> binding.foto.setImageResource(R.drawable.reparacion)
                "G" -> binding.foto.setImageResource(R.drawable.garantia)
                "M" -> binding.foto.setImageResource(R.drawable.mantenimiento)
                "S" -> binding.foto.setImageResource(R.drawable.accidente)
            }

        }
    }

    fun mostrarDetallesDialog(context: Context, actuacion: Actuacion, user: String, password: String) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_detalles_actuacion, null)
        val recyclerView: RecyclerView = dialogView.findViewById(R.id.recyclerViewDetalles)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val actuacionDetalles = mutableListOf<Actuacion_Detalle>()
        val adapter = ActuacionDetalleAdapter(actuacionDetalles) { detalleId ->
            obtenerImagenYMostrarDialogo(context, detalleId, user, password)
        }

        recyclerView.adapter = adapter

        HttpUtils.getDetallesActuacion(context, actuacion.id, { listaDetalles ->
            if (listaDetalles != null) {
                actuacionDetalles.clear()
                actuacionDetalles.addAll(listaDetalles)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(context, "Error al cargar los detalles", Toast.LENGTH_SHORT).show()
            }
        }, user, password)

        val dialog = AlertDialog.Builder(context)
            .setTitle("Detalles de la actuación")
            .setView(dialogView)
            .setPositiveButton("Cerrar", null)
            .create()

        dialog.show()
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun obtenerImagenYMostrarDialogo(context: Context, detalleId: Int, user: String, password: String) {
        HttpUtils.getImagenActuacionDetalle(context, detalleId, { base64Image ->
            if (!base64Image.isNullOrEmpty()) {

                val imageBytes = Base64.decode(base64Image)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                // Crear un ImageView para mostrar la imagen e incorporarlo al diálogo
                val imageView = ImageView(context).apply {
                    setImageBitmap(bitmap)
                    setPadding(20, 20, 20, 20)
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
                }

                val dialog = AlertDialog.Builder(context)
                    .setTitle("Imagen de la actuación")
                    .setView(imageView)
                    .setPositiveButton("Cerrar", null)
                    .create()
                dialog.show()
            } else {
                Toast.makeText(context, "No se pudo cargar la imagen", Toast.LENGTH_SHORT).show()
            }
        }, user, password)
    }

}
