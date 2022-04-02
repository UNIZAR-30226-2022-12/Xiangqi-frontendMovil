package eina.unizar.xiangqi_frontendmovil

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL


class HttpHandler {
    companion object {
        // TODO: save session token and user id
        private const val base_url = "http://ec2-3-82-235-243.compute-1.amazonaws.com:3000"

        // TODO: pass in/out parameters as array or data class with parser
        suspend fun makeLoginRequest(email: String, password: String): String {
            return withContext(Dispatchers.IO) {
                try{
                    val conn: HttpURLConnection = URL("$base_url/do-login").openConnection() as HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Content-Type", "application/json; utf-8")
                    conn.connectTimeout = 5000
                    conn.doOutput = true
                    conn.outputStream.write(("{\"email\":\"$email\", " +
                                             "\"pwd\":\"$password\"}")
                                            .toByteArray())
                    conn.connect()
                    // TODO: parse response body
                    val ret = conn.inputStream != null
                    if (ret) return@withContext BufferedReader(conn.inputStream.reader()).readText()//Log.d("HTTP", BufferedReader(conn.inputStream.reader()).readText())
                    else return@withContext "Conn failed"
                }
                catch (e: SocketTimeoutException) {
                    // Timeout msg
                    return@withContext "TimeoutException"
                }
                catch (e: IOException) {
                    // Url not found
                    return@withContext "IOException"
                }
            }
        }

        // TODO: pass in/out parameters as array or data class with parser
        suspend fun makeRegisterRequest(nickname: String, name: String, email: String, password: String,
                                        date: String, code: String, country: String): String {
            return withContext(Dispatchers.IO) {
                try{
                    val conn: HttpURLConnection = URL("$base_url/do-create").openConnection() as HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Content-Type", "application/json; utf-8")
                    conn.connectTimeout = 5000
                    conn.doOutput = true

                    // TODO: inject image as byte array, and pass country from getCountries request
                    conn.outputStream.write(("{\"nickname\":\"$nickname\", " +
                            "\"name\":\"$name\", " +
                            "\"email\":\"$email\", " +
                            "\"date\":\"$date\", " +
                            "\"country\":{\"code\":\"AD\", \"name\":\"Andorra\"}" +
                            "\"pwd\":\"$password\", " +
                            "\"image\":\"a\"}")
                            .toByteArray())
                    conn.connect()
                    // TODO: parse response body
                    val ret = conn.inputStream != null
                    if (ret) return@withContext BufferedReader(conn.inputStream.reader()).readText()
                    else return@withContext "Conn failed"
                }
                catch (e: SocketTimeoutException) {
                    // Timeout msg
                    return@withContext "Timeout Exception"
                }
                catch (e: IOException) {
                    // Url not found
                    return@withContext "IO Exception"
                }
            }
        }

        // TODO: pass in/out parameters as array or data class with parser
        suspend fun makeProfileRequest(id: String): String {
            return withContext(Dispatchers.IO) {
                try{
                    val conn: HttpURLConnection = URL("$base_url/do-getProfile/$id").openConnection() as HttpURLConnection
                    conn.requestMethod = "GET"
                    conn.setRequestProperty("Content-Type", "application/json; utf-8")
                    // TODO: get saved session token
                    conn.setRequestProperty("x-access-token", "")
                    conn.connectTimeout = 5000
                    conn.connect()
                    // TODO: parse response body
                    val ret = conn.inputStream != null
                    if (ret) return@withContext BufferedReader(conn.inputStream.reader()).readText()
                    else return@withContext "Conn failed"
                }
                catch (e: SocketTimeoutException) {
                    // Timeout msg
                    return@withContext "Timeout Exception"
                }
                catch (e: IOException) {
                    // Url not found
                    return@withContext "IO Exception"
                }
            }
        }

        // TODO: pass in/out parameters as array or data class with parser
        suspend fun makeCountriesRequest(id: String): String {
            return withContext(Dispatchers.IO) {
                try{
                    val conn: HttpURLConnection = URL("$base_url/do-getCountries").openConnection() as HttpURLConnection
                    conn.requestMethod = "GET"
                    conn.setRequestProperty("Content-Type", "application/json; utf-8")
                    conn.connectTimeout = 5000
                    conn.connect()
                    // TODO: parse response body
                    val ret = conn.inputStream != null
                    if (ret) return@withContext BufferedReader(conn.inputStream.reader()).readText()
                    else return@withContext "Conn failed"
                }
                catch (e: SocketTimeoutException) {
                    // Timeout msg
                    return@withContext "Timeout Exception"
                }
                catch (e: IOException) {
                    // Url not found
                    return@withContext "IO Exception"
                }
            }
        }

        // TODO: pass in/out parameters as array or data class with parser
        suspend fun makeImageRequest(id: String): String {
            return withContext(Dispatchers.IO) {
                try{
                    val conn: HttpURLConnection = URL("$base_url/do-getProfileImage/$id").openConnection() as HttpURLConnection
                    conn.requestMethod = "GET"
                    conn.setRequestProperty("Content-Type", "application/json; utf-8")
                    // TODO: get saved session token
                    conn.setRequestProperty("x-access-token", "")
                    conn.connectTimeout = 5000
                    conn.connect()
                    // TODO: parse response body
                    val ret = conn.inputStream != null
                    if (ret) return@withContext BufferedReader(conn.inputStream.reader()).readText()
                    else return@withContext "Conn failed"
                }
                catch (e: SocketTimeoutException) {
                    // Timeout msg
                    return@withContext "Timeout Exception"
                }
                catch (e: IOException) {
                    // Url not found
                    return@withContext "IO Exception"
                }
            }
        }
    }
}