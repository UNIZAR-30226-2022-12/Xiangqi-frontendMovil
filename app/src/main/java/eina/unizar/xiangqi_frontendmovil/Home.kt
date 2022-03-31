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

    fun onClickProfile(view: View) {
        val i = Intent(this, Profile::class.java)
        startActivity(i)
    }
}