package eina.unizar.xiangqi_frontendmovil

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
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
        }
    }

    fun editProfile(view: View) {
        Toast.makeText(this ,"No implementado aun", Toast.LENGTH_SHORT).show()
    }
    fun deleteProfile(view: View) {
         var context = this
        MainScope().launch {
            if (HttpHandler.makeDeletionRequest().error){
                Toast.makeText(context ,"No se ha podido eliminar la cuenta", Toast.LENGTH_SHORT).show()
            }else{
                val intent = Intent(context, SignIn::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.putExtra("Exit me", true)
                startActivity(intent)
                finish()
            }
        }
    }


}