package eina.unizar.xiangqi_frontendmovil

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class HttpHandler {
    companion object {
        private const val login_url = "https://httpdump.io/cwwdb"
        //private const val url = "http://ec2-3-82-235-243.compute-1.amazonaws.com:3000"

        suspend fun makeLoginRequest(email: String, password: String): Boolean {
            return withContext(Dispatchers.IO) {
                try{
                    val conn: HttpURLConnection = URL(login_url).openConnection() as HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; utf-8")
                    conn.connectTimeout = 5000
                    conn.doOutput = true
                    conn.outputStream.write(("email=$email&pwd=$password").toByteArray())
                    conn.connect()
                    val ret = conn.inputStream != null
                    //conn.disconnect()
                    return@withContext ret
                }
                catch (e: SocketTimeoutException) {
                    // Timeout msg
                    Log.d("HTTP", "Timeout Exception")
                }
                catch (e: IOException) {
                    // Url not found
                    Log.d("HTTP", "IO Exception")
                }
                return@withContext false
            }
        }

        suspend fun makeJsonLoginRequest(email: String, password: String): Boolean {
            return withContext(Dispatchers.IO) {
                try{
                    val conn: HttpURLConnection = URL(login_url).openConnection() as HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Content-Type", "application/json; utf-8")
                    //conn.setRequestProperty("Accept", "application/json");
                    conn.connectTimeout = 5000
                    conn.doOutput = true
                    conn.outputStream.write(("{\"email\":\"$email\", \"pwd\":\"$password\"}").toByteArray())
                    Log.d("HTTP", "Connecting...")
                    conn.connect()
                    val ret = conn.inputStream != null
                    //conn.disconnect()
                    return@withContext ret
                }
                catch (e: SocketTimeoutException) {
                    // Timeout msg
                    Log.d("HTTP", "Timeout Exception")
                }
                catch (e: IOException) {
                    // Url not found
                    Log.d("HTTP", "IO Exception")
                }
                return@withContext false
            }
        }
    }
}