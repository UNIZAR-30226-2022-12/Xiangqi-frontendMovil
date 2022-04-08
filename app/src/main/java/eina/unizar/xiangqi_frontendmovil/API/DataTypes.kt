package eina.unizar.xiangqi_frontendmovil.API

import com.google.gson.annotations.SerializedName
import java.util.*

data class LoginRequest (
    @SerializedName("email")
    var email: String,

    @SerializedName("pwd")
    var password: String
    )

data class LoginResponse (
    @SerializedName("exist")
    var ex : Boolean,

    @SerializedName("ok")
    var ok : Boolean,

    @SerializedName("token")
    var token : String,

    @SerializedName("id")
    var id: Int
)




data class Country(
    @SerializedName("code")
    var code : String,

    @SerializedName("name")
    var name : String
)
data class RegisterRequest(
    @SerializedName("nickname")
    var nick : String,

    @SerializedName("name")
    var name: String,

    @SerializedName("email")
    var email : String,

    @SerializedName("date")
    var date : String,

    @SerializedName("country")
    var country : Country,

    @SerializedName("pwd")
    var pwd: String
)

data class ValidateRequest(
    @SerializedName("email")
    var email : String
)

data class ProfileResponse(
    @SerializedName("email")
    var email : String
    )


data class CountriesResponse(
    var vector: Vector<Country>
)

data class ImageResponse(
    @SerializedName("email")
    var email : String
)