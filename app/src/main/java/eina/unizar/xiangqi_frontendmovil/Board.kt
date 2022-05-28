package eina.unizar.xiangqi_frontendmovil

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.get
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class Board : AppCompatActivity() {
    private lateinit var exitGameDialog: Dialog
    private lateinit var loseGameDialog: Dialog
    lateinit var winGameDialog: Dialog
    private var roomId = 0
    private var opponentId = 0
    private var red = true
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        // Enable backward navigation
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Construct exit game dialog
        exitGameDialog = Dialog(this)
        exitGameDialog.setContentView(R.layout.dialog_exit_game)

        // Construct lose game dialog
        loseGameDialog = Dialog(this)
        loseGameDialog.setContentView(R.layout.dialog_lose_game)

        // Construct win game dialog
        winGameDialog = Dialog(this)
        winGameDialog.setContentView(R.layout.dialog_win_game)

        // Load game
        roomId = intent.getIntExtra("roomId", -1)
        opponentId = intent.getIntExtra("opponentId", -1)
        MainScope().launch {
            val opponent = HttpHandler.makeInfoRequest(
                HttpHandler.InfoRequest(opponentId))
            val skins = HttpHandler.makeStoreRequest()
            val game = HttpHandler.makeLoadRequest(
                HttpHandler.LoadRequest(roomId))
            if (opponent.image) {
                val image = HttpHandler.makeImageRequest(
                    HttpHandler.ImageRequest(opponentId)).image
                findViewById<ImageView>(R.id.imageViewProfile).setImageDrawable(image)
            }

            // Hide loading bar and show basic ui
            findViewById<ProgressBar>(R.id.progressBar).visibility = ProgressBar.GONE
            findViewById<TextView>(R.id.textViewLoading).visibility = TextView.GONE

            if (opponent.error || skins.error || game.error) return@launch

            findViewById<ImageView>(R.id.imageViewProfile).visibility = ImageView.VISIBLE
            findViewById<TextView>(R.id.textViewBirthdateTitle).visibility = TextView.VISIBLE
            title = "Partida contra ${opponent.realname}"
            findViewById<TextView>(R.id.textViewNickname).text = "#${opponent.nickname}"
            findViewById<TextView>(R.id.textViewBirthdate).text = opponent.birthdate
            val flagOffset = 0x1F1E6
            val asciiOffset = 0x41
            val firstChar = Character.codePointAt(opponent.code, 0) - asciiOffset + flagOffset
            val secondChar = Character.codePointAt(opponent.code, 1) - asciiOffset + flagOffset
            findViewById<TextView>(R.id.textViewCountry).text = String(Character.toChars(firstChar)) +
                    String(Character.toChars(secondChar)) + " " + opponent.country
            findViewById<TextView>(R.id.textViewRanking).text = "Puesto en ranking: ${opponent.ranking}"

            findViewById<TextView>(R.id.textViewChatTitle).visibility = TextView.VISIBLE
            findViewById<ShapeableImageView>(R.id.shapeableImageView).visibility = ShapeableImageView.VISIBLE
            findViewById<TextInputLayout>(R.id.editTextChatMsg).visibility = TextInputLayout.VISIBLE
            findViewById<MaterialButton>(R.id.buttonSend).visibility = MaterialButton.VISIBLE

            findViewById<TextView>(R.id.textViewThemesTitle).visibility = TextView.VISIBLE
            val pieceThemes = mutableListOf<String>()
            val boardThemes = mutableListOf<String>()
            for (i in 0 until skins.ids.size) {
                if (skins.purchased[i]) {
                    if (skins.types[i] == 0) pieceThemes.add(skins.names[i])
                    else boardThemes.add(skins.names[i])
                }
            }
            val piecesSpinner = findViewById<TextInputLayout>(R.id.editTextThemePieces)
            piecesSpinner.visibility = View.VISIBLE
            val adapterPieces = ArrayAdapter(this@Board, android.R.layout.simple_list_item_1, pieceThemes)
            (piecesSpinner.editText as? AutoCompleteTextView)?.setAdapter(adapterPieces)
            piecesSpinner.editText!!.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    var index = 0
                    for (i in 0 until skins.ids.size) {
                        if (skins.names[i] == s.toString()) {
                            index = skins.ids[i]
                            break
                        }
                    }
                    findViewById<ShapeableImageView>(R.id.imageViewPiece).setImageResource(piecesSkins[index])
                }
            })
            val boardsSpinner = findViewById<TextInputLayout>(R.id.editTextThemeBoard)
            boardsSpinner.visibility = View.VISIBLE
            val adapterBoards = ArrayAdapter(this@Board, android.R.layout.simple_list_item_1, boardThemes)
            (boardsSpinner.editText as? AutoCompleteTextView)?.setAdapter(adapterBoards)
            boardsSpinner.editText!!.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    var index = 0
                    for (i in 0 until skins.ids.size) {
                        if (skins.names[i] == s.toString()) {
                            index = skins.ids[i]
                            break
                        }
                    }
                    findViewById<ShapeableImageView>(R.id.imageViewBoard).setImageResource(boardSkins[index-1])
                }
            })
            findViewById<ShapeableImageView>(R.id.imageViewBoard).visibility = ShapeableImageView.VISIBLE
            findViewById<ShapeableImageView>(R.id.imageViewPiece).visibility = ShapeableImageView.VISIBLE

            findViewById<TextView>(R.id.textViewBoardTitle).visibility = TextView.VISIBLE
            findViewById<ShapeableImageView>(R.id.shapeableImageViewFrame).visibility = ShapeableImageView.VISIBLE
            findViewById<TextInputLayout>(R.id.editTextMove).visibility = TextInputLayout.VISIBLE
            findViewById<MaterialButton>(R.id.buttonMove).visibility = MaterialButton.VISIBLE
            findViewById<MaterialButton>(R.id.buttonResign).visibility = MaterialButton.VISIBLE

            if (intent.getBooleanExtra("continue", false)) {
                val turn = intent.getBooleanExtra("turn", true)
                red = (game.moves.size % 2 == 0) && turn || (game.moves.size % 2 == 1) && !turn
                for (i in 0 until game.moves.size) {
                    if ((i % 2 == 0) && red || (i % 2 == 1) && !red) newMove(game.moves[i], true)
                    else newMove(game.moves[i], false)
                }
            }
            else {
                red = intent.getBooleanExtra("red", true)
            }
            SocketHandler.enterRoom(roomId, this@Board)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> exitGameDialog.show()
        }
        return true
    }

    override fun onBackPressed() {
        exitGameDialog.show()
    }

    fun onClickExitGameYes(view: View) {
        SocketHandler.leaveRoom(roomId)
        val intent = Intent(this, Home::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("fromGame", true)
        startActivity(intent)
        finish()
    }

    fun onClickExitGameNo(view: View) {
        exitGameDialog.hide()
    }

    fun onClickSendMsg(view: View) {
        val msg = findViewById<TextInputLayout>(R.id.editTextChatMsg).editText!!
        if (msg.text.toString().isNotEmpty()) {
            SocketHandler.sendMsg(roomId, msg.text.toString())
            newMsg(msg.text.toString(), true)
            msg.setText("")
        }
    }

    fun newMsg(msg: String, self: Boolean) {
        val chat = findViewById<LinearLayout>(R.id.linearLayoutMessages)
        if (self) {
            val layout = layoutInflater.inflate(R.layout.message_row_own, chat)
        }
        else {
            val layout = layoutInflater.inflate(R.layout.message_row_other, chat)
        }
        chat[chat.childCount-1].findViewById<TextView>(R.id.textViewMessage).text = msg
        findViewById<ScrollView>(R.id.scrollViewChat).postDelayed(Runnable {
            findViewById<ScrollView>(R.id.scrollViewChat).fullScroll(
                ScrollView.FOCUS_DOWN
            )
        }, 600)
    }

    fun onClickSendMove(view: View) {
        val msg = findViewById<TextInputLayout>(R.id.editTextMove).editText!!
        if (msg.text.toString().isNotEmpty()) {
            SocketHandler.doMov(roomId, msg.text.toString(), red)
            newMove(msg.text.toString(), true)
            msg.setText("")
        }
    }

    fun newMove(msg: String, self: Boolean) {
        val moves = findViewById<LinearLayout>(R.id.linearLayoutMoves)
        if (self) {
            val layout = layoutInflater.inflate(R.layout.message_row_own, moves)
        }
        else {
            val layout = layoutInflater.inflate(R.layout.message_row_other, moves)
        }
        val item = moves[moves.childCount-1]
        item.findViewById<TextView>(R.id.textViewMessage).text = msg
        item.findViewById<TextView>(R.id.textViewMessage).setTextColor(
            ResourcesCompat.getColor(resources, R.color.white, null))
        if (self && red || !self && !red)
            item.findViewById<ImageView>(R.id.imageViewBackground).setBackgroundColor(
                ResourcesCompat.getColor(resources, R.color.red, null))
        else
            item.findViewById<ImageView>(R.id.imageViewBackground).setBackgroundColor(
                ResourcesCompat.getColor(resources, R.color.black, null))
        findViewById<ScrollView>(R.id.scrollViewChat).postDelayed(Runnable {
            findViewById<ScrollView>(R.id.scrollViewChat).fullScroll(
                ScrollView.FOCUS_DOWN
            )
        }, 600)
    }

    fun onClickLose(view: View) {
        SocketHandler.lose(roomId, opponentId)
        loseGameDialog.show()
    }
}