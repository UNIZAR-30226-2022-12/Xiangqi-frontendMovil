package eina.unizar.xiangqi_frontendmovil

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class Profile : AppCompatActivity() {
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setTitle(R.string.profile_title)

        // Construct delete account dialog
        dialog = Dialog(this)
        dialog.setContentView(R.layout.fragment_delete_account)

        MainScope().launch {
            // Retrieve profile data
            val response = HttpHandler.makeProfileRequest(HttpHandler.ProfileRequest(null))
            if (response.image) {
                val image = HttpHandler.makeImageRequest(HttpHandler.ImageRequest(null)).image
                findViewById<ImageView>(R.id.imageViewProfile).setImageDrawable(image)
            }

            // Hide loading bar and fill text fields
            findViewById<ProgressBar>(R.id.progressBar).visibility = ProgressBar.GONE
            findViewById<TextView>(R.id.textViewLoading).visibility = TextView.GONE

            findViewById<ImageView>(R.id.imageViewProfile).visibility = ImageView.VISIBLE
            findViewById<TextView>(R.id.textViewBirthdateTitle).visibility = TextView.VISIBLE
            findViewById<TextView>(R.id.textViewRealname).text = response.realname
            findViewById<TextView>(R.id.textViewNickname).text = "#${response.nickname}"
            findViewById<TextView>(R.id.textViewBirthdate).text = response.birthdate
            val flagOffset = 0x1F1E6
            val asciiOffset = 0x41
            val firstChar = Character.codePointAt(response.code, 0) - asciiOffset + flagOffset
            val secondChar = Character.codePointAt(response.code, 1) - asciiOffset + flagOffset
            findViewById<TextView>(R.id.textViewCountry).text = String(Character.toChars(firstChar)) +
                    String(Character.toChars(secondChar)) + " " + response.country

            findViewById<ImageView>(R.id.imageViewPoints).visibility = ImageView.VISIBLE
            findViewById<TextView>(R.id.textViewPointsTitle).visibility = TextView.VISIBLE
            findViewById<TextView>(R.id.textViewPoints).text = "${response.points} puntos"

            findViewById<ImageView>(R.id.imageViewRanking).visibility = ImageView.VISIBLE
            findViewById<TextView>(R.id.textViewRankingTitle).visibility = TextView.VISIBLE
            findViewById<TextView>(R.id.textViewRanking).text = "Puesto ${response.points}"

            findViewById<ImageView>(R.id.imageViewCalendar).visibility = ImageView.VISIBLE
            findViewById<TextView>(R.id.textViewCalendarTitle).visibility = TextView.VISIBLE
            findViewById<TextView>(R.id.textViewCalendar).text = response.registerdate

            findViewById<ImageView>(R.id.imageViewFriends).visibility = ImageView.VISIBLE
            findViewById<TextView>(R.id.textViewFriendsTitle).visibility = TextView.VISIBLE
            findViewById<TextView>(R.id.textViewFriends).text = "${response.points} amigos"

            val winrate = findViewById<ProgressBar>(R.id.progressBarWinrate)
            winrate.visibility = ProgressBar.VISIBLE
            winrate.max = response.played
            winrate.progress = response.won
            findViewById<TextView>(R.id.textViewWinrate).text = "Partidas ganadas: ${response.won}" +
                    "  Partidas jugadas: ${response.played}"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val result = super.onCreateOptionsMenu(menu)
        menu?.add(Menu.NONE, Menu.FIRST, Menu.NONE, R.string.edit_title)
        menu?.add(Menu.NONE, Menu.FIRST+1, Menu.NONE, R.string.profile_delete)
        return result
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            Menu.FIRST -> Toast.makeText(this ,"No implementado aun", Toast.LENGTH_SHORT).show()
            Menu.FIRST+1 -> dialog.show()
        }
        return super.onOptionsItemSelected(item)
    }

    fun editProfile(view: View) {
        Toast.makeText(this ,"No implementado aun", Toast.LENGTH_SHORT).show()
    }

    fun onClickCancel(view: View) {
        dialog.hide()
    }

    fun onClickDelete(view: View) {
        val context = this
        MainScope().launch {
            if (HttpHandler.makeDeletionRequest().error){
                Toast.makeText(context ,"No se ha podido eliminar la cuenta", Toast.LENGTH_SHORT).show()
            }
            else {
                val intent = Intent(context, SignIn::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                Toast.makeText(context ,"La cuenta ha sido eliminada correctamente", Toast.LENGTH_LONG).show()
                startActivity(intent)
                finish()
            }
        }
    }
}