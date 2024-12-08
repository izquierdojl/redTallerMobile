package com.jlizquierdo.redtaller.ui.actuaciones

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jlizquierdo.redtaller.R
import com.jlizquierdo.redtaller.databinding.ListItemActuacionBinding
import com.jlizquierdo.redtaller.modelo.Actuacion

class ActuacionesAdapter(private val actuaciones: List<Actuacion>) : RecyclerView.Adapter<com.jlizquierdo.redtaller.ui.actuaciones.ActuacionesAdapter.ActuacionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActuacionesAdapter.ActuacionViewHolder {
        val binding = ListItemActuacionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActuacionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActuacionViewHolder, position: Int) {
        val actuacion = actuaciones[position]
        holder.bind(actuacion)
    }

    override fun getItemCount(): Int = actuaciones.size

    inner class ActuacionViewHolder(private val binding: ListItemActuacionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(actuacion: Actuacion) {
            binding.vehiculo.text = actuacion.matricula!!.modelo
            binding.taller.text = actuacion.taller!!.nombre
            binding.fecha.text = actuacion.fecha.toString()
            when (actuacion.tipo)
            {
                "R" -> binding.foto.setImageResource(R.drawable.reparacion)
                "G" -> binding.foto.setImageResource(R.drawable.garantia)
                "M" -> binding.foto.setImageResource(R.drawable.mantenimiento)
                "S" -> binding.foto.setImageResource(R.drawable.accidente)
            }
        }
    }

}