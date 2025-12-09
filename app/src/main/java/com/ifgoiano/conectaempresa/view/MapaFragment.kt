// kotlin
package com.ifgoiano.conectaempresa.view

import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import com.ifgoiano.conectaempresa.R
import com.ifgoiano.conectaempresa.data.model.Empresa
import com.ifgoiano.conectaempresa.databinding.FragmentMapaBinding
import com.ifgoiano.conectaempresa.view.fragment.ClusterBottomSheetFragment
import com.ifgoiano.conectaempresa.viewmodel.MapaViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

class MapaFragment : Fragment() {

    private var _binding: FragmentMapaBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MapaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapViewAll.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(12.0)
        }

        viewModel.empresas.observe(viewLifecycleOwner) { lista ->
            binding.mapViewAll.post {
                adicionarMarcadoresPorRua(lista)
                val first = lista.firstOrNull { it.latitude != null && it.longitude != null }
                first?.let {
                    binding.mapViewAll.controller.setCenter(GeoPoint(it.latitude!!, it.longitude!!))
                }
            }
        }
    }

    // Agrupa estritamente por street (normalizado). Se não houver street, cria marcadores individuais.
    private fun adicionarMarcadoresPorRua(lista: List<Empresa>) {
        val map = binding.mapViewAll
        map.overlays.clear()

        val grupos = lista.filter { it.latitude != null && it.longitude != null }
            .groupBy { emp ->
                emp.street.trim().lowercase()
                    .ifEmpty { "____NO_STREET_____${emp.city.trim().lowercase()}" }
            }

        grupos.forEach { (chave, items) ->
            if (items.isEmpty()) return@forEach

            if (chave.startsWith("____NO_STREET____")) {
                items.forEach { emp ->
                    val gp = GeoPoint(emp.latitude!!, emp.longitude!!)
                    val marker = Marker(map).apply {
                        position = gp
                        title = emp.nome
                        subDescription = emp.endereco ?: ""
                        icon = createCompanyIcon(emp)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        setOnMarkerClickListener { _, _ ->
                            // única empresa: abrir detalhes direto
                            abrirDetalhes(emp)
                            true
                        }
                    }
                    map.overlays.add(marker)
                }
            } else {
                if (items.size == 1) {
                    val emp = items.first()
                    val gp = GeoPoint(emp.latitude!!, emp.longitude!!)
                    val marker = Marker(map).apply {
                        position = gp
                        title = emp.nome
                        subDescription = emp.endereco ?: ""
                        icon = createCompanyIcon(emp)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        setOnMarkerClickListener { _, _ ->
                            // mostrar card da empresa em BottomSheet (sem abrir diretamente a activity)
                            val sheet = ClusterBottomSheetFragment()
                            sheet.empresas = listOf(emp)
                            sheet.rua = emp.street ?: "Rua"
                            sheet.show(parentFragmentManager, "cluster_sheet")
                            true
                        }
                    }
                    map.overlays.add(marker)
                } else {
                    val lat = items.map { it.latitude!! }.average()
                    val lon = items.map { it.longitude!! }.average()
                    val centroid = GeoPoint(lat, lon)
                    val marker = Marker(map).apply {
                        position = centroid
                        title = "${items.size} na mesma rua"
                        icon = createClusterIcon(items.size)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        setOnMarkerClickListener { _, _ ->
                            val sheet = ClusterBottomSheetFragment()
                            sheet.empresas = items
                            sheet.rua = items.firstOrNull()?.street ?: "Rua"
                            sheet.show(parentFragmentManager, "cluster_sheet")
                            true
                        }
                    }
                    map.overlays.add(marker)
                }
            }
        }

        map.invalidate()
    }

    private fun abrirDetalhes(emp: Empresa) {
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
    }

    // converte Drawable para Bitmap redimensionado
    private fun drawableToBitmap(drawable: Drawable, size: Int): Bitmap {
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bmp
    }

    // mapeia categoria para nome de drawable Material esperado (tente importar esses nomes via Vector Asset)
    private fun drawableNameForCategoria(categoria: String): String {
        return when (categoria.lowercase().trim()) {
            "restaurantes" -> "ic_restaurant_24"
            "mercados" -> "ic_shopping_cart_24"
            "farmácias", "farmacias" -> "ic_local_pharmacy_24"
            "moda" -> "ic_local_mall_24"
            "serviços", "servicos" -> "ic_build_24"
            else -> "ic_business_24"
        }
    }

    // retorna id de drawable por nome com fallback para 0 quando não existe
    private fun drawableIdIfExists(name: String): Int {
        return resources.getIdentifier(name, "drawable", requireContext().packageName)
    }

    // retorna id de drawable por nome com fallback para icon_empresa
    private fun drawableIdByName(name: String): Int {
        val resId = drawableIdIfExists(name)
        return if (resId != 0) resId else R.drawable.icon_empresa
    }

    // fallback por emoji quando não houver vector importado
    private fun emojiForCategoria(categoria: String): String {
        return when (categoria.lowercase().trim()) {
            "restaurantes" -> "🍔"
            "mercados" -> "🛒"
            "farmácias", "farmacias" -> "💊"
            "moda" -> "👗"
            "serviços", "servicos" -> "🛠️"
            else -> "🏢"
        }
    }

    // escolhe cor de fundo por categoria
    private fun colorForCategoria(categoria: String): Int {
        return when (categoria.lowercase().trim()) {
            "restaurantes" -> Color.parseColor("#E74C3C")
            "mercados" -> Color.parseColor("#3498DB")
            "farmácias", "farmacias" -> Color.parseColor("#2ECC71")
            "moda" -> Color.parseColor("#9B59B6")
            "serviços", "servicos" -> Color.parseColor("#F39C12")
            else -> Color.parseColor("#F6C90E")
        }
    }

    // ícone para empresa única: tenta drawable Material; se não existir, desenha emoji único
    private fun createCompanyIcon(empresa: Empresa): BitmapDrawable {
        val density = resources.displayMetrics.density
        val size = (56 * density).toInt().coerceAtLeast(24)
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(Color.TRANSPARENT)

        val radius = size / 2f

        // fundo circular colorido
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = colorForCategoria(empresa.categoria)
        }
        canvas.drawCircle(radius, radius, radius, bgPaint)

        // borda branca fina
        val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = Color.WHITE
            strokeWidth = 2f * density
        }
        canvas.drawCircle(radius, radius, radius - 1f * density, stroke)

        // tenta drawable Material por nome
        val drawableName = drawableNameForCategoria(empresa.categoria)
        val resId = drawableIdIfExists(drawableName)

        if (resId != 0) {
            val drawable = ContextCompat.getDrawable(requireContext(), resId)
                ?: ContextCompat.getDrawable(requireContext(), R.drawable.icon_empresa)!!

            try {
                drawable.mutate()
                drawable.setTint(Color.WHITE)
            } catch (_: Exception) {
            }

            val iconSize = (size * 0.52f).toInt().coerceAtLeast(12)
            val iconBmp = drawableToBitmap(drawable, iconSize)
            val left = (size - iconSize) / 2f
            val top = (size - iconSize) / 2f
            canvas.drawBitmap(iconBmp, left, top, null)
        } else {
            val emoji = emojiForCategoria(empresa.categoria)
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textAlign = Paint.Align.CENTER
                textSize = size * 0.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val fm = textPaint.fontMetrics
            val textY = radius - (fm.ascent + fm.descent) / 2
            canvas.drawText(emoji, radius, textY, textPaint)
        }

        return BitmapDrawable(resources, bmp)
    }

    // ícone de cluster: círculo simples sem sombra pesada, drawable Material no centro e badge com contagem
    private fun createClusterIcon(count: Int): BitmapDrawable {
        val density = resources.displayMetrics.density
        val size = (64 * density).toInt().coerceAtLeast(1)
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(Color.TRANSPARENT)

        val cx = size / 2f
        val cy = size / 2f
        val radius = size * 0.45f

        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = Color.parseColor("#1F1F1F")
        }
        canvas.drawCircle(cx, cy, radius, bgPaint)

        val border = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = Color.argb(180, 255, 255, 255)
            strokeWidth = 1.2f * density
        }
        canvas.drawCircle(cx, cy, radius, border)

        val businessRes = drawableIdByName("ic_business_24")
        val drawable = ContextCompat.getDrawable(requireContext(), businessRes)!!
        try {
            drawable.mutate()
            drawable.setTint(Color.WHITE)
        } catch (_: Exception) {
        }

        val iconSize = (radius * 1.0f).toInt().coerceAtLeast(1)
        val iconBmp = drawableToBitmap(drawable, iconSize)
        val iconLeft = cx - iconSize / 2f
        val iconTop = cy - iconSize / 2f
        canvas.drawBitmap(iconBmp, iconLeft, iconTop, null)

        val badgeRadius = size * 0.22f
        val badgeCx = cx + radius * 0.55f - badgeRadius / 2f
        val badgeCy = cy + radius * 0.55f - badgeRadius / 2f
        val badgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = Color.parseColor("#F6C90E")
        }
        canvas.drawCircle(badgeCx, badgeCy, badgeRadius, badgePaint)

        val countPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textAlign = Paint.Align.CENTER
            textSize = badgeRadius * 0.9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val fmCount = countPaint.fontMetrics
        val countY = badgeCy - (fmCount.ascent + fmCount.descent) / 2
        val label = if (count > 99) "99+" else count.toString()
        canvas.drawText(label, badgeCx, countY, countPaint)

        return BitmapDrawable(resources, bmp)
    }

    override fun onResume() {
        super.onResume()
        binding.mapViewAll.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapViewAll.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
