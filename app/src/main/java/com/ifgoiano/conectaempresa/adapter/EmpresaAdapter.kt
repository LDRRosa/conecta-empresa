import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ifgoiano.conectaempresa.data.model.Empresa
import com.ifgoiano.conectaempresa.databinding.ItemEmpresaBinding

class EmpresaAdapter(private val lista: List<Empresa>) :
    RecyclerView.Adapter<EmpresaAdapter.EmpresaViewHolder>() {

    inner class EmpresaViewHolder(val binding: ItemEmpresaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmpresaViewHolder {
        val binding = ItemEmpresaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EmpresaViewHolder(binding)
    }



    override fun onBindViewHolder(holder: EmpresaViewHolder, position: Int) {
        val empresa = lista[position]
        holder.binding.tvNome.text = empresa.nome
        holder.binding.tvDistancia.text = empresa.distancia
        holder.binding.imgEmpresa.setImageResource(empresa.imagem)
    }

    override fun getItemCount() = lista.size
}
