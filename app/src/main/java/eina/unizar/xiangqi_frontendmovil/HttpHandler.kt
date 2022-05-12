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
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

object HttpHandler {

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
    data class ProfileRequest(val id: Int?)
    data class ProfileResponse(val nickname: String, val realname: String, val birthdate: String,
                               val country: String, val code: String, val ranking: Int,
                               val points: Int, val registerdate: String, val friends: Int,
                               val image: Boolean, val played: Int, val won: Int, val error: Boolean)
    data class ImageRequest(val id: Int?)
    data class ImageResponse(val image: Drawable?, val error: Boolean)
    data class CountriesResponse(val countryList: List<String>, val codeList: List<String>,
                                 val error: Boolean)
    data class EditRequest(val nickname: String, val realname: String, val pwd: String,
                           val birthdate: String, val country: String, val image: Uri?)
    data class EditResponse(val success: Boolean, val error: Boolean)

    private const val base_url = "http://ec2-3-82-235-243.compute-1.amazonaws.com:3000"
    private var token = ""
    var id = -1

    suspend fun makeLoginRequest(request: LoginRequest): LoginResponse {
        return withContext(Dispatchers.IO) {
            try {
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
            try {
                val conn: HttpURLConnection = URL("$base_url/do-create").openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json; utf-8")
                conn.connectTimeout = 5000
                conn.doOutput = true

                var bmpStr = ""
                if (request.image != null) {
                    val bmp = ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, request.image))
                    val stream = ByteArrayOutputStream()
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    bmpStr = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
                }

                val dateList = request.birthdate.split("/")

                conn.outputStream.write(("{\"nickname\":\"${request.nickname}\", " +
                        "\"name\":\"${request.realname}\", " +
                        "\"email\":\"${request.email}\", " +
                        "\"image\":\"${bmpStr}\", " +
                        "\"date\":\"${dateList[2]}-${dateList[1]}-${dateList[0]} 00:00\", " +
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
            try {
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
            try {
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
            try {
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

    suspend fun makeProfileRequest(request: ProfileRequest): ProfileResponse {
        return withContext(Dispatchers.IO) {
            try {
                val conn: HttpURLConnection
                if (request.id == null) conn = URL("$base_url/do-getProfile/$id").openConnection() as HttpURLConnection
                else conn = URL("$base_url/do-getProfile/${request.id}").openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("Content-Type", "application/json; utf-8")
                conn.setRequestProperty("x-access-token", token)
                conn.connectTimeout = 5000
                conn.connect()
                var parser = JSONObject(BufferedReader(conn.inputStream.reader()).readText())
                Log.d("HTTP", parser.toString())
                val profile = parser.getJSONObject("perfil")
                val stats = parser.getJSONObject("estadisticas")
                val response = ProfileResponse(profile.getString("nickname"),
                    profile.getString("name"),
                    profile.getString("birthday"),
                    profile.getJSONObject("country").getString("name"),
                    profile.getJSONObject("country").getString("code"),
                    profile.getInt("range"),
                    profile.getInt("points"),
                    profile.getString("registerDate"),
                    profile.getInt("nFriends"),
                    profile.getBoolean("hasImage"),
                    stats.getInt("totalJugadas"),
                    stats.getInt("totalGanadas"),
                    false)
                return@withContext response
            }
            catch (e: SocketTimeoutException) {
                // Timeout msg
                return@withContext ProfileResponse("", "", "", "",
                    "", 0, 0, "", 0, false, 0,
                    0, true)
            }
            catch (e: IOException) {
                // Url not found
                return@withContext ProfileResponse("", "", "", "",
                    "", 0, 0, "", 0, false, 0,
                    0, true)
            }
        }
    }

    suspend fun makeImageRequest(request: ImageRequest): ImageResponse {
        return withContext(Dispatchers.IO) {
            try {
                val conn: HttpURLConnection
                if (request.id == null) conn = URL("$base_url/do-getProfileImage/$id").openConnection() as HttpURLConnection
                else conn = URL("$base_url/do-getProfileImage/${request.id}").openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("Content-Type", "application/json; utf-8")
                conn.setRequestProperty("x-access-token", token)
                conn.connectTimeout = 5000
                conn.connect()
                return@withContext ImageResponse(Drawable.createFromStream(conn.inputStream, "profile"), false)
            }
            catch (e: SocketTimeoutException) {
                // Timeout msg
                return@withContext ImageResponse(null, true)
            }
            catch (e: IOException) {
                // Url not found
                return@withContext ImageResponse(null, true)
            }
        }
    }

    suspend fun makeCountriesRequest(): CountriesResponse {
        return withContext(Dispatchers.IO) {
            try {
                val conn: HttpURLConnection = URL("$base_url/do-getCountries").openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("Content-Type", "application/json; utf-8")
                conn.connectTimeout = 5000
                conn.connect()
                val parser = JSONArray(BufferedReader(conn.inputStream.reader()).readText())
                val countryList = mutableListOf<String>()
                val codeList = mutableListOf<String>()
                for (i in 0 until parser.length()) {
                    val item = parser.getJSONObject(i)
                    countryList.add(item.getString("name"))
                    codeList.add(item.getString("code"))
                }
                val response = CountriesResponse(countryList.toList(), codeList.toList(), false)
                return@withContext response
            }
            catch (e: SocketTimeoutException) {
                // Timeout msg
                return@withContext CountriesResponse(listOf(), listOf(), true)
            }
            catch (e: IOException) {
                // Url not found
                return@withContext CountriesResponse(listOf(), listOf(), true)
            }
        }
    }

    suspend fun makeEditRequest(request: EditRequest, context: Context): EditResponse {
        return withContext(Dispatchers.IO) {
            try {
                val dateList = request.birthdate.split("/")

                val conn: HttpURLConnection = URL("$base_url/do-changeProfile/${request.nickname}/" +
                        "${request.realname}/${dateList[2]}-${dateList[1]}-${dateList[0]}/${request.country}/${request.pwd}"
                    ).openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json; utf-8")
                conn.setRequestProperty("x-access-token", token)
                conn.connectTimeout = 5000
                conn.doOutput = true

                var bmpStr = ""
                if (request.image != null) {
                    val bmp = ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, request.image))
                    val stream = ByteArrayOutputStream()
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    bmpStr = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
                }

                conn.outputStream.write(("{\"image\":\"${bmpStr}\"}")
                    .toByteArray())
                conn.connect()
                val response = BufferedReader(conn.inputStream.reader()).readText().toBooleanStrict()
                return@withContext EditResponse(response, false)
            }
            catch (e: SocketTimeoutException) {
                // Timeout msg
                return@withContext EditResponse(false, true)
            }
            catch (e: IOException) {
                // Url not found
                return@withContext EditResponse(false, true)
            }
        }
    }
}