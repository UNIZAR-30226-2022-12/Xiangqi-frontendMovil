package eina.unizar.xiangqi_frontendmovil.home_fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import eina.unizar.xiangqi_frontendmovil.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class Games : Fragment(R.layout.fragment_games) {
    lateinit var dialog: Dialog
    private var friendGame = false
    private var friendPos = 0
    private val callback = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        dialog.hide()
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.home_content, Games())
            .commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val i = Intent(activity, Board::class.java)
        callback.launch(i)
        // Construct new game dialog
        dialog = object : Dialog(requireContext()) {
            override fun onDetachedFromWindow() {
                super.onDetachedFromWindow()
                if (!dialog.findViewById<Button>(R.id.buttonGameStart).isEnabled) cancelSearch()
                findViewById<SwitchCompat>(R.id.switchFriend).isChecked = false
            }
        }
        dialog.setContentView(R.layout.dialog_new_game)

        dialog.findViewById<SwitchCompat>(R.id.switchFriend).setOnCheckedChangeListener { _, isChecked ->
            checkFriendGame(isChecked)
        }

        dialog.findViewById<TextInputLayout>(R.id.editTextFriend)
            .editText?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val button = dialog.findViewById<Button>(R.id.buttonGameStart)
                    button.text = "Nueva partida contra $s"
                    button.isEnabled = true
                }
            })

        dialog.findViewById<Button>(R.id.buttonGameStart).setOnClickListener {
            startSearch()
        }

        dialog.findViewById<Button>(R.id.buttonGameCancel).setOnClickListener {
            cancelSearch()
        }

        view.findViewById<Button>(R.id.buttonNewGame).setOnClickListener {
            dialog.show()
            SocketHandler.refreshFriends()
        }

        // Set listener for opponent found
        /*SocketHandler.socket.once("startGame") {
            Log.d("Games", it[0].toString())
            val i = Intent(activity, Board::class.java)
            callback.launch(i)
        }*/

        MainScope().launch {
            // Retrieve profile data
            val response = HttpHandler.makeGamesRequest(HttpHandler.GamesRequest(null))
            if (!isAdded) return@launch

            // Hide loading bar and show basic ui
            view.findViewById<ProgressBar>(R.id.progressBar).visibility = ProgressBar.GONE
            view.findViewById<TextView>(R.id.textViewLoading).visibility = TextView.GONE

            view.findViewById<Button>(R.id.buttonNewGame).visibility = Button.VISIBLE
            view.findViewById<TextView>(R.id.textViewTable).visibility = TextView.VISIBLE
            view.findViewById<LinearLayout>(R.id.linearLayoutHeader).visibility = LinearLayout.VISIBLE
            view.findViewById<View>(R.id.divider).visibility = View.VISIBLE

            val table = view.findViewById<LinearLayout>(R.id.linearLayoutTable)
            if (response.error) return@launch
            else if (response.ids.isEmpty()) {
                table.findViewById<TextView>(R.id.textViewNotFound).visibility = TextView.VISIBLE
                return@launch
            }

            val rowOffset = table.childCount
            // If there are games, fill the table
            for (i in 0 until response.ids.size) {
                val layout = layoutInflater.inflate(R.layout.games_row, table)
                val row = table[i+rowOffset]
                row.findViewById<TextView>(R.id.textViewStartDate).text = response.startDates[i]
                row.findViewById<TextView>(R.id.textViewLastMovDate).text = response.lastMovDates[i]

                val flagOffset = 0x1F1E6
                val asciiOffset = 0x41
                val firstChar = Character.codePointAt(response.codes[i], 0) - asciiOffset + flagOffset
                val secondChar = Character.codePointAt(response.codes[i], 1) - asciiOffset + flagOffset
                row.findViewById<TextView>(R.id.textViewCountry).text = String(Character.toChars(firstChar)) +
                        String(Character.toChars(secondChar)) + " " + response.countries[i]

                val nickname = row.findViewById<TextView>(R.id.textViewOpponent)
                nickname.text = response.nicknames[i]
                nickname.setOnClickListener {
                    val intent = Intent(requireContext(), OtherProfile::class.java)
                    intent.putExtra("id", response.ids[i])
                    intent.putExtra("nickname", response.nicknames[i])
                    callback.launch(intent)
                }

                if (response.myTurns[i])
                    row.findViewById<ImageView>(R.id.imageViewMove).setImageDrawable(
                        ResourcesCompat.getDrawable(resources,
                            R.drawable.ic_baseline_check_circle_outline_40, null))

                row.findViewById<Button>(R.id.button).setOnClickListener {
                    // Debug, integrate when appropriate
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

    private fun checkFriendGame(isChecked: Boolean) {
        if (isChecked) {
            friendGame = true
            val spinner = dialog.findViewById<TextInputLayout>(R.id.editTextFriend)
            spinner.visibility = View.VISIBLE
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, SocketHandler.getOnlineFriends())
            (spinner.editText as? AutoCompleteTextView)?.setAdapter(adapter)
            val constraintLayout = dialog.findViewById<ConstraintLayout>(R.id.parentLayout)
            val constraintSet = ConstraintSet()
            constraintSet.clone(constraintLayout)
            constraintSet.connect(
                R.id.buttonGameStart,
                ConstraintSet.TOP,
                R.id.editTextFriend,
                ConstraintSet.BOTTOM,
                10
            )
            constraintSet.applyTo(constraintLayout)
            val button = dialog.findViewById<Button>(R.id.buttonGameStart)
            button.text = resources.getText(R.string.games_select_friend_button)
            button.isEnabled = false
        }
        else {
            friendGame = false
            val spinner = dialog.findViewById<TextInputLayout>(R.id.editTextFriend)
            spinner.visibility = TextInputLayout.GONE
            spinner.editText?.setText("")
            val constraintLayout = dialog.findViewById<ConstraintLayout>(R.id.parentLayout)
            val constraintSet = ConstraintSet()
            constraintSet.clone(constraintLayout)
            constraintSet.connect(
                R.id.buttonGameStart,
                ConstraintSet.TOP,
                R.id.switchFriend,
                ConstraintSet.BOTTOM,
                10
            )
            constraintSet.applyTo(constraintLayout)
            val button = dialog.findViewById<Button>(R.id.buttonGameStart)
            button.text = resources.getText(R.string.games_start)
            button.isEnabled = true
        }
    }

    private fun startSearch() {
        // Begin opponent search
        dialog.findViewById<SwitchCompat>(R.id.switchSync).isEnabled = false
        dialog.findViewById<SwitchCompat>(R.id.switchFriend).isEnabled = false
        val friends = dialog.findViewById<TextInputLayout>(R.id.editTextFriend)
        friends.isEnabled = false
        dialog.findViewById<ProgressBar>(R.id.progressBarConnecting).visibility = ProgressBar.VISIBLE
        dialog.findViewById<Button>(R.id.buttonGameCancel).visibility = Button.VISIBLE

        val constraintLayout = dialog.findViewById<ConstraintLayout>(R.id.parentLayout)
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        constraintSet.clear(
            R.id.buttonGameStart,
            ConstraintSet.BOTTOM,
        )
        constraintSet.applyTo(constraintLayout)

        val start = dialog.findViewById<Button>(R.id.buttonGameStart)
        start.isEnabled = false
        if (friends.editText?.text.toString() == "") {
            start.text = getText(R.string.games_searching)
            SocketHandler.searchRandomOpponent(requireActivity(), callback)
        }
        else {
            start.text = "Conectando con ${friends.editText?.text}"
        }
        Log.d("Games", "start")
    }

    fun cancelSearch() {
        // Cancel opponent search
        dialog.findViewById<SwitchCompat>(R.id.switchSync).isEnabled = true
        dialog.findViewById<SwitchCompat>(R.id.switchFriend).isEnabled = true
        val friends = dialog.findViewById<TextInputLayout>(R.id.editTextFriend)
        friends.isEnabled = true
        dialog.findViewById<ProgressBar>(R.id.progressBarConnecting).visibility = ProgressBar.GONE
        dialog.findViewById<Button>(R.id.buttonGameCancel).visibility = Button.GONE


        val constraintLayout = dialog.findViewById<ConstraintLayout>(R.id.parentLayout)
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        constraintSet.connect(
            R.id.buttonGameStart,
            ConstraintSet.BOTTOM,
            R.id.parentLayout,
            ConstraintSet.BOTTOM,
            20
        )
        constraintSet.applyTo(constraintLayout)

        val start = dialog.findViewById<Button>(R.id.buttonGameStart)
        start.isEnabled = true
        if (friends.editText?.text.toString() == "") {
            start.text = getText(R.string.games_start)
            //SocketHandler.cancelSearch()
        }
        else {
            start.text = "Nueva partida contra ${friends.editText?.text.toString()}"
        }
    }
}