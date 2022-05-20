package eina.unizar.xiangqi_frontendmovil.home_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.*
import androidx.core.view.get
import com.google.android.material.imageview.ShapeableImageView
import eina.unizar.xiangqi_frontendmovil.HttpHandler
import eina.unizar.xiangqi_frontendmovil.R
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class Store : Fragment(R.layout.fragment_store) {
    private var points = 0
    private val boardSkins = listOf(
        R.drawable.skin_board_1,
        R.drawable.skin_board_2,
        R.drawable.skin_board_3,
        R.drawable.skin_board_4,
        R.drawable.skin_board_5,
        R.drawable.skin_board_6,
        R.drawable.skin_board_7,
        R.drawable.skin_board_8,
        R.drawable.skin_board_9,
        R.drawable.skin_board_10,
        R.drawable.skin_board_11,
        R.drawable.skin_board_12
    )
    private val piecesSkins = listOf(
        R.drawable.skin_pieces_canyonnegro_1,
        R.drawable.skin_pieces_canyonnegro_2,
        R.drawable.skin_pieces_canyonnegro_3,
        R.drawable.skin_pieces_canyonnegro_4,
        R.drawable.skin_pieces_canyonnegro_5,
        R.drawable.skin_pieces_canyonnegro_6
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainScope().launch {
            // Retrieve store data
            points = HttpHandler.makePointsRequest().points
            val response = HttpHandler.makeStoreRequest()
            if (!isAdded) return@launch

            // Hide loading bar and fill text fields
            view.findViewById<ProgressBar>(R.id.progressBar).visibility = ProgressBar.GONE
            view.findViewById<TextView>(R.id.textViewLoading).visibility = TextView.GONE

            if (response.error) return@launch
            view.findViewById<ShapeableImageView>(R.id.imageViewPoints).visibility = ShapeableImageView.VISIBLE
            view.findViewById<TextView>(R.id.textViewPoints).text = "Número de puntos: $points"
            view.findViewById<ScrollView>(R.id.scrollView).visibility = ScrollView.VISIBLE

            val boards = view.findViewById<LinearLayout>(R.id.linearLayoutBoards)
            val pieces = view.findViewById<LinearLayout>(R.id.linearLayoutPieces)
            for (i in 0 until response.ids.size) {
                val index = response.ids[i]-1
                val item: View
                if (response.types[i]== 1) {
                    val layout = layoutInflater.inflate(R.layout.store_item, boards)
                    item = boards[index]
                    item.findViewById<ShapeableImageView>(R.id.imageViewSkin).setImageResource(boardSkins[index])
                }
                else {
                    val layout = layoutInflater.inflate(R.layout.store_item, pieces)
                    item = pieces[index]
                    item.findViewById<ShapeableImageView>(R.id.imageViewSkin).setImageResource(piecesSkins[index])
                }
                item.findViewById<TextView>(R.id.textViewCat).text = response.cats[i]
                item.findViewById<TextView>(R.id.textViewName).text = response.names[i]
                item.findViewById<TextView>(R.id.textViewDesc).text = response.descs[i]
                item.findViewById<TextView>(R.id.textViewPrice).text = response.prices[i].toString()

                val button = item.findViewById<Button>(R.id.buttonBuy)
                if (response.purchased[i]) button.isEnabled = false
                else {
                    button.setOnClickListener {
                        if (points < response.prices[i]) {
                            Toast.makeText(requireContext(), "Puntos insuficientes", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            button.isEnabled = false
                            val loading = item.findViewById<ProgressBar>(R.id.progressBarLoading)
                            loading.visibility = ProgressBar.VISIBLE
                            MainScope().launch {
                                val id = response.ids[i]
                                val type = response.types[i]
                                val price = response.prices[i]
                                val response = HttpHandler.makePurchaseRequest(HttpHandler.PurchaseRequest(
                                    id,
                                    type,
                                    price
                                ))
                                if (!isAdded) return@launch
                                loading.visibility = ProgressBar.GONE
                                if (response.error) {
                                    button.isEnabled = true
                                    Toast.makeText(requireContext(), "Error de conexión con backend", Toast.LENGTH_SHORT).show()
                                    return@launch
                                }
                                points -= price
                                view.findViewById<TextView>(R.id.textViewPoints).text = "Número de puntos: $points"
                                Toast.makeText(requireContext(), "Compra realizada correctamente", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }
}