package eina.unizar.xiangqi_frontendmovil

import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.Polling
import java.net.URISyntaxException

object SocketHandler {

    lateinit var mSocket: Socket

    @Synchronized
    fun setSocket() {
        try {
            val options = IO.Options.builder()
                .setTransports(arrayOf(Polling.NAME))
                .build()
            mSocket = IO.socket("http://ec2-3-82-235-243.compute-1.amazonaws.com:3005", options)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    @Synchronized
    fun getSocket(): Socket {
        return mSocket
    }

    @Synchronized
    fun establishConnection() {
        mSocket.connect()
    }

    @Synchronized
    fun closeConnection() {
        mSocket.disconnect()
    }
}