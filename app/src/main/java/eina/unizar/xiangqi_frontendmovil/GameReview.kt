package eina.unizar.xiangqi_frontendmovil

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.get

class GameReview : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_review)
        title = resources.getString(R.string.history_review)

        // Enable backward navigation
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val ids = intent.getIntegerArrayListExtra("ids")!!
        val nicknames = intent.getStringArrayListExtra("nicknames")!!
        val codes = intent.getStringArrayListExtra("codes")!!
        val colors = intent.getStringArrayListExtra("colors")!!
        val origins = intent.getStringArrayListExtra("origins")!!
        val destinations = intent.getStringArrayListExtra("destinations")!!

        val table = findViewById<LinearLayout>(R.id.linearLayoutTable)
        // Fill the table
        for (i in 0 until ids.size) {
            val layout = layoutInflater.inflate(R.layout.review_row, table)
            val row = table[i]
            row.findViewById<TextView>(R.id.textViewMov).text =  ids[i].toString()
            row.findViewById<TextView>(R.id.textViewPlayer).text =  nicknames[i]
            row.findViewById<TextView>(R.id.textViewOrigin).text =  origins[i]
            row.findViewById<TextView>(R.id.textViewDestination).text =  destinations[i]

            val flagOffset = 0x1F1E6
            val asciiOffset = 0x41
            val firstChar = Character.codePointAt(codes[i], 0) - asciiOffset + flagOffset
            val secondChar = Character.codePointAt(codes[i], 1) - asciiOffset + flagOffset
            row.findViewById<TextView>(R.id.textViewCountry).text = String(Character.toChars(firstChar)) +
                    String(Character.toChars(secondChar))

            if (colors[i] == "negro") {
                row.findViewById<ImageView>(R.id.imageViewColor).setImageResource(R.drawable.skin_pieces_negro)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }
}