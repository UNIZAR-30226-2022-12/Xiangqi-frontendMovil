package eina.unizar.xiangqi_frontendmovil.home_fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.get
import eina.unizar.xiangqi_frontendmovil.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class History : Fragment(R.layout.fragment_history) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainScope().launch {
            // Retrieve history data
            val response = HttpHandler.makeHistoryRequest()
            if (!isAdded) return@launch

            // Hide loading bar and fill text fields
            view.findViewById<ProgressBar>(R.id.progressBar).visibility = ProgressBar.GONE
            view.findViewById<TextView>(R.id.textViewLoading).visibility = TextView.GONE
            view.findViewById<LinearLayout>(R.id.linearLayoutHeader).visibility = LinearLayout.VISIBLE
            view.findViewById<View>(R.id.divider).visibility = View.VISIBLE

            if (response.error) return@launch
            if (response.ids.isEmpty())
                view.findViewById<TextView>(R.id.textViewGamesNotFound).visibility = TextView.VISIBLE

            val table = view.findViewById<LinearLayout>(R.id.linearLayoutTable)
            val rowOffset = table.childCount
            // If there are games, fill the table
            for (i in 0 until  response.ids.size) {
                val layout = layoutInflater.inflate(R.layout.history_row, table)
                val row = table[i+rowOffset]
                row.findViewById<TextView>(R.id.textViewDate).text =  response.dates[i]
                row.findViewById<TextView>(R.id.textViewHour).text =  response.hours[i]
                row.findViewById<TextView>(R.id.textViewWinner).text =  response.winners[i]
                row.findViewById<TextView>(R.id.textViewMoves).text =  response.ids[i].size.toString()

                row.findViewById<Button>(R.id.buttonReview).setOnClickListener {
                    val intent = Intent(requireContext(), GameReview::class.java)
                    intent.putIntegerArrayListExtra("ids", ArrayList(response.ids[i]))
                    intent.putStringArrayListExtra("nicknames", ArrayList(response.nicknames[i]))
                    intent.putStringArrayListExtra("codes", ArrayList(response.codes[i]))
                    intent.putStringArrayListExtra("colors", ArrayList(response.colors[i]))
                    intent.putStringArrayListExtra("origins", ArrayList(response.origins[i]))
                    intent.putStringArrayListExtra("destinations", ArrayList(response.destinations[i]))
                    startActivity(intent)
                }
            }
        }
    }
}