package eina.unizar.xiangqi_frontendmovil.home_fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.get
import eina.unizar.xiangqi_frontendmovil.HttpHandler
import eina.unizar.xiangqi_frontendmovil.OtherProfile
import eina.unizar.xiangqi_frontendmovil.R
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class Ranking : Fragment(R.layout.fragment_ranking) {
    private val callback = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.home_content, Ranking())
            .commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainScope().launch {
            // Retrieve ranking data
            val response = HttpHandler.makeRankingRequest()
            if (!isAdded) return@launch

            // Hide loading bar and fill text fields
            view.findViewById<ProgressBar>(R.id.progressBar).visibility = ProgressBar.GONE
            view.findViewById<TextView>(R.id.textViewLoading).visibility = TextView.GONE

            if (response.error) return@launch
            view.findViewById<LinearLayout>(R.id.linearLayoutHeader).visibility = LinearLayout.VISIBLE
            view.findViewById<View>(R.id.divider).visibility = View.VISIBLE

            // Fill the table
            val table = view.findViewById<LinearLayout>(R.id.linearLayoutTable)
            val rowOffset = table.childCount
            for (i in 0 until response.ids.size) {
                val layout = layoutInflater.inflate(R.layout.ranking_row, table)
                val row = table[i+rowOffset]
                row.findViewById<TextView>(R.id.textViewPosition).text = "# ${response.positions[i]+1}"
                row.findViewById<TextView>(R.id.textViewPlayed).text = response.played[i].toString()
                row.findViewById<TextView>(R.id.textViewWon).text = response.won[i].toString()

                val flagOffset = 0x1F1E6
                val asciiOffset = 0x41
                val firstChar = Character.codePointAt(response.codes[i], 0) - asciiOffset + flagOffset
                val secondChar = Character.codePointAt(response.codes[i], 1) - asciiOffset + flagOffset
                row.findViewById<TextView>(R.id.textViewCountry).text = String(Character.toChars(firstChar)) +
                        String(Character.toChars(secondChar)) + " " + response.countries[i]

                val nickname = row.findViewById<TextView>(R.id.textViewPlayer)
                nickname.text = response.nicknames[i]
                nickname.setOnClickListener {
                    val intent = Intent(requireContext(), OtherProfile::class.java)
                    intent.putExtra("id", response.ids[i])
                    intent.putExtra("nickname", response.nicknames[i])
                    callback.launch(intent)
                }
            }

            // Load opponent images afterwards to reduce loading time
            for (i in 0 until response.ids.size) {
                if (response.hasImages[i]) {
                    val image = HttpHandler.makeImageRequest(HttpHandler.ImageRequest(response.ids[i])).image
                    table[i+rowOffset].findViewById<ImageView>(R.id.imageViewPlayer).setImageDrawable(image)
                }
            }
        }
    }
}