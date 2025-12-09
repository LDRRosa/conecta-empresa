package com.ifgoiano.conectaempresa.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ifgoiano.conectaempresa.adapter.EmpresaDetalhadaAdapter
import com.ifgoiano.conectaempresa.data.model.Empresa
import com.ifgoiano.conectaempresa.databinding.BottomSheetClusterBinding
import com.ifgoiano.conectaempresa.view.DetalhesEmpresaActivity

class ClusterBottomSheetFragment : BottomSheetDialogFragment() {

    var empresas: List<Empresa> = emptyList()
    var rua: String = "Rua"

    private var _binding: BottomSheetClusterBinding? = null
    private val binding get() = _binding!!

    override fun getTheme(): Int =
        R.style.Theme_Material3_Light_BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetClusterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (empresas.size == 1) {
            binding.tvRua.visibility = View.GONE
            binding.tvSub.visibility = View.GONE
        } else {
            binding.tvRua.text = rua
            binding.tvSub.text = "${empresas.size} empresas nesta rua"
            binding.tvRua.visibility = View.VISIBLE
            binding.tvSub.visibility = View.VISIBLE
        }

        binding.rvLista.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = EmpresaDetalhadaAdapter(empresas) { emp ->
                val intent = Intent(requireContext(), DetalhesEmpresaActivity::class.java).apply {
                    putExtra("empresa_nome", emp.nome)
                    putExtra("empresa_imagem", emp.imageUrl)
                    putExtra("empresa_descricao", emp.descricao)
                    putExtra("empresa_categoria", emp.categoria)
                    putExtra("empresa_telefone", emp.telefone)
                    putExtra("empresa_endereco", emp.endereco)
                    putExtra("empresa_avaliacao", emp.avaliacao.toFloat())
                    putExtra("empresa_email", emp.email)
                    putExtra("empresa_latitude", emp.latitude ?: Double.NaN)
                    putExtra("empresa_longitude", emp.longitude ?: Double.NaN)
                }
                startActivity(intent)
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}