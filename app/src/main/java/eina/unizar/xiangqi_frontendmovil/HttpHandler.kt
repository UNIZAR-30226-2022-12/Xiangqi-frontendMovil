package eina.unizar.xiangqi_frontendmovil

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Base64
import android.util.Log
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
                               val code: String, val image: Uri?)
    data class RegisterResponse(val success: Boolean, val error: Boolean)
    data class ForgottenPassRequest(val email: String)
    data class ForgottenPassResponse(val success: Boolean, val error: Boolean)
    data class ValidationRequest(val email: String)
    data class ValidationResponse(val success: Boolean, val error: Boolean)
    data class DeletionResponse(val success: Boolean, val error: Boolean)
    data class ProfileResponse(val nickname: String, val realname: String, val birthdate: String,
                               val country: String, val error: Boolean)

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

                    val bmp: Bitmap
                    if (request.image == null) bmp = Bitmap.createBitmap(IntArray(4), 2, 2, Bitmap.Config.ARGB_8888)
                    else bmp = ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, request.image))
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

        suspend fun makeForgottenPassRequest(request: ForgottenPassRequest): ForgottenPassResponse {
            return withContext(Dispatchers.IO) {
                try{
                    val conn: HttpURLConnection = URL("$base_url/do-forgotPwd").openConnection() as HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Content-Type", "application/json; utf-8")
                    conn.connectTimeout = 5000
                    conn.doOutput = true
                    conn.outputStream.write(("{\"email\":\"${request.email}\"}").toByteArray())
                    conn.connect()
                    val response = BufferedReader(conn.inputStream.reader()).readText().toBooleanStrict()
                    return@withContext ForgottenPassResponse(response, false)
                }
                catch (e: SocketTimeoutException) {
                    // Timeout msg
                    return@withContext ForgottenPassResponse(false, true)
                }
                catch (e: IOException) {
                    // Url not found
                    return@withContext ForgottenPassResponse(false, true)
                }
            }
        }

        suspend fun makeValidationRequest(request: ValidationRequest): ValidationResponse {
            return withContext(Dispatchers.IO) {
                try{
                    val conn: HttpURLConnection = URL("$base_url/do-validate").openConnection() as HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Content-Type", "application/json; utf-8")
                    conn.connectTimeout = 5000
                    conn.doOutput = true
                    conn.outputStream.write(("{\"email\":\"${request.email}\"}").toByteArray())
                    conn.connect()
                    val response = BufferedReader(conn.inputStream.reader()).readText().toBooleanStrict()
                    return@withContext ValidationResponse(response, false)
                }
                catch (e: SocketTimeoutException) {
                    // Timeout msg
                    return@withContext ValidationResponse(false, true)
                }
                catch (e: IOException) {
                    // Url not found
                    return@withContext ValidationResponse(false, true)
                }
            }
        }

        suspend fun makeDeletionRequest(): DeletionResponse {
            return withContext(Dispatchers.IO) {
                try{
                    val conn: HttpURLConnection = URL("$base_url/do-deleteAccount").openConnection() as HttpURLConnection
                    conn.requestMethod = "GET"
                    conn.setRequestProperty("Content-Type", "application/json; utf-8")
                    conn.setRequestProperty("x-access-token", token)
                    conn.connectTimeout = 5000
                    conn.connect()
                    val response = BufferedReader(conn.inputStream.reader()).readText().toBooleanStrict()
                    return@withContext DeletionResponse(response, false)
                }
                catch (e: SocketTimeoutException) {
                    // Timeout msg
                    return@withContext DeletionResponse(false, true)
                }
                catch (e: IOException) {
                    // Url not found
                    return@withContext DeletionResponse(false, true)
                }
            }
        }

        // TODO: pass in/out parameters as array or data class with parser
        suspend fun makeProfileRequest(user: Int): ProfileResponse {
            return withContext(Dispatchers.IO) {
                try{
                    val conn: HttpURLConnection
                    if (user == -1) conn = URL("$base_url/do-getProfile/$id").openConnection() as HttpURLConnection
                    else conn = URL("$base_url/do-getProfile/$user").openConnection() as HttpURLConnection
                    conn.requestMethod = "GET"
                    conn.setRequestProperty("Content-Type", "application/json; utf-8")
                    conn.setRequestProperty("x-access-token", token)
                    conn.connectTimeout = 5000
                    conn.connect()
                    val parser = JSONObject(BufferedReader(conn.inputStream.reader()).readText()).getJSONObject("perfil")
                    val response = ProfileResponse(parser.getString("nickname"),
                        parser.getString("name"),
                        parser.getString("birthday"),
                        parser.getJSONObject("country").getString("name"),
                        false)
                    return@withContext response
                }
                catch (e: SocketTimeoutException) {
                    // Timeout msg
                    return@withContext ProfileResponse("", "", "", "", true)
                }
                catch (e: IOException) {
                    // Url not found
                    return@withContext ProfileResponse("", "", "", "", true)
                }
            }
        }

        // TODO: pass in/out parameters as array or data class with parser
        suspend fun makeImageRequest(user: Int): Drawable? {
            return withContext(Dispatchers.IO) {
                try{
                    val conn: HttpURLConnection
                    if (user == -1) conn = URL("$base_url/do-getProfileImage/$id").openConnection() as HttpURLConnection
                    else conn = URL("$base_url/do-getProfileImage/$user").openConnection() as HttpURLConnection
                    conn.requestMethod = "GET"
                    conn.setRequestProperty("Content-Type", "application/json; utf-8")
                    conn.setRequestProperty("x-access-token", token)
                    conn.connectTimeout = 5000
                    conn.connect()
                    val d = Drawable.createFromStream(conn.inputStream, "profile")
                    return@withContext d
                }
                catch (e: SocketTimeoutException) {
                    // Timeout msg
                    return@withContext null
                }
                catch (e: IOException) {
                    // Url not found
                    return@withContext null
                }
            }
        }

        // TODO: pass in/out parameters as array or data class with parser
        suspend fun makeCountriesRequest(): String {
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
    }
}