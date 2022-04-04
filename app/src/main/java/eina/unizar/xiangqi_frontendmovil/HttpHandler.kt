package eina.unizar.xiangqi_frontendmovil

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class HttpHandler {
    data class LoginRequest(val email: String, val pwd: String)
    data class LoginResponse(val exist: Boolean, val ok: Boolean, val validacion: Boolean,
                             val error: Boolean)
    data class RegisterRequest(val nickname: String, val realname: String, val email: String,
                               val pwd: String, val birthdate: String, val country: String,
                               val code: String, val image: Uri)
    data class RegisterResponse(val success: Boolean, val error: Boolean)

    companion object {
        private const val base_url = "http://ec2-3-82-235-243.compute-1.amazonaws.com:3000"
        private var token = ""
        private var id = -1

        suspend fun makeLoginRequest(request: LoginRequest): LoginResponse {
            return withContext(Dispatchers.IO) {
                try{
                    val conn: HttpURLConnection = URL("$base_url/do-login").openConnection() as HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Content-Type", "application/json; utf-8")
                    conn.connectTimeout = 5000
                    conn.doOutput = true
                    conn.outputStream.write(("{\"email\":\"${request.email}\", " +
                                             "\"pwd\":\"${request.pwd}\"}")
                                            .toByteArray())
                    conn.connect()
                    val parser = JSONObject(BufferedReader(conn.inputStream.reader()).readText())
                    val response = LoginResponse(parser.getBoolean("exist"),
                        parser.getBoolean("ok"),
                        parser.getBoolean("validacion"),
                        false)
                    if (response.exist and response.ok and response.validacion) {
                        token = parser.getString("token")
                        id = parser.getInt("id")
                    }
                    return@withContext response
                }
                catch (e: SocketTimeoutException) {
                    // Timeout msg
                    return@withContext LoginResponse(false, false, false, true)
                }
                catch (e: IOException) {
                    // Url not found
                    return@withContext LoginResponse(false, false, false, true)
                }
            }
        }

        suspend fun makeRegisterRequest(request: RegisterRequest, context: Context): RegisterResponse {
            return withContext(Dispatchers.IO) {
                try{
                    val conn: HttpURLConnection = URL("$base_url/do-create").openConnection() as HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Content-Type", "application/json; utf-8")
                    conn.connectTimeout = 5000
                    conn.doOutput = true

                    //val bmp = Bitmap.createBitmap(IntArray(4), 2, 2, Bitmap.Config.ARGB_8888)
                    val bmp = ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, request.image))
                    val stream = ByteArrayOutputStream()
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val bmpStr = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)

                    val dateList = request.birthdate.split("/")

                    conn.outputStream.write(("{\"nickname\":\"${request.nickname}\", " +
                            "\"name\":\"${request.realname}\", " +
                            "\"email\":\"${request.email}\", " +
                            "\"image\":\"${bmpStr}\", " +
                            "\"date\":\"${dateList[2]}-${dateList[0]}-${dateList[1]} 00:00\", " +
                            "\"country\":{\"code\":\"${request.code}\", \"name\":\"${request.country}\"}, " +
                            "\"pwd\":\"${request.pwd}\"}")
                            .toByteArray())
                    conn.connect()
                    val response = BufferedReader(conn.inputStream.reader()).readText().toBooleanStrict()
                    return@withContext RegisterResponse(response, false)
                }
                catch (e: SocketTimeoutException) {
                    // Timeout msg
                    return@withContext RegisterResponse(false, true)
                }
                catch (e: IOException) {
                    // Url not found
                    return@withContext RegisterResponse(false, true)
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