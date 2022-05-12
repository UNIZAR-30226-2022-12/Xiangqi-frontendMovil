package eina.unizar.xiangqi_frontendmovil

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.OkHttpClient
import org.json.JSONObject
import java.util.concurrent.TimeUnit


object SocketHandler {
    val socket: Socket

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

    fun searchRandomOpponent() {
        socket.connect()
        socket.emit("searchRandomOpponent", JSONObject("{id:${HttpHandler.id}}"))
    }

    fun cancelSearch() {
        if (socket.connected()) {
            socket.emit("cancelSearch", JSONObject("{id:${HttpHandler.id}}"))
            socket.disconnect()
        }
    }
}