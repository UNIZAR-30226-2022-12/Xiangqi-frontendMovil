package eina.unizar.xiangqi_frontendmovil

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class HttpHandler {
    companion object {
        private const val login_url = "https://httpdump.io/c7wdy"

        suspend fun makeLoginRequest(email: String, password: String): Boolean {
            return withContext(Dispatchers.IO) {
                val url = URL(login_url)
                val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; utf-8")
                conn.doOutput = true
                conn.outputStream.write(("email=$email&pwd=$password").toByteArray())
                conn.connect()
                return@withContext conn.inputStream != null
            }
        }
    }
}