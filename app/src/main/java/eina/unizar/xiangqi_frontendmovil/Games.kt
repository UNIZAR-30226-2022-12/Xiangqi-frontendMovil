package eina.unizar.xiangqi_frontendmovil

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class Games : Fragment(R.layout.fragment_games) {
    private lateinit var dialog: Dialog
    private val callback = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        dialog.hide()
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.home_content, Games())
            .commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Construct new game dialog
        dialog = object : Dialog(requireContext()) {
            override fun onDetachedFromWindow() {
                super.onDetachedFromWindow()
                val button = findViewById<Button>(R.id.buttonGameStart)
                button.isEnabled = true
                button.text = getText(R.string.games_start)
            }
        }
        dialog.setContentView(R.layout.dialog_new_game)
        dialog.findViewById<Button>(R.id.buttonGameStart).setOnClickListener {
            // Begin opponent search
            val button = dialog.findViewById<Button>(R.id.buttonGameStart)
            button.isEnabled = false
            button.text = getText(R.string.games_searching)
            //SocketHandler.searchRandomOpponent()
            // Launch intent (debug only)
            val i = Intent(activity, Board::class.java)
            callback.launch(i)
        }

        view.findViewById<Button>(R.id.buttonNewGame).setOnClickListener { dialog.show() }

        // Set listener for opponent found
        /*SocketHandler.socket.once("startGame") {
            Log.d("Games", it[0].toString())
            val i = Intent(activity, Board::class.java)
            callback.launch(i)
        }*/

        MainScope().launch {
            // Retrieve profile data
            val response = HttpHandler.makeGamesRequest(HttpHandler.GamesRequest(null))

            // Hide loading bar and show basic ui
            view.findViewById<ProgressBar>(R.id.progressBar).visibility = ProgressBar.GONE
            view.findViewById<TextView>(R.id.textViewLoading).visibility = TextView.GONE
            view.findViewById<Button>(R.id.buttonNewGame).visibility = Button.VISIBLE
            view.findViewById<TextView>(R.id.textViewTable).visibility = TextView.VISIBLE
            view.findViewById<TableRow>(R.id.tableHeader).visibility = TableRow.VISIBLE
            val table = view.findViewById<TableLayout>(R.id.tableLayout)

            if (response.error) return@launch
            else if (response.ids.isEmpty()) {
                table.findViewById<TableRow>(R.id.tableNotFound).visibility = TableRow.VISIBLE
                return@launch
            }

            val rowOffset = table.childCount
            // If there are games, fill the table
            for (i in 0 until response.ids.size) {
                val layout = layoutInflater.inflate(R.layout.games_row, table)
                val row = table[i+rowOffset]
                row.findViewById<TextView>(R.id.textViewOpponent).text = response.nicknames[i]
                row.findViewById<TextView>(R.id.textViewStartDate).text = response.startDates[i]
                row.findViewById<TextView>(R.id.textViewLastMovDate).text = response.lastMovDates[i]

                val flagOffset = 0x1F1E6
                val asciiOffset = 0x41
                val firstChar = Character.codePointAt(response.codes[i], 0) - asciiOffset + flagOffset
                val secondChar = Character.codePointAt(response.codes[i], 1) - asciiOffset + flagOffset
                row.findViewById<TextView>(R.id.textViewCountry).text = String(Character.toChars(firstChar)) +
                        String(Character.toChars(secondChar)) + " " + response.countries[i]

                if (response.myTurns[i])
                    row.findViewById<ImageView>(R.id.imageViewMove).setImageDrawable(
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_check_circle_outline_40, null))

                row.findViewById<Button>(R.id.button).setOnClickListener {
                    Toast.makeText(requireContext(), "Game against ${response.ids[i]}", Toast.LENGTH_SHORT).show()
                }
            }

            // Load opponent images afterwards to reduce loading time
            for (i in 0 until response.ids.size) {
                if (response.hasImages[i]) {
                    val image = HttpHandler.makeImageRequest(HttpHandler.ImageRequest(response.ids[i])).image
                    table[i+rowOffset].findViewById<ImageView>(R.id.imageViewOpponent).setImageDrawable(image)
                }
            }
        }
    }
}