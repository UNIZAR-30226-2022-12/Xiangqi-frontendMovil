package eina.unizar.xiangqi_frontendmovil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    fun profileButton(view: View) {
        val i = Intent(this, Profile::class.java)
        startActivity(i)
    }

    fun friendButton(view: View) {
        val i = Intent(this, Profile::class.java)
        i.putExtra("id", 18)
        startActivity(i)
    }
    fun playButton(view: View) {}
    fun rankingButton(view: View) {}
    fun shopButton(view: View) {}
    fun historyButton(view: View) {}
    fun skinButton(view: View) {}
}