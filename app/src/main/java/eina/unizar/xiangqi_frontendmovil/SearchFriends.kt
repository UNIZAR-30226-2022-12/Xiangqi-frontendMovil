package eina.unizar.xiangqi_frontendmovil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.core.view.get
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class SearchFriends : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_friends)
        title = resources.getString(R.string.friends_search)

        // Enable backward navigation
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Add listener to remove editText error message after editing it
        val nickname: TextInputLayout = findViewById(R.id.editTextNickname)
        nickname.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                nickname.error = ""
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    fun onClickSearchUsers(view: View) {
        val nickname = findViewById<TextInputLayout>(R.id.editTextNickname)
        if (nickname.editText?.text.toString() == "") {
            nickname.error = "Por favor, indique un nombre de usuario"
            return
        }
        val button = findViewById<Button>(R.id.buttonSearch)
        button.isEnabled = false
        val spinner = findViewById<ProgressBar>(R.id.progressBarLoading)
        spinner.visibility = ProgressBar.VISIBLE
        val notFound = findViewById<TextView>(R.id.textViewNotFound)
        notFound.visibility = TextView.GONE
        val table = findViewById<LinearLayout>(R.id.linearLayoutTable)
        table.removeViews(1, table.childCount-1)

        val context = this
        MainScope().launch {
            val response = HttpHandler.makeSearchRequest(HttpHandler.SearchRequest(nickname.editText?.text.toString()))
            button.isEnabled = true
            spinner.visibility = ProgressBar.GONE
            if (response.error) return@launch
            else if (response.ids.isEmpty()) {
                notFound.text = "No se han encontrado usuarios por ${nickname.editText?.text.toString()}"
                notFound.visibility = TextView.VISIBLE
                return@launch
            }

            val rowOffset = table.childCount
            // If there are users, fill the table
            for (i in 0 until  response.ids.size) {
                val layout = layoutInflater.inflate(R.layout.search_row, table)
                val row = table[i+rowOffset]
                row.findViewById<TextView>(R.id.textViewName).text =  response.realnames[i]
                row.findViewById<TextView>(R.id.textViewBirthdate).text =  response.birthdates[i]

                val flagOffset = 0x1F1E6
                val asciiOffset = 0x41
                val firstChar = Character.codePointAt(response.codes[i], 0) - asciiOffset + flagOffset
                val secondChar = Character.codePointAt(response.codes[i], 1) - asciiOffset + flagOffset
                row.findViewById<TextView>(R.id.textViewCountry).text = String(Character.toChars(firstChar)) +
                        String(Character.toChars(secondChar)) + " " +  response.countries[i]

                val nickname = row.findViewById<TextView>(R.id.textViewUser)
                nickname.text =  response.nicknames[i]
                nickname.setOnClickListener {
                    val intent = Intent(context, OtherProfile::class.java)
                    intent.putExtra("id",  response.ids[i])
                    intent.putExtra("nickname",  response.nicknames[i])
                    startActivity(intent)
                }

                val send = row.findViewById<Button>(R.id.buttonSend)
                if (response.sents[i]) {
                    send.isEnabled = false
                    send.text = resources.getString(R.string.search_sent)
                }
                else {
                    send.setOnClickListener {
                        send.isEnabled = false
                        send.text = resources.getString(R.string.search_sent)
                        SocketHandler.sendFriendRequest(response.ids[i])
                    }
                }
            }

            // Load opponent images afterwards to reduce loading time
            for (i in 0 until  response.ids.size) {
                if (response.hasImages[i]) {
                    val image = HttpHandler.makeImageRequest(HttpHandler.ImageRequest(response.ids[i])).image
                    table[i+rowOffset].findViewById<ImageView>(R.id.imageViewUser).setImageDrawable(image)
                }
            }
        }
    }
}