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
                               val image: Boolean, val played: Int, val won: Int, val dailyPlayed: List<Int>,
                               val dailyWon: List<Int>, val error: Boolean)
    data class ImageRequest(val id: Int?)
    data class ImageResponse(val image: Drawable?, val error: Boolean)
    data class CountriesResponse(val countryList: List<String>, val codeList: List<String>,
                                 val error: Boolean)
    data class EditRequest(val nickname: String, val realname: String, val pwd: String,
                           val birthdate: String, val country: String, val image: Uri?)
    data class EditResponse(val success: Boolean, val error: Boolean)
    data class GamesRequest(val id: Int?)
    data class GamesResponse(val ids: List<Int>, val nicknames: List<String>, val countries: List<String>,
                             val codes: List<String>, val startDates: List<String>, val lastMovDates: List<String>,
                             val myTurns: List<Boolean>, val hasImages: List<Boolean>, val error: Boolean)
    data class PointsResponse(val points: Int, val error: Boolean)
    data class StoreResponse(val ids: List<Int>, val types: List<Int>, val names: List<String>,
                             val descs: List<String>, val cats: List<String>, val prices: List<Int>,
                             val purchased: List<Boolean>, val error: Boolean)
    data class PurchaseRequest(val id: Int, val type: Int, val price: Int)
    data class PurchaseResponse(val success: Boolean, val error: Boolean)
    data class RankingResponse(val ids: List<Int>, val positions: List<Int>, val nicknames: List<String>,
                               val codes: List<String>, val countries: List<String>, val won: List<Int>,
                               val played: List<Int>, val hasImages: List<Boolean>, val error: Boolean)
    data class SearchRequest(val nickname: String)
    data class SearchResponse(val ids: List<Int>, val nicknames: List<String>, val realnames: List<String>,
                              val codes: List<String>, val countries: List<String>, val birthdates: List<String>,
                              val sents: List<Boolean>, val hasImages: List<Boolean>, val error: Boolean)
    data class FriendRequestsResponse(val ids: List<Int>, val nicknames: List<String>, val realnames: List<String>,
                                      val codes: List<String>, val countries: List<String>, val birthdates: List<String>,
                                      val hasImages: List<Boolean>, val error: Boolean)
    data class FriendsResponse(val ids: List<Int>, val nicknames: List<String>, val realnames: List<String>,
                               val codes: List<String>, val countries: List<String>, val hasImages: List<Boolean>,
                               val error: Boolean)
    data class AcceptRequest(val id: Int)
    data class AcceptResponse(val success: Boolean, val error: Boolean)
    data class RejectRequest(val id: Int)
    data class RejectResponse(val success: Boolean, val error: Boolean)
    data class HistoryResponse(val success: Boolean, val error: Boolean)

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
                val parser = JSONObject(BufferedReader(conn.inputStream.reader()).readText())
                val profile = parser.getJSONObject("perfil")
                val stats = parser.getJSONObject("estadisticas")
                val playedArray = stats.getJSONArray("diaJugadas")
                val wonArray = stats.getJSONArray("diaGanadas")
                val dailyPlayed = mutableListOf<Int>()
                val dailyWon = mutableListOf<Int>()
                for (i in 0 until playedArray.length()) {
                    dailyPlayed.add(playedArray.getInt(i))
                    dailyWon.add(wonArray.getInt(i))
                }
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
                    dailyPlayed.toList(),
                    dailyWon.toList(),
                    false)
                return@withContext response
            }
            catch (e: SocketTimeoutException) {
                // Timeout msg
                return@withContext ProfileResponse("", "", "", "",
                    "", 0, 0, "", 0, false, 0,
                    0, listOf(), listOf(), true)
            }
            catch (e: IOException) {
                // Url not found
                return@withContext ProfileResponse("", "", "", "",
                    "", 0, 0, "", 0, false, 0,
                    0, listOf(), listOf(), true)
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

    suspend fun makeGamesRequest(request: GamesRequest): GamesResponse {
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
                val parser = JSONObject(BufferedReader(conn.inputStream.reader()).readText())
                val games = parser.getJSONArray("partidas")
                val ids = mutableListOf<Int>()
                val nicknames = mutableListOf<String>()
                val countries = mutableListOf<String>()
                val codes = mutableListOf<String>()
                val startDates = mutableListOf<String>()
                val lastMovDates = mutableListOf<String>()
                val myTurns = mutableListOf<Boolean>()
                val hasImages = mutableListOf<Boolean>()
                for (i in 0 until games.length()) {
                    val item: JSONObject = games.getJSONObject(i)
                    ids.add(item.getInt("id"))
                    nicknames.add(item.getString("nickname"))
                    countries.add(item.getString("country"))
                    codes.add(item.getString("flag").subSequence(5, 7).toString().uppercase())
                    startDates.add(item.getString("startDate"))
                    lastMovDates.add(item.getString("lastMovDate"))
                    myTurns.add(item.getBoolean("myTurn"))
                    hasImages.add(item.getBoolean("hasImage"))
                }
                val response = GamesResponse(
                    ids.toList(),
                    nicknames.toList(),
                    countries.toList(),
                    codes.toList(),
                    startDates.toList(),
                    lastMovDates.toList(),
                    myTurns.toList(),
                    hasImages.toList(),
                    false)
                return@withContext response
            }
            catch (e: SocketTimeoutException) {
                // Timeout msg
                return@withContext GamesResponse(listOf(), listOf(), listOf(), listOf(), listOf(),
                    listOf(), listOf(), listOf(), true)
            }
            catch (e: IOException) {
                // Url not found
                return@withContext GamesResponse(listOf(), listOf(), listOf(), listOf(), listOf(),
                    listOf(), listOf(), listOf(), true)
            }
        }
    }

    suspend fun makePointsRequest(): PointsResponse {
        return withContext(Dispatchers.IO) {
            try {
                val conn: HttpURLConnection = URL("$base_url/do-getPoints").openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("Content-Type", "application/json; utf-8")
                conn.setRequestProperty("x-access-token", token)
                conn.connectTimeout = 5000
                conn.connect()
                val response = BufferedReader(conn.inputStream.reader()).readText().toInt()
                return@withContext PointsResponse(response, false)
            }
            catch (e: SocketTimeoutException) {
                // Timeout msg
                return@withContext PointsResponse(0, true)
            }
            catch (e: IOException) {
                // Url not found
                return@withContext PointsResponse(0, true)
            }
        }
    }

    suspend fun makeStoreRequest(): StoreResponse {
        return withContext(Dispatchers.IO) {
            try {
                val conn: HttpURLConnection = URL("$base_url/do-getStoreItems").openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("Content-Type", "application/json; utf-8")
                conn.setRequestProperty("x-access-token", token)
                conn.connectTimeout = 5000
                conn.connect()
                val parser = JSONObject(BufferedReader(conn.inputStream.reader()).readText())
                val boards = parser.getJSONArray("setsBoards")
                val pieces = parser.getJSONArray("setsPieces")
                val ids = mutableListOf<Int>()
                val types = mutableListOf<Int>()
                val names = mutableListOf<String>()
                val descs = mutableListOf<String>()
                val cats = mutableListOf<String>()
                val prices = mutableListOf<Int>()
                val purchased = mutableListOf<Boolean>()
                for (i in 0 until boards.length()) {
                    val item: JSONObject = boards.getJSONObject(i)
                    ids.add(item.getInt("id"))
                    types.add(item.getInt("tipo"))
                    names.add(item.getString("name"))
                    descs.add(item.getString("desc"))
                    cats.add(item.getString("category"))
                    prices.add(item.getInt("price"))
                    purchased.add(item.getBoolean("purchased"))
                }
                for (i in 0 until pieces.length()) {
                    val item: JSONObject = pieces.getJSONObject(i)
                    ids.add(item.getInt("id"))
                    types.add(item.getInt("tipo"))
                    names.add(item.getString("name"))
                    descs.add(item.getString("desc"))
                    cats.add(item.getString("category"))
                    prices.add(item.getInt("price"))
                    purchased.add(item.getBoolean("purchased"))
                }
                val response = StoreResponse(
                    ids.toList(),
                    types.toList(),
                    names.toList(),
                    descs.toList(),
                    cats.toList(),
                    prices.toList(),
                    purchased.toList(),
                    false
                )
                return@withContext response
            }
            catch (e: SocketTimeoutException) {
                // Timeout msg
                return@withContext StoreResponse(listOf(), listOf(), listOf(), listOf(), listOf(),
                    listOf(), listOf(), true)
            }
            catch (e: IOException) {
                // Url not found
                return@withContext StoreResponse(listOf(), listOf(), listOf(), listOf(), listOf(),
                    listOf(), listOf(), true)
            }
        }
    }

    suspend fun makePurchaseRequest(request: PurchaseRequest): PurchaseResponse {
        return withContext(Dispatchers.IO) {
            try {
                val conn: HttpURLConnection = URL("$base_url/do-purchaseItem").openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json; utf-8")
                conn.setRequestProperty("x-access-token", token)
                conn.connectTimeout = 5000
                conn.doOutput = true
                conn.outputStream.write(("{\"id\":\"${request.id}\", " +
                        "\"tipo\":\"${request.type}\", " +
                        "\"price\":\"${request.price}\"}")
                    .toByteArray())
                conn.connect()
                val response = BufferedReader(conn.inputStream.reader()).readText().toBooleanStrict()
                return@withContext PurchaseResponse(response, false)
            }
            catch (e: SocketTimeoutException) {
                // Timeout msg
                return@withContext PurchaseResponse(false, true)
            }
            catch (e: IOException) {
                // Url not found
                return@withContext PurchaseResponse(false, true)
            }
        }
    }

    suspend fun makeRankingRequest(): RankingResponse {
        return withContext(Dispatchers.IO) {
            try {
                val conn: HttpURLConnection = URL("$base_url/do-getRanking").openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("Content-Type", "application/json; utf-8")
                conn.connectTimeout = 5000
                conn.connect()
                val parser = JSONArray(BufferedReader(conn.inputStream.reader()).readText())
                val ids = mutableListOf<Int>()
                val positions = mutableListOf<Int>()
                val nicknames = mutableListOf<String>()
                val codes = mutableListOf<String>()
                val countries = mutableListOf<String>()
                val won = mutableListOf<Int>()
                val played = mutableListOf<Int>()
                val hasImages = mutableListOf<Boolean>()
                for (i in 0 until parser.length()) {
                    val item: JSONObject = parser.getJSONObject(i)
                    ids.add(item.getInt("id"))
                    positions.add(item.getInt("position"))
                    nicknames.add(item.getString("nickname"))
                    codes.add(item.getString("flag").subSequence(5, 7).toString().uppercase())
                    countries.add(item.getString("country"))
                    won.add(item.getInt("winnedGames"))
                    played.add(item.getInt("playedGames"))
                    hasImages.add(item.getBoolean("hasImage"))
                }
                val response = RankingResponse(
                    ids.toList(),
                    positions.toList(),
                    nicknames.toList(),
                    codes.toList(),
                    countries.toList(),
                    won.toList(),
                    played.toList(),
                    hasImages.toList(),
                    false
                )
                return@withContext response
            }
            catch (e: SocketTimeoutException) {
                // Timeout msg
                return@withContext RankingResponse(listOf(), listOf(), listOf(), listOf(), listOf(),
                    listOf(), listOf(), listOf(), true)
            }
            catch (e: IOException) {
                // Url not found
                return@withContext RankingResponse(listOf(), listOf(), listOf(), listOf(), listOf(),
                    listOf(), listOf(), listOf(), true)
            }
        }
    }

    suspend fun makeSearchRequest(request: SearchRequest): SearchResponse {
        return withContext(Dispatchers.IO) {
            try {
                val conn: HttpURLConnection = URL("$base_url/do-searchUsers").openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json; utf-8")
                conn.setRequestProperty("x-access-token", token)
                conn.connectTimeout = 5000
                conn.doOutput = true
                conn.outputStream.write(("{\"nickname\":\"${request.nickname}\"}")
                    .toByteArray())
                conn.connect()
                val parser = JSONArray(BufferedReader(conn.inputStream.reader()).readText())
                val ids = mutableListOf<Int>()
                val nicknames = mutableListOf<String>()
                val realnames = mutableListOf<String>()
                val codes = mutableListOf<String>()
                val countries = mutableListOf<String>()
                val birthdates = mutableListOf<String>()
                val sents = mutableListOf<Boolean>()
                val hasImages = mutableListOf<Boolean>()
                for (i in 0 until parser.length()) {
                    val item: JSONObject = parser.getJSONObject(i)
                    ids.add(item.getInt("id"))
                    nicknames.add(item.getString("nickname"))
                    realnames.add(item.getString("name"))
                    codes.add(item.getString("flag").subSequence(5, 7).toString().uppercase())
                    countries.add(item.getString("country"))
                    birthdates.add(item.getString("birthday"))
                    sents.add(item.getBoolean("sended"))
                    hasImages.add(item.getBoolean("hasImage"))
                }
                val response = SearchResponse(
                    ids.toList(),
                    nicknames.toList(),
                    realnames.toList(),
                    codes.toList(),
                    countries.toList(),
                    birthdates.toList(),
                    sents.toList(),
                    hasImages.toList(),
                    false
                )
                return@withContext response
            }
            catch (e: SocketTimeoutException) {
                // Timeout msg
                return@withContext SearchResponse(listOf(), listOf(), listOf(), listOf(), listOf(),
                    listOf(), listOf(), listOf(), true)
            }
            catch (e: IOException) {
                // Url not found
                return@withContext SearchResponse(listOf(), listOf(), listOf(), listOf(), listOf(),
                    listOf(), listOf(), listOf(), true)
            }
        }
    }

    suspend fun makeFriendRequestsRequest(): FriendRequestsResponse {
        return withContext(Dispatchers.IO) {
            try {
                val conn: HttpURLConnection = URL("$base_url/do-getFriendRequests").openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("Content-Type", "application/json; utf-8")
                conn.setRequestProperty("x-access-token", token)
                conn.connectTimeout = 5000
                conn.connect()
                val parser = JSONArray(BufferedReader(conn.inputStream.reader()).readText())
                val ids = mutableListOf<Int>()
                val nicknames = mutableListOf<String>()
                val realnames = mutableListOf<String>()
                val codes = mutableListOf<String>()
                val countries = mutableListOf<String>()
                val birthdates = mutableListOf<String>()
                val hasImages = mutableListOf<Boolean>()
                for (i in 0 until parser.length()) {
                    val item: JSONObject = parser.getJSONObject(i)
                    ids.add(item.getInt("id"))
                    nicknames.add(item.getString("nickname"))
                    realnames.add(item.getString("name"))
                    codes.add(item.getString("flag").subSequence(5, 7).toString().uppercase())
                    countries.add(item.getString("country"))
                    birthdates.add(item.getString("birthday"))
                    hasImages.add(item.getBoolean("hasImage"))
                }
                val response = FriendRequestsResponse(
                    ids.toList(),
                    nicknames.toList(),
                    realnames.toList(),
                    codes.toList(),
                    countries.toList(),
                    birthdates.toList(),
                    hasImages.toList(),
                    false
                )
                return@withContext response
            }
            catch (e: SocketTimeoutException) {
                // Timeout msg
                return@withContext FriendRequestsResponse(listOf(), listOf(), listOf(), listOf(), listOf(),
                    listOf(), listOf(), true)
            }
            catch (e: IOException) {
                // Url not found
                return@withContext FriendRequestsResponse(listOf(), listOf(), listOf(), listOf(), listOf(),
                    listOf(), listOf(), true)
            }
        }
    }

    suspend fun makeFriendsRequest(): FriendsResponse {
        return withContext(Dispatchers.IO) {
            try {
                val conn: HttpURLConnection = URL("$base_url/do-getFriends").openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("Content-Type", "application/json; utf-8")
                conn.setRequestProperty("x-access-token", token)
                conn.connectTimeout = 5000
                conn.connect()
                val parser = JSONArray(BufferedReader(conn.inputStream.reader()).readText())
                val ids = mutableListOf<Int>()
                val nicknames = mutableListOf<String>()
                val realnames = mutableListOf<String>()
                val codes = mutableListOf<String>()
                val countries = mutableListOf<String>()
                val hasImages = mutableListOf<Boolean>()
                for (i in 0 until parser.length()) {
                    val item: JSONObject = parser.getJSONObject(i)
                    ids.add(item.getInt("id"))
                    nicknames.add(item.getString("nickname"))
                    realnames.add(item.getString("name"))
                    codes.add(item.getString("flag").subSequence(5, 7).toString().uppercase())
                    countries.add(item.getString("country"))
                    hasImages.add(item.getBoolean("hasImage"))
                }
                val response = FriendsResponse(
                    ids.toList(),
                    nicknames.toList(),
                    realnames.toList(),
                    codes.toList(),
                    countries.toList(),
                    hasImages.toList(),
                    false
                )
                return@withContext response
            }
            catch (e: SocketTimeoutException) {
                // Timeout msg
                return@withContext FriendsResponse(listOf(), listOf(), listOf(), listOf(), listOf(),
                    listOf(), true)
            }
            catch (e: IOException) {
                // Url not found
                return@withContext FriendsResponse(listOf(), listOf(), listOf(), listOf(), listOf(),
                    listOf(), true)
            }
        }
    }

    suspend fun makeAcceptRequest(request: AcceptRequest): AcceptResponse {
        return withContext(Dispatchers.IO) {
            try {
                val conn: HttpURLConnection = URL("$base_url/do-acceptRequest").openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json; utf-8")
                conn.setRequestProperty("x-access-token", token)
                conn.connectTimeout = 5000
                conn.doOutput = true
                conn.outputStream.write(("{\"id\":\"${request.id}\"}")
                    .toByteArray())
                conn.connect()
                val response = BufferedReader(conn.inputStream.reader()).readText().toBooleanStrict()
                return@withContext AcceptResponse(response, false)
            }
            catch (e: SocketTimeoutException) {
                // Timeout msg
                return@withContext AcceptResponse(false, true)
            }
            catch (e: IOException) {
                // Url not found
                return@withContext AcceptResponse(false, true)
            }
        }
    }

    suspend fun makeRejectRequest(request: RejectRequest): RejectResponse {
        return withContext(Dispatchers.IO) {
            try {
                val conn: HttpURLConnection = URL("$base_url/do-rejectRequest").openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json; utf-8")
                conn.setRequestProperty("x-access-token", token)
                conn.connectTimeout = 5000
                conn.doOutput = true
                conn.outputStream.write(("{\"id\":\"${request.id}\"}")
                    .toByteArray())
                conn.connect()
                val response = BufferedReader(conn.inputStream.reader()).readText().toBooleanStrict()
                return@withContext RejectResponse(response, false)
            }
            catch (e: SocketTimeoutException) {
                // Timeout msg
                return@withContext RejectResponse(false, true)
            }
            catch (e: IOException) {
                // Url not found
                return@withContext RejectResponse(false, true)
            }
        }
    }

    // TODO: request placeholder
    suspend fun makeHistoryRequest(): HistoryResponse {
        return withContext(Dispatchers.IO) {
            try {
                val conn: HttpURLConnection = URL("$base_url/do-getHistorial").openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("Content-Type", "application/json; utf-8")
                conn.setRequestProperty("x-access-token", token)
                conn.connectTimeout = 5000
                conn.connect()
                val parser = JSONArray(BufferedReader(conn.inputStream.reader()).readText())
                Log.d("HTTP", parser.toString())
                return@withContext HistoryResponse(true, false)
            }
            catch (e: SocketTimeoutException) {
                // Timeout msg
                return@withContext HistoryResponse(false, true)
            }
            catch (e: IOException) {
                // Url not found
                return@withContext HistoryResponse(false, true)
            }
        }
    }

    // TODO: request placeholder
    suspend fun makeSkinRequest(): HistoryResponse {
        return withContext(Dispatchers.IO) {
            try {
                val conn: HttpURLConnection = URL("$base_url/do-getUserSkinList").openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("Content-Type", "application/json; utf-8")
                conn.setRequestProperty("x-access-token", token)
                conn.connectTimeout = 5000
                conn.connect()
                val parser = JSONArray(BufferedReader(conn.inputStream.reader()).readText())
                Log.d("HTTP", parser.toString())
                return@withContext HistoryResponse(true, false)
            }
            catch (e: SocketTimeoutException) {
                // Timeout msg
                return@withContext HistoryResponse(false, true)
            }
            catch (e: IOException) {
                // Url not found
                return@withContext HistoryResponse(false, true)
            }
        }
    }
}