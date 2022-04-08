package eina.unizar.xiangqi_frontendmovil

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
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
            val response = HttpHandler.makeProfileRequest(-1)
            findViewById<TextView>(R.id.textViewRealname).text = response.realname
            findViewById<TextView>(R.id.textViewNickname).text = response.nickname
            findViewById<TextView>(R.id.textViewBirthdate).text = response.birthdate
            findViewById<TextView>(R.id.textViewCountry).text = response.country
            var img = HttpHandler.makeImageRequest(-1)
            if(img == null){
                img = Drawable.createFromPath("src/main/res/drawable/profile.jpeg")
            }
            findViewById<ImageView>(R.id.imageView).setImageDrawable(img)
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