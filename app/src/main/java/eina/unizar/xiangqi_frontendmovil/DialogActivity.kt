package eina.unizar.xiangqi_frontendmovil

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast

class DialogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_game_req)
        setTitle(R.string.games_req)
        findViewById<TextView>(R.id.textViewGameReqBody).text = "${intent.getStringExtra("nickname")} te ha retado a una partida"
    }

    fun onClickReject(view: View) {
        Toast.makeText(this, "Invitación rechazada", Toast.LENGTH_SHORT).show()
        finish()
        SocketHandler.rejectReq()
    }

    fun onClickAccept(view: View) {
        finish()
        SocketHandler.acceptReq(this)
    }
}