package eina.unizar.xiangqi_frontendmovil

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        MainScope().launch {
            val response = HttpHandler.makeProfileRequest(-1)
            findViewById<TextView>(R.id.textViewRealname).text = response.realname
            findViewById<TextView>(R.id.textViewNickname).text = response.nickname
            findViewById<TextView>(R.id.textViewBirthdate).text = response.birthdate
            findViewById<TextView>(R.id.textViewCountry).text = response.country
        }
    }
}