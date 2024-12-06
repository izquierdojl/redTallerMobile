package com.jlizquierdo.redtaller.ui.talleres

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.jlizquierdo.redtaller.databinding.ListItemTalleresBinding
import com.jlizquierdo.redtaller.modelo.Taller
import kotlinx.coroutines.withContext

class TalleresAdapter(private val talleres: List<Taller>) : RecyclerView.Adapter<TalleresAdapter.TallerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TallerViewHolder {
        val binding = ListItemTalleresBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TallerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TallerViewHolder, position: Int) {
        val taller = talleres[position]
        holder.bind(taller)
    }

    override fun getItemCount(): Int = talleres.size

    inner class TallerViewHolder(private val binding: ListItemTalleresBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(taller: Taller) {
            binding.nom.text = taller.nombre
            binding.pob.text = taller.pob
            val telefonos = buildString {
                if (taller.tel.isNotEmpty()) append("Fijo: ${taller.tel}")
                if (taller.movil.isNotEmpty()) append(", Móvil : ${taller.movil}")
            }
            binding.tel.text = telefonos
            binding.root.setOnClickListener {
                showTallerDetails(it.context,taller)
            }
            binding.botonLocation.setOnClickListener{
                doNavigate(itemView,taller)
            }
            binding.botonCall.setOnClickListener{
                doCallDialog(it.context,taller.tel,taller.movil)
            }
        }
    }

    private fun showTallerDetails(context: Context, taller: Taller) {
        // Crear un diálogo con los detalles del taller
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Detalles del Taller")

        // Construir el mensaje con los datos del taller
        val message = """
                NIF: ${taller.nif}
                Nombre: ${taller.nombre}
                Domicilio: ${taller.domicilio}
                CP: ${taller.cp}
                Población: ${taller.pob}
                Provincia: ${taller.pro}
                Teléfono: ${taller.tel}
                Móvil: ${taller.movil}
                eMail: ${taller.email}
            """.trimIndent()

        builder.setMessage(message)
        builder.setPositiveButton("Cerrar") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()

    }

    fun doNavigate(view: View, taller: Taller?) {
        taller?.let {
            val address = it.domicilio
            val postalCode = it.cp
            val pob = it.pob
            val province = it.pro

            val query = buildString {
                append(address)
                if (postalCode.isNotEmpty()) append(", $postalCode")
                if (pob.isNotEmpty()) append(", $pob")
                if (province.isNotEmpty()) append(", $province")
            }
            val uri = Uri.parse("geo:0,0?q=$query")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            val context = view.context
            context.startActivity(intent)
        }
    }

    fun doCallDialog(context: Context, fijo: String, movil: String) {

        val builder = AlertDialog.Builder(context)
        builder.setTitle("¿A qué número deseas llamar?")

        if( ! fijo.isNullOrEmpty() ) {
            builder.setPositiveButton("Fijo") { _, _ ->
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$fijo"))
                context.startActivity(intent)
            }
        }

        if( ! fijo.isNullOrEmpty() ) {
            builder.setNegativeButton("Móvil") { _, _ ->
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$movil"))
                context.startActivity(intent)
            }
        }

        builder.setNeutralButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()

    }


}
