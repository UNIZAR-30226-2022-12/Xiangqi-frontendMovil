package eina.unizar.xiangqi_frontendmovil

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit


object SocketHandler {
    val socket: Socket
    lateinit var onlineFriends: JSONArray

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
        socket.connect()
        socket.emit("enter", JSONObject("{id:\"${HttpHandler.id}\"}"))
        refreshFriends()
    }

    fun disconnect() {
        socket.disconnect()
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

    fun searchRandomOpponent(context: Context, callback: ActivityResultLauncher<Intent>) {
        socket.once("startGame") {
            callback.launch(Intent(context, Board::class.java))
        }
        socket.emit("searchRandomOpponent", JSONObject("{id:${HttpHandler.id}}"))
    }

    fun cancelSearch() {
        socket.off("startGame")
        socket.emit("cancelSearch", JSONObject("{id:${HttpHandler.id}}"))
    }

    fun sendFriendRequest(id: Int) {
        socket.emit("sendFriendRequest", JSONObject("{\"id\":${HttpHandler.id}, " +
                "\"idFriend\":$id}"))
    }
}