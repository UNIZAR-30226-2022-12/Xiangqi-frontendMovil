package eina.unizar.xiangqi_frontendmovil

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class Games : Fragment(R.layout.fragment_games) {
    lateinit var dialog: Dialog
    val callback = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
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
        dialog.findViewById<Button>(R.id.buttonGameStart).setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View) {
                val button = dialog.findViewById<Button>(R.id.buttonGameStart)
                button.isEnabled = false
                button.text = getText(R.string.games_searching)
                SocketHandler.searchRandomOpponent()
            }
        })

        view.findViewById<Button>(R.id.buttonNewGame).setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View) {
                dialog.show()
            }
        })

        // Set listener for opponent found
        SocketHandler.socket.once("startGame") {
            Log.d("Games", it[0].toString())
            val i = Intent(activity, Board::class.java)
            callback.launch(i)
        }

        MainScope().launch {
            // Retrieve profile data
            val response = HttpHandler.makeProfileRequest(HttpHandler.ProfileRequest(null))

            // Hide loading bar and fill async games
            view.findViewById<ProgressBar>(R.id.progressBar).visibility = ProgressBar.GONE
            view.findViewById<TextView>(R.id.textViewLoading).visibility = TextView.GONE
            view.findViewById<Button>(R.id.buttonNewGame).visibility = Button.VISIBLE

            if (response.error) return@launch
        }
    }
}