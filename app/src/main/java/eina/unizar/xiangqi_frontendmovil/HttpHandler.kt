package eina.unizar.xiangqi_frontendmovil

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class HttpHandler {
    companion object {
        private const val login_url = "https://ptsv2.com/t/rcrml-1647992436/post"

        suspend fun makeLoginRequest(email: String, password: String): Boolean {
            return withContext(Dispatchers.IO) {
                val url = URL(login_url)
                (url.openConnection() as? HttpURLConnection)?.run {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/x-www-form-urlencoded; utf-8")
                    doOutput = true
                    outputStream.write(("email=$email&pwd=$password").toByteArray())
                    return@withContext true
                }
                return@withContext false
            }
        }
    }
}