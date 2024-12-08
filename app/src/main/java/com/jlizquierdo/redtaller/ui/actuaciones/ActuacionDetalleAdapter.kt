import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jlizquierdo.redtaller.databinding.ListItemDetalleActuacionBinding
import com.jlizquierdo.redtaller.modelo.Actuacion_Detalle

class ActuacionDetalleAdapter(
    private var detalles: List<Actuacion_Detalle>,
    private val cargarImagen: (Int) -> Unit // Agrega el parámetro cargarImagen
) : RecyclerView.Adapter<ActuacionDetalleAdapter.DetalleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetalleViewHolder {
        val binding = ListItemDetalleActuacionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetalleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetalleViewHolder, position: Int) {
        val detalle = detalles[position]
        holder.bind(detalle, cargarImagen) // Pasa el callback al ViewHolder
    }

    override fun getItemCount(): Int = detalles.size

    fun actualizarDetalles(nuevosDetalles: List<Actuacion_Detalle>) {
        detalles = nuevosDetalles
        notifyDataSetChanged() // Notifica al RecyclerView para que refresque los datos
    }

    inner class DetalleViewHolder(private val binding: ListItemDetalleActuacionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(detalle: Actuacion_Detalle, cargarImagen: (Int) -> Unit) {
            binding.descripcion.text = detalle.descripcion
            if (detalle.existeImagen) {
                binding.verImagen.visibility = View.VISIBLE
                binding.verImagen.setOnClickListener {
                    cargarImagen(detalle.id)
                }
            } else {
                binding.verImagen.visibility = View.GONE
            }
        }
    }
}
