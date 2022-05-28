package eina.unizar.xiangqi_frontendmovil

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import eina.unizar.xiangqi_frontendmovil.home_fragments.Games
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit


object SocketHandler {
    val socket: Socket
    lateinit var onlineFriends: JSONArray
    var opponentId: Int = 0
    var roomId: Int = 0

    init {
        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .build()
        IO.setDefaultOkHttpCallFactory(okHttpClient)
        IO.setDefaultOkHttpWebSocketFactory(okHttpClient)
        socket = IO.socket("http://ec2-3-82-235-243.compute-1.amazonaws.com:3005")
    }

    fun connect(context: Context) {
        socket.on("onlineFriends") {
            onlineFriends = it[0] as JSONArray
        }
        socket.on("friendRequest") {
            (context as Activity).runOnUiThread {
                Toast.makeText(context, "Has recibido una solicitud de amistad", Toast.LENGTH_SHORT).show()
            }
        }
        enableRequests(context)
        socket.connect()
        socket.emit("enter", JSONObject("{id:\"${HttpHandler.id}\"}"))
        refreshFriends()
    }

    fun disconnect() {
        socket.disconnect()
    }

    fun enableRequests(context: Context) {
        socket.on("gameRequest") {
            opponentId = (it[0] as JSONObject).getInt("id")
            roomId = (it[0] as JSONObject).getString("idSala").toInt()
            MainScope().launch {
                val nickname = HttpHandler.makeNicknameRequest(HttpHandler.NicknameRequest(opponentId)).nickname
                (context as Activity).runOnUiThread {
                    context.startActivity(Intent(context, DialogActivity::class.java).putExtra("nickname", nickname))
                }
            }
        }
    }

    fun disableRequests() {
        socket.off("gameRequest")
    }

    fun refreshFriends() {
        socket.emit("getOnlineFriends", JSONObject("{id:\"${HttpHandler.id}\"}"))
    }

    fun getOnlineFriends(): List<String> {
        val friends = mutableListOf<String>()
        for (i in 0 until onlineFriends.length()) {
            friends.add(onlineFriends.getJSONObject(i).getString("nickname"))
        }
        return friends.toList()
    }

    fun sendFriendRequest(id: Int) {
        socket.emit("sendFriendRequest", JSONObject("{\"id\":${HttpHandler.id}, " +
                "\"idFriend\":$id}"))
    }

    fun searchRandomOpponent(context: Context, callback: ActivityResultLauncher<Intent>) {
        socket.once("startGame") {
            socket.off("gameRequest")
            val intent = Intent(context, Board::class.java)
            intent.putExtra("roomId", (it[0] as JSONObject).getInt("idSala"))
            intent.putExtra("opponentId", (it[0] as JSONObject).getInt("idOponent"))
            intent.putExtra("red", (it[0] as JSONObject).getBoolean("rojo"))
            callback.launch(intent)
        }
        socket.emit("searchRandomOpponent", JSONObject("{\"id\":\"${HttpHandler.id}\"}"))
    }

    fun cancelRandom() {
        socket.off("startGame")
        socket.emit("cancelGameRandom", JSONObject("{\"id\":\"${HttpHandler.id}\"}"))
    }

    fun sendGameRequest(pos: Int, context: Context, fragment: Fragment, callback: ActivityResultLauncher<Intent>) {
        socket.once("rejectReq") {
            socket.off("startGame")
            (context as Activity).runOnUiThread {
                (fragment as Games).cancelSearch()
                Toast.makeText(context,
                    "${onlineFriends.getJSONObject(pos).getString("nickname")} ha rechazado tu invitación",
                    Toast.LENGTH_SHORT).show()
            }
        }
        socket.once("startGame") {
            socket.off("rejectReq")
            socket.off("gameRequest")
            val intent = Intent(context, Board::class.java)
            intent.putExtra("roomId", (it[0] as JSONObject).getInt("idSala"))
            intent.putExtra("opponentId", (it[0] as JSONObject).getInt("idOponent"))
            intent.putExtra("red", (it[0] as JSONObject).getBoolean("rojo"))
            callback.launch(intent)
        }
        socket.emit("sendGameRequest", JSONObject("{\"id\":\"${HttpHandler.id}\", " +
                "\"idFriend\":${onlineFriends.getJSONObject(pos).getInt("id")}}"))
    }

    fun cancelRequest() {
        socket.off("startGame")
        socket.off("rejectReq")
        socket.emit("cancelGameRequest", JSONObject("{\"id\":\"${HttpHandler.id}\"}"))
    }

    fun rejectReq() {
        socket.emit("rejectReq", JSONObject("{\"id\":\"$opponentId\"}"))
        socket.emit("cancelGameRequest", JSONObject("{\"id\":\"$opponentId\"}"))
    }

    fun acceptReq(context: Context) {
        socket.once("startGame") {
            socket.off("gameRequest")
            val intent = Intent(context, Board::class.java)
            intent.putExtra("roomId", (it[0] as JSONObject).getInt("idSala"))
            intent.putExtra("opponentId", (it[0] as JSONObject).getInt("idOponent"))
            intent.putExtra("red", (it[0] as JSONObject).getBoolean("rojo"))
            context.startActivity(intent)
        }
        socket.emit("acceptReq", JSONObject("{\"id\":\"${HttpHandler.id}\", " +
                "\"idFriend\":\"$opponentId\", " +
                "\"idSala\":\"$roomId\"}"))
    }

    fun enterRoom(roomId: Int, board: Board) {
        socket.on("my msg") {
            board.runOnUiThread {
                board.newMsg(it[0].toString(), false)
            }
        }
        socket.on("opMov") {
            board.runOnUiThread {
                board.newMove((it[0] as JSONObject).getString("mov"), false)
            }
        }
        socket.on("win") {
            board.runOnUiThread {
                board.winGameDialog.show()
            }
        }
        socket.emit("enterRoom", JSONObject("{\"id\":\"$roomId\"}"))
    }

    fun leaveRoom(roomId: Int) {
        socket.off("my msg")
        socket.off("opMov")
        socket.emit("leaveRoom", JSONObject("{\"id\":\"$roomId\"}"))
    }

    fun sendMsg(roomId: Int, msg: String) {
        socket.emit("sendMsg", JSONObject("{\"id\":\"$roomId\", " +
                "\"msg\":\"$msg\"}}"))
    }

    fun doMov(roomId: Int, msg: String, red: Boolean) {
        val color = if (red) "rojo" else "negro"
        socket.emit("doMov", JSONObject("{\"id\":\"$roomId\", " +
                "\"mov\":\"$msg\", " +
                "\"color\":\"$color\"}"))
    }

    fun lose(roomId: Int, winnerId: Int) {
        socket.emit("lose", JSONObject("{\"idSala\":\"$roomId\", " +
                "\"idGanador\":\"$winnerId\"}}"))
    }
}